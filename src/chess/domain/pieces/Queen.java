package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.PieceMovement;
import lombok.NoArgsConstructor;

import java.io.Serial;

import static chess.constants.PieceValues.QUEEN_VALUE;

@NoArgsConstructor
public class Queen extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;

    public Queen(PieceColor color) {
        super(color);
        this.setValue(QUEEN_VALUE);
    }

    @Override
    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) {
        boolean movingOneDirection = PieceMovement.movingOneDirection(currPosition, newPosition);
        if (!movingOneDirection)
            return false;
        boolean endSquareNotOwnPiece = this.destinationOppositeColor(newPosition, board);
        if (!endSquareNotOwnPiece)
            return false;
        boolean noPiecesInPath = this.noPieceInWay(currPosition, newPosition, board);
        return noPiecesInPath;
    }

    @Override
    public String toString() {
        return "Q:" + this.pieceColorToString();
    }
}
