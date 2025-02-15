package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.PieceMovement;

public class King extends Piece {

    private boolean hasMoved;

    public King(PieceColor color) {
        super(color);
        this.hasMoved = false;
    }

    @Override
    public boolean isMoveLegal(int[] currPosition, int[] newPosition, Board board) throws Exception {
        boolean oneSquare = PieceMovement.oneSquareAnyDirection(currPosition, newPosition);
        boolean endSquareNotOwnPiece = this.destinationOppositeColor(newPosition, board);
        boolean notInCheckPostMove = !this.inCheckPostMove(currPosition, newPosition, board);
        return oneSquare && endSquareNotOwnPiece && notInCheckPostMove;
    }

    @Override
    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) throws Exception {
        boolean oneSquare = PieceMovement.oneSquareAnyDirection(currPosition, newPosition);
        boolean endSquareNotOwnPiece = this.destinationOppositeColor(newPosition, board);
        return oneSquare && endSquareNotOwnPiece;
    }

    @Override
    public String toString() {
        return "K:" + this.pieceColorToString();
    }

    public boolean isInCheck(int[] currPosition, Board board) throws Exception {
        PieceColor oppositeColor = PieceColor.getInverseColor(this.getColor());
        int col = 0;
        while (col < 8) {
            int row = 0;
            while (row < 8) {
                Piece p = board.getState()[col][row];
                int[] piecePosition = new int[]{col, row};
                // used to be // if (!(p instanceof King) && p.getColor() == oppositeColor) { // not sure why
                // but no override for inLegalPath on the King object caused kings to be able to go in each others path
                if (p.getColor() == oppositeColor) {
                    if (p.inLegalPath(piecePosition, currPosition, board)) {
                        return true;
                    }
                }
                row++;
            }
            col++;
        }
        return false;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
