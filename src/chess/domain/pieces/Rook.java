package chess.domain.pieces;

import chess.domain.board.Board;
import chess.domain.enums.PieceColor;
import chess.utils.PieceMovement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

import static chess.constants.PieceValues.ROOK_VALUE;

@Getter
@Setter
@NoArgsConstructor
public class Rook extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean hasMoved;

    public Rook(PieceColor color) {
        super(color);
        this.hasMoved = false;
        this.setValue(ROOK_VALUE);
    }

    @Override
    public boolean inLegalPath(int[] currPosition, int[] newPosition, Board board) {
        boolean movingStraight = PieceMovement.movingStraight(currPosition, newPosition);
        if (!movingStraight)
            return false;
        boolean endSquareNotOwnPiece = this.destinationOppositeColor(newPosition, board);
        if (!endSquareNotOwnPiece)
            return false;
        boolean noPiecesInPath = this.noPieceInWay(currPosition, newPosition, board);
        return noPiecesInPath;
    }

    @Override
    public String toString() {
        return "R:" + this.pieceColorToString();
    }
}
