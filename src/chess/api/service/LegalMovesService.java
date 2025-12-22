package chess.api.service;

import chess.business.Game;
import chess.constants.ColumnLetters;
import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.domain.pieces.Piece;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LegalMovesService {

    public List<String> getLegalMoves(Game game, String square) throws Exception {
        Board board = game.getBoard();
        PieceColor turn = game.getTurn();
        int[] position = parseSquare(square);

        Piece piece = board.getPieceAtPosition(position);

        // Validate piece exists and belongs to current player
        if (piece.getColor() == PieceColor.EMPTY) {
            throw new IllegalArgumentException("No piece at square: " + square);
        }

        if (piece.getColor() != turn) {
            throw new IllegalArgumentException("Not your piece - it's " + turn + "'s turn");
        }

        List<String> legalMoves = new ArrayList<>();

        // Iterate all board positions to find legal moves
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                int[] targetPos = new int[]{col, row};
                try {
                    // Use existing isMoveLegal method from Piece
                    if (piece.isMoveLegal(position, targetPos, board)) {
                        legalMoves.add(formatSquare(col, row));
                    }
                } catch (Exception e) {
                    // Skip invalid moves
                }
            }
        }

        return legalMoves;
    }

    private int[] parseSquare(String square) {
        if (square.length() != 2) {
            throw new IllegalArgumentException("Invalid square notation: " + square);
        }

        char file = square.charAt(0);
        char rank = square.charAt(1);

        int col = ColumnLetters.COLUMN_LETTERS.get(file);
        int row = Integer.parseInt(String.valueOf(rank)) - 1;

        return new int[]{col, row};
    }

    private String formatSquare(int col, int row) {
        char file = (char) ('a' + col);
        int rank = row + 1;
        return "" + file + rank;
    }
}
