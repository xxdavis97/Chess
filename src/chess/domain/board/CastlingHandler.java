package chess.domain.board;

import chess.domain.enums.PieceColor;
import chess.domain.pieces.King;
import chess.domain.pieces.Piece;
import chess.domain.pieces.Rook;
import chess.utils.BoardPosition;

import static chess.utils.BoardPosition.getPositionFromCoords;

public class CastlingHandler {

    public void castle(Piece p, int[] currKingPosition, int[] newKingPosition, Board board) {
        castleSwap(p, currKingPosition, newKingPosition, board);
    }

    public boolean isCastlingKing(Piece p, int[] currPosition, int[] newPosition, Board board) throws Exception {
        return p instanceof King && this.isCastle((King) p, currPosition, newPosition, board);
    }

    private boolean isCastle(King king, int[] currPosition, int[] newPosition, Board board) throws Exception {
        // can't castle if one of the castling square are covered or if king in check
        if (((newPosition[0] != (currPosition[0] + 2)) && (newPosition[0] != (currPosition[0] - 2)))
                || (king.isHasMoved()
                        || !this.castlingSquareSafe(currPosition, newPosition, board))) {
            return false;
        }
        if (newPosition[0] == (currPosition[0] + 2)) {
            Piece p = board.getPieceAtPosition(new int[]{currPosition[0] + 3, currPosition[1]});
            return p instanceof Rook && !((Rook) p).isHasMoved();
        } else if (newPosition[0] == (currPosition[0] - 2)) {
            Piece p = board.getPieceAtPosition(new int[]{currPosition[0] - 4, currPosition[1]});
            return p instanceof Rook && !((Rook) p).isHasMoved();
        }
        return false;
    }

    private void castleSwap(Piece p, int[] currPosition, int[] newPosition, Board board) {
        /*
        Swaps the king and the rook
        */
        Piece rook = getCastledRook(currPosition, newPosition, board);
        int[] currRookPosition = getCastledRookCurrPosition(currPosition, newPosition);
        int[] newRookPosition = getCastledRookNewPosition(currPosition, newPosition);
        board.setPieceAtPosition(rook, newRookPosition);
        board.setPieceAtPosition(p, newPosition);
        board.setPieceAtPosition(new Piece(), currPosition);
        board.setPieceAtPosition(new Piece(), currRookPosition);
    }

    private int[] getCastledRookCurrPosition(int[] currKingPosition, int[] newKingPosition) {
        /*
        Gets the rook that the king is castling with
         */
        if (currKingPosition[0] < newKingPosition[0]) {
            return getPositionFromCoords(newKingPosition[0] + 1, currKingPosition[1]);
        } else if (newKingPosition[0] < currKingPosition[0]) {
            return getPositionFromCoords(newKingPosition[0] - 2, currKingPosition[1]);
        } else {
            throw new RuntimeException("There was not a Rook at this location");
        }
    }

    private int[] getCastledRookNewPosition(int[] currKingPosition, int[] newKingPosition) {
        /*
        Gets the square the castling rook will end up on
         */
        if (currKingPosition[0] < newKingPosition[0]) {
            return getPositionFromCoords(currKingPosition[0] + 1, currKingPosition[1]);
        } else if (newKingPosition[0] < currKingPosition[0]) {
            return getPositionFromCoords(currKingPosition[0] - 1, currKingPosition[1]);
        } else {
            throw new RuntimeException("There was not a Rook at this location");
        }
    }

    private Piece getCastledRook(int[] currKingPosition, int[] newKingPosition, Board board) {
        /*
        Gets the rook the king is castling with
         */
        if (currKingPosition[0] < newKingPosition[0]) {
            return board.getPieceAtPosition(getPositionFromCoords(newKingPosition[0] + 1, newKingPosition[1]));
        } else if (newKingPosition[0] < currKingPosition[0]) {
            return board.getPieceAtPosition(getPositionFromCoords(newKingPosition[0] - 2, newKingPosition[1]));
        } else {
            throw new RuntimeException("There was not a Rook at this location");
        }
    }

    private boolean castlingSquareSafe(int[] currKingPosition, int[] newKingPosition, Board board) throws Exception {
        Piece king = board.getPieceAtPosition(currKingPosition);
        PieceColor color = king.getColor();
        PieceColor oppositeColor = PieceColor.getInverseColor(color);
        int[] newRookPosition = getCastledRookNewPosition(currKingPosition, newKingPosition);
        int col = 0;
        while (col < 8) {
            int row = 0;
            while (row < 8) {
                int[] position = BoardPosition.getPositionFromCoords(col, row);
                Piece piece = board.getPieceAtPosition(position);
                if (piece.getColor() == oppositeColor){
                    boolean legalPathOfKing = piece.inLegalPath(position, newKingPosition, board);
                    boolean legalPathOfRook = piece.inLegalPath(position, newRookPosition, board);
                    if (legalPathOfKing || legalPathOfRook) {
                        return false;
                    }
                }
                row++;
            }
            col++;
        }
        return true;
    }
}
