package chess.api.exception;

public class GameFullException extends RuntimeException {
    public GameFullException(String gameId) {
        super("Game " + gameId + " is full. Only 2 players allowed.");
    }
}
