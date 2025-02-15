package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.PieceMovement;

import static chess.constants.PieceValues.PIECE_VALUE;

public class Knight extends Piece {

    public Knight(PieceColor color) {
        super(color);
        this.setValue(PIECE_VALUE);
    }

    @Override
    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) {
        // Ensures not landing on its own piece, is an L and doesn't result in self check
        boolean moveL = PieceMovement.isL(currPosition, newPosition);
        if (!moveL)
            return false;
        boolean endSquareNotOwnPiece = this.destinationOppositeColor(newPosition, board);
        return endSquareNotOwnPiece;
    }

    @Override
    public String toString() {
        return "N:" + this.pieceColorToString();
    }
}
