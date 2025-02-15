package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.PieceMovement;

import java.util.Arrays;

import static chess.utils.BoardPosition.isPositionOutOfBounds;

public class Piece {

    private int value;
    private PieceColor color;

    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) throws Exception {
        /*
        Checks if a move within a piece's legal path
         */
        // All the returns speed up the process so don't do computationally intensive
        // checks if the basic criteria not met
        return false;
    }

    public boolean isMoveLegal(int[] currPosition, int[] newPosition, Board board) throws Exception {
        /*
        Checks if a move is legal, should be implemented by pieces
         */
        // All the returns speed up the process so don't do computationally intensive
        // checks if the basic criteria not met
        boolean inLegalPath = this.inLegalPath(currPosition, newPosition, board);
        if (!inLegalPath) {
            return false;
        }
        boolean notInCheckPostMove = !this.inCheckPostMove(currPosition, newPosition, board);
        return notInCheckPostMove;
    }

//    public int[][][] getAllLegalMoves(int[] currPosition, Board board) throws Exception {
//
//    }

    public boolean inCheckPostMove(int[] currPosition, int[] newPosition, Board board) throws Exception {
        /*
        Checks if your king is in check after you make a move by replicating the state of the board, moving the piece,
        and checking for check
         */
        Board copyOfBoardState = new Board(board.getState());
        Piece p = copyOfBoardState.getPieceAtPosition(currPosition);
        Piece pieceAtTargetPosition = copyOfBoardState.getPieceAtPosition(newPosition);
        copyOfBoardState.setPieceAtPosition(new Piece(), currPosition);
        copyOfBoardState.setPieceAtPosition(p, newPosition);
        boolean isCheck = copyOfBoardState.isInCheck(p.getColor());
        copyOfBoardState.setPieceAtPosition(p, currPosition);
        copyOfBoardState.setPieceAtPosition(pieceAtTargetPosition, newPosition);
        return isCheck;
    }

    public boolean destinationOppositeColor(int[] newPosition, Board board) {
        Piece newLocation = board.getPieceAtPosition(newPosition);
        return newLocation.getColor() != this.getColor();
    }

    boolean noPieceInWay(int[] currPosition, int[] newPosition, Board board) {
        int[] directionIterAmount = PieceMovement.getMovementDirection(currPosition, newPosition);
        int colIterAmount = directionIterAmount[0];
        int rowIterAmount = directionIterAmount[1];

        int[] positionIterator = new int[]{currPosition[0] + colIterAmount, currPosition[1] + rowIterAmount};
        while (!isPositionOutOfBounds(positionIterator) && !Arrays.equals(positionIterator, newPosition))//positionIterator[0] != (newPosition[0] - colIterAmount) && positionIterator[1] != (newPosition[1] - rowIterAmount))
        {
            // incrementing first or else it will always return false immediately because it registers itself
            Piece p = board.getPieceAtPosition(positionIterator);
            if (p.getColor() != PieceColor.EMPTY) {
                return false;
            }
            positionIterator[0] += colIterAmount;
            positionIterator[1] += rowIterAmount;
        }
        return true;
    }

    public Piece() {
        this.value = 0;
        this.color = PieceColor.EMPTY;
    }

    public Piece(PieceColor color) {
        this.color = color;
    }

    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String pieceColorToString() {
        return switch (this.color) {
            case BLACK -> "b";
            case WHITE -> "w";
            case EMPTY -> " x ";
        };
    }

    @Override
    public String toString() {
        return this.pieceColorToString();
    }
}
