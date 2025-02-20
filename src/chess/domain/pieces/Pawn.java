package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.BoardPosition;

import static chess.constants.PieceValues.PAWN_VALUE;

public class Pawn extends Piece {

    private int BASE_MOVE = 1;
    private int DOUBLE_MOVE = 2;
    private boolean canEnpassant = false;
    private boolean performedEnpassant = false;
    private int[] enPassantCapturePosition = null;

    public Pawn(PieceColor color) {
        super(color);
        this.setValue(PAWN_VALUE);
        // Pawns can only move forward, if black need to make sure position index is being
        // subtracted from, not added to
        if (color == PieceColor.BLACK) {
            BASE_MOVE = BASE_MOVE * -1;
            DOUBLE_MOVE = DOUBLE_MOVE * -1;
        }
    }

    @Override
    public boolean isMoveLegal(int[] currPosition, int[] newPosition, Board board) throws Exception {
        /*
        Checks if pawn has made a legal move
         */
        // If move greater than 2 squares return false
        if ((newPosition[0] != currPosition[0] && newPosition[0] > (currPosition[0] + 2)) || (newPosition[1] != currPosition[1] && newPosition[1] > (currPosition[1] + 2)))
        {
            return false;
        }
        // If the move is not an attempted capture or a legal single or double move return false
        if (!this.takeAttempt(currPosition, newPosition, board) && !this.clearPathMove(currPosition, newPosition, board)) {
            return false;
        }
        // Verify your king is not in check post moving
        boolean legalMove = !this.inCheckPostMove(currPosition, newPosition, board);
        // Set if after making the move this pawn is now en passant able
        this.canEnpassant = legalMove && (newPosition[1] == currPosition[1] + DOUBLE_MOVE);
        return legalMove;
    }

    @Override
    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) {
        return this.takeAttempt(currPosition, newPosition, board);
    }

    private boolean takeAttempt(int[] currPosition, int[] newPosition, Board board) {
        /*
        Verifies if pawn tried to take opposing pawn and if it is allowed
         */
        boolean horizontalDirection = (((newPosition[0] == (currPosition[0] + 1)) || (newPosition[0] == (currPosition[0] - 1))));
        boolean verticalDirection = newPosition[1] == (currPosition[1] + BASE_MOVE);
        // Verify pawn moved one square diagonally in the correct direction
        if (horizontalDirection && verticalDirection) {
            Piece opposingPiece = board.getPieceAtPosition(newPosition);
            // If there is no piece on the selected square, check if it was an en passant capture
            if (opposingPiece.getColor().equals(PieceColor.EMPTY)) {
                boolean enPassantTake = false;
                int[] capturePosition = null;
                if (newPosition[0] == (currPosition[0] - 1)) {
                    capturePosition = BoardPosition.getPositionFromCoords(currPosition[0] - 1, currPosition[1]);
                    enPassantTake = this.isEnpassantTakeLegal(capturePosition, board);
                }
                if (newPosition[0] == (currPosition[0] + 1)) {
                    capturePosition = BoardPosition.getPositionFromCoords(currPosition[0] + 1, currPosition[1]);
                    enPassantTake = this.isEnpassantTakeLegal(capturePosition, board);
                }
                if (enPassantTake) {
                    this.setPerformedEnpassant(true);
                    this.setEnPassantCapturePosition(capturePosition);
                    return true;
                }
            }
            // Return true for take attempt if target is a piece of opposing color
            PieceColor inverseColor = PieceColor.getInverseColor(this.getColor());
            return opposingPiece.getColor() == inverseColor;
        }
        return false;
    }

    private boolean isEnpassantTakeLegal(int[] comparisonPosition, Board board) {
        /*
        If a pawn capture move is to a blank square, check if the square adjacent to the original position is
        and en passantable pawn if so return true
         */
        Piece possibleCapture;
        possibleCapture = board.getPieceAtPosition(comparisonPosition);
        return possibleCapture instanceof Pawn && ((Pawn) possibleCapture).isCanEnpassant();
    }

    private boolean clearPathMove(int[] currPosition, int[] newPosition, Board board) {
        /*
        Checks if a straight move is legal by checking if the pawn tried to move once and the path is clear or,
        if the pawn is on the starting row, if tried to move twice and both squares are clear
         */
        if ((newPosition[0] == currPosition[0] && newPosition[1] == (currPosition[1] + BASE_MOVE)) && board.isEmptySquare(newPosition[0], newPosition[1])) {
            return true;
        } else if ((newPosition[0] == currPosition[0] && newPosition[1] == (currPosition[1] + DOUBLE_MOVE)) && this.onStartingRow(currPosition[1])) {
            return board.isEmptySquare(newPosition[0], currPosition[1] + BASE_MOVE) && board.isEmptySquare(newPosition[0], newPosition[1]);
        } else {
            return false;
        }
    }

    private boolean onStartingRow(int row) {
        /*
        Returns if the pawn is on the row it initially started on
         */
        return (this.getColor() == PieceColor.WHITE && row == 1) || (this.getColor() == PieceColor.BLACK && row == 6);
    }

    public boolean isCanEnpassant() {
        return canEnpassant;
    }

    public void setCanEnpassant(boolean canEnpassant) {
        this.canEnpassant = canEnpassant;
    }

    public boolean isPerformedEnpassant() {
        return performedEnpassant;
    }

    public void setPerformedEnpassant(boolean performedEnpassant) {
        this.performedEnpassant = performedEnpassant;
    }

    public int[] getEnPassantCapturePosition() {
        return enPassantCapturePosition;
    }

    public void setEnPassantCapturePosition(int[] enPassantCapturePosition) {
        this.enPassantCapturePosition = enPassantCapturePosition;
    }

    @Override
    public String toString() {
        return "P:" + this.pieceColorToString();
    }
}
