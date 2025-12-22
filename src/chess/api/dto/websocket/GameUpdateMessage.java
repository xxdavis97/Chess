package chess.api.dto.websocket;

import chess.api.dto.response.GameStateResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateMessage {
    private GameUpdateType type;
    private String gameId;
    private GameStateResponse gameState;
    private String message;
    private LocalDateTime timestamp;

    public GameUpdateMessage(GameUpdateType type, String gameId, GameStateResponse gameState) {
        this.type = type;
        this.gameId = gameId;
        this.gameState = gameState;
        this.timestamp = LocalDateTime.now();
    }

    public GameUpdateMessage(GameUpdateType type, String gameId, String message) {
        this.type = type;
        this.gameId = gameId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
