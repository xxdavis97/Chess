package chess.api.model;

import chess.business.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("game_session")
public class GameSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String gameId;
    private Game game;
    private String whitePlayerId;
    private String blackPlayerId;
    private GameMode gameMode;
    private GameStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastMoveAt;

    public GameSession(String gameId, Game game, GameMode gameMode) {
        this.gameId = gameId;
        this.game = game;
        this.gameMode = gameMode;
        this.status = gameMode == GameMode.SINGLE_PLAYER ? GameStatus.IN_PROGRESS : GameStatus.WAITING;
        this.createdAt = LocalDateTime.now();
        this.lastMoveAt = LocalDateTime.now();
    }
}
