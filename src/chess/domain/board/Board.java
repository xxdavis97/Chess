package chess.domain.board;

import chess.constants.ColumnLetters;
import chess.domain.enums.GameResult;
import chess.domain.enums.PieceColor;
import chess.domain.pieces.*;
import chess.utils.BoardPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static chess.domain.enums.PieceColor.EMPTY;
import static chess.utils.BoardPosition.getPositionFromCoords;
import static chess.utils.BoardPosition.isPositionOutOfBounds;

public class Board {
    //checkmate harder, would prefer not to have to look at the isInCheck scan for every possible user move
    // (that would brute force to if always true then its mate)

    private final Piece[][] state;
    private final CastlingHandler castlingHandler;
    private Map<String, Integer> previousBoardStateMap;
    private PieceColor kingInCheck;
    private PieceColor winner;

    public Board() {
        BoardInitializer boardInitializer = new BoardInitializer();
        this.state = boardInitializer.initializeBoard();
        this.castlingHandler = new CastlingHandler();
        this.previousBoardStateMap = new HashMap<>();
        this.kingInCheck = EMPTY;
    }

    public Board(Piece[][] state) {
        this.state = new Piece[8][8];
        System.arraycopy(state, 0, this.state, 0, state.length);
        this.castlingHandler = new CastlingHandler();
        this.kingInCheck = EMPTY;
    }

    public Piece[][] getState() {
        return state;
    }

    public GameResult movePiece(int[] currPosition, int[] newPosition) throws Exception {
        /*
        Moves a piece in the board.. returns true if checkmate occurs
         */
        if (isPositionOutOfBounds(currPosition) || isPositionOutOfBounds(newPosition)) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        Piece p = this.getPieceAtPosition(currPosition);
        boolean isPieceCastlingKing = this.castlingHandler.isCastlingKing(p, currPosition, newPosition, this);
        // castling king (can't castle in check)
        if (isPieceCastlingKing && p.getColor() != this.kingInCheck) {
            this.setKingRookMoved(p);
            this.castlingHandler.castle(p, currPosition, newPosition, this);
        } else {
            boolean legalMove = p.isMoveLegal(currPosition, newPosition, this);
            if (!legalMove) {
                throw new RuntimeException("Inputted move is illegal");
            }
            //keep track of values as enhancement
            this.setKingRookMoved(p);
            this.swapPiecePosition(p, currPosition, newPosition);
            this.handleEnPassant(p);
            this.toggleEnPassant(p);
        }
        return this.updateGameState(p);
    }

    private void handleEnPassant(Piece p) {
        /*
        Captures the en passant'd piece
         */
        if (p instanceof Pawn && ((Pawn) p).isPerformedEnpassant()) {
            int[] position = ((Pawn) p).getEnPassantCapturePosition();
            this.setPieceAtPosition(new Piece(), position);
            ((Pawn) p).setPerformedEnpassant(false);
        }
    }

    public void swapPiecePosition(Piece p, int[] currPosition, int[] newPosition) {
        /*
        Swaps a piece to replace the old position with the new position
         */
        this.state[currPosition[0]][currPosition[1]] = new Piece();
        this.state[newPosition[0]][newPosition[1]] = p;
    }

    public boolean isEmptySquare(int col, int row) {
        /*
        Checks if a given square on the board is empty
         */
        int[] position = getPositionFromCoords(col, row);
        return this.getPieceAtPosition(position).getColor() == EMPTY;
    }

    public Piece getPieceAtPosition(int[] position) {
        /*
        Returns the piece at a position on the board
         */
        if (isPositionOutOfBounds(position)) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        return this.state[position[0]][position[1]];
    }

    private GameResult updateGameState(Piece p) throws Exception {
        /*
        Following the moving of a piece, update the game state to check for checks, checkmates and draws
         */
        PieceColor opposingPieceColor = PieceColor.getInverseColor(p.getColor());
        if (this.opposingKingInCheck(p.getColor())) {
            this.kingInCheck = opposingPieceColor;
            boolean checkmate = this.colorHasNoLegalMoves(opposingPieceColor);
            if (checkmate) {
                this.winner = p.getColor();
                return GameResult.CHECKMATE;
            }
        } else {
            this.kingInCheck = EMPTY;
        }
        boolean drawByRepetition = this.handleDrawByRepetition();
        if (drawByRepetition) {
            this.winner = EMPTY;
            return GameResult.DRAW_BY_REPETITION;
        }
        boolean stalemate = this.colorHasNoLegalMoves(PieceColor.getInverseColor(p.getColor()));
        if (stalemate) {
            this.winner = EMPTY;
            return GameResult.STALEMATE;
        }
        return GameResult.CONTINUE;
    }

    private void toggleEnPassant(Piece p) {
        /*
        After a move is made on the board, all pawns of the same color except the one that was moved (if one was moved)
        are no longer en passantable
         */
        for (Piece[] pieces : this.state) {
            for (Piece piece : pieces) {
                if (!piece.equals(p) && piece instanceof Pawn && piece.getColor().equals(p.getColor())) {
                    ((Pawn) piece).setCanEnpassant(false);
                }
            }
        }
    }

    public boolean isPawnPromotion(int[] position) {
        /*
        Checks to see if a pawn is in a promotion square following a piece move
         */
        Piece p = this.getPieceAtPosition(position);
        return p instanceof Pawn && (p.getColor().equals(PieceColor.WHITE) && position[1] == 7
                || p.getColor().equals(PieceColor.BLACK) && position[1] == 0);
    }

    public GameResult handlePawnPromotion(String promotionSelection, int[] position) throws Exception {
        /*
        After a move is made on the board, check if the pawn hit the opposite end of the board and
        prompt the user for the promotion decision, then replace the piece
         */
        Piece p = this.promotionSelectionToPiece(promotionSelection, position);
        this.setPieceAtPosition(p, position);
        return this.updateGameState(p);
    }

    private Piece promotionSelectionToPiece(String promotionSelection, int[] position) {
        Piece p = this.getPieceAtPosition(position);
        switch (promotionSelection) {
            case "Q" -> p = new Queen(p.getColor());
            case "R" -> p = new Rook(p.getColor());
            case "N" -> p = new Knight(p.getColor());
            case "B" -> p = new Bishop(p.getColor());
            default -> throw new RuntimeException("This was not a valid choice, please select Q, R, N or B");
        }
        return p;
    }

    private boolean opposingKingInCheck(PieceColor movedPieceColor) throws Exception {
        /*
        Given a color, checks if the other color's king is in check
         */
        PieceColor oppColor = PieceColor.getInverseColor(movedPieceColor);
        int[] oppKingPos = this.getKingPositionForColor(oppColor);
        King oppKing = (King) this.getPieceAtPosition(oppKingPos);
        // maybe for a noise?
        return oppKing.isInCheck(oppKingPos, this);
    }

    private boolean colorHasNoLegalMoves(PieceColor color) throws Exception {
        /*
        Checks if the color passed has legal moves (has been checkmated if in check or stalemated if not in check)
         */
        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[i].length; j++) {
                int[] currPos = BoardPosition.getPositionFromCoords(i, j);
                Piece piece = this.getPieceAtPosition(currPos);
                if (piece.getColor() == color) {
                    int col = 0;
                    while (col < 8) {
                        int row = 0;
                        while (row < 8) {
                            int[] newPos = BoardPosition.getPositionFromCoords(col, row);
                            Piece pieceAtNewPos = this.getPieceAtPosition(newPos);
                            if (pieceAtNewPos.getColor() == piece.getColor()) {
                                row++;
                                continue;
                            }
                            //currPos can't equal newPos
                            if (!(col == i && row == j) && piece.isMoveLegal(currPos, newPos, this)) {
                                return false;
                            }
                            row++;
                        }
                        col++;
                    }
                }

            }
        }
        return true;
    }

    private void setKingRookMoved(Piece p) {
        /*
        Sets the value of King or Rook to hasMoved to show can't castle
         */
        if (p instanceof King) {
            ((King) p).setHasMoved(true);
        }
        if (p instanceof Rook) {
            ((Rook) p).setHasMoved(true);
        }
    }

    public boolean isInCheck(PieceColor colorInCheck) throws Exception {
        /*
        Checks if the king of a given color is in check
         */
        int[] kingPosition = this.getKingPositionForColor(colorInCheck);
        King kingInCheck = (King) this.getPieceAtPosition(kingPosition);
        return kingInCheck.isInCheck(kingPosition, this);
    }

    public void setPieceAtPosition(Piece p, int[] position) {
        /*
        Sets a given piece to be at a specified position on the board regardless of chess rules
         */
        this.state[position[0]][position[1]] = p;
    }

    private int[] getKingPositionForColor(PieceColor color) throws Exception {
        /*
        Gets the position of the king for a given color
         */
        int col = 0;
        while (col < 8) {
            int row = 0;
            while (row < 8) {
                int[] position = getPositionFromCoords(col, row);
                Piece p = this.getPieceAtPosition(position);
                if (p.getColor() == color && p instanceof King) {
                    return position;
                }
                row++;
            }
            col++;
        }
        throw new Exception("There is no king of this color");
    }

    private boolean handleDrawByRepetition() {
        /*
        Checks is a position on the board has been repeated 3 times (draw)
         */
        // Using strings to avoid key reference (map will always contain this.state)
        String stateString = Arrays.deepToString(this.state);
        if (this.previousBoardStateMap.containsKey(stateString)) {
            this.previousBoardStateMap.put(stateString, this.previousBoardStateMap.get(stateString) + 1);
            return this.previousBoardStateMap.get(stateString) == 3;
        } else {
            this.previousBoardStateMap.put(stateString, 1);
        }
        return false;
    }

    private void addRowLabelsToStringBuilder(StringBuilder sb)
    {
        /*
        Adds row labels 1-8 to the stringbuilder for printing the board
         */
        sb.append("    ");
        for (String rowLabel : ColumnLetters.ROW_LABELS)
        {
            sb.append(rowLabel);
            sb.append("   ");
        }
        sb.append("\n");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int rowCount = 0;
        this.addRowLabelsToStringBuilder(sb);
        for (Piece[] row : this.state) {
            sb.append(ColumnLetters.COLUMN_LABELS.get(rowCount));
            sb.append("   ");
            for (Piece piece : row) {
                sb.append(piece.toString());
                sb.append(" ");
            }
            sb.append("\n");
            rowCount++;
        }
        return sb.toString();
    }

    public PieceColor getWinner() {
        return winner;
    }

    public PieceColor getKingInCheck() {
        return kingInCheck;
    }
}
