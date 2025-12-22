package chess.api.exception;

import chess.domain.enums.PieceColor;

public class ColorAlreadyTakenException extends RuntimeException {
    public ColorAlreadyTakenException(PieceColor color, String gameId) {
        super(color + " is already taken in game " + gameId);
    }
}
