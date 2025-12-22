package chess.business;

import chess.api.dto.PieceType;
import chess.api.model.Move;
import chess.constants.ColumnLetters;
import chess.domain.board.Board;
import chess.domain.enums.GameResult;
import chess.domain.enums.PieceColor;
import chess.domain.pieces.Piece;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Board board;
    private PieceColor turn;
    private List<Move> moveHistory;

    public Game() {
        this.board = new Board();
        this.turn = PieceColor.WHITE;
        this.moveHistory = new ArrayList<>();
    }

    public int[] userInputToPosition(String userInput) {
        if (userInput.length() != 2) {
            throw new IllegalArgumentException("An inputted move must be a string of length 2, for example 'e4'");
        }
        char letterVal = userInput.charAt(0);
        char rowNumber = userInput.charAt(1);
        // -1 for 0 index
        int row = Integer.parseInt(Character.toString(rowNumber)) - 1;
        if (row < 0 || row > 8) {
            throw new IllegalArgumentException("The second character of your move must be a number 1-8");
        }
        if (letterVal < 97 || letterVal > 104) {
            throw new IllegalArgumentException("The first character of your move must be the lowercase character a-h");
        }
        int col = ColumnLetters.COLUMN_LETTERS.get(letterVal);
        return new int[]{col, row};
    }

    // API version - does not handle pawn promotion automatically
    public GameResult userInputMovePiece(String start, String end) throws Exception {
        return userInputMovePiece(start, end, Optional.empty());
    }

    // Full version with optional Scanner for CLI pawn promotion
    public GameResult userInputMovePiece(String start, String end, Optional<Scanner> scanner) throws Exception {
        int[] startPosition = this.userInputToPosition(start);
        Piece p = this.board.getPieceAtPosition(startPosition);
        if (p.getColor() != this.turn) {
            throw new RuntimeException("Must move a " + this.turn.toString() + " piece as it is their turn");
        }
        int[] endPosition = this.userInputToPosition(end);

        // Record move in history
        Move move = new Move(
            this.moveHistory.size() + 1,
            this.turn,
            start,
            end,
            PieceType.valueOf(p.getClass().getSimpleName().toUpperCase())
        );

        GameResult moveResult = this.board.movePiece(startPosition, endPosition);
        boolean pawnPromote = this.board.isPawnPromotion(endPosition);
        if (pawnPromote && scanner.isPresent()) {
            // CLI mode: handle promotion with user input
            moveResult = this.handlePawnPromotion(endPosition, scanner.get());
        }
        // API mode: if pawnPromote is true but scanner is empty, caller handles promotion separately

        // Update move with game state info
        move.setCheck(moveResult == GameResult.CONTINUE && this.board.getKingInCheck() != PieceColor.EMPTY);
        move.setCheckmate(moveResult == GameResult.CHECKMATE);

        this.moveHistory.add(move);
        this.turn = PieceColor.getInverseColor(this.turn);
        return moveResult;
    }

    private GameResult handlePawnPromotion(int[] position, Scanner scanner) throws Exception {
        System.out.println("Your pawn has promoted, please select a piece to change it to (Q, R, N, B): ");
        String promotionSelection = scanner.next();
        return this.board.handlePawnPromotion(promotionSelection, position);
    }

    public void gameLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to chess, here is your board: ");
        System.out.println(this.board.toString());
        GameResult gameResult = GameResult.CONTINUE;
        while (gameResult.equals(GameResult.CONTINUE)) {
            System.out.println("Please input piece you would like to move (i.e. a2): ");
            String currPos = scanner.next();
            System.out.println("Please input the square your piece should move to (i.e. a4): ");
            String newPos = scanner.next();
            try {
                gameResult = this.userInputMovePiece(currPos, newPos, Optional.of(scanner));
                System.out.println(this.board.toString());
                switch (gameResult) {
                    case CHECKMATE -> {
                        System.out.println(this.board.getWinner().toString() + " wins!");
                        scanner.close();
                        return;
                    }
                    case STALEMATE -> {
                        System.out.println("The game ends in a draw by stalemate");
                        scanner.close();
                        return;
                    }
                    case DRAW_BY_REPETITION -> {
                        System.out.println("The game ends in a draw by repetition of moves");
                        scanner.close();
                        return;
                    }
                }
                if (this.board.getKingInCheck() != PieceColor.EMPTY) {
                    System.out.println(this.board.getKingInCheck().toString() + " is in check");
                }
                System.out.println(this.turn.toString() + " to play");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }

    // Getter methods for API access
    public Board getBoard() {
        return board;
    }

    public PieceColor getTurn() {
        return turn;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }
}
