package chess.api.exception;

public class UnauthorizedMoveException extends RuntimeException {
    public UnauthorizedMoveException(String playerName, String color) {
        super("Player '" + playerName + "' is not authorized to move " + color + " pieces");
    }
}
