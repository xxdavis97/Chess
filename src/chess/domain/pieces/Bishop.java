package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.PieceMovement;

public class Bishop extends Piece {

    public Bishop(PieceColor color) {
        super(color);
    }

    @Override
    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) {
        boolean movingDiagonally = PieceMovement.movingDiagonally(currPosition, newPosition);
        if (!movingDiagonally)
            return false;
        boolean endSquareNotOwnPiece = this.destinationOppositeColor(newPosition, board);
        if (!endSquareNotOwnPiece)
            return false;
        boolean noPiecesInPath = this.noPieceInWay(currPosition, newPosition, board);
        return noPiecesInPath;
    }

    @Override
    public String toString() {
        return "B:" + this.pieceColorToString();
    }
}
