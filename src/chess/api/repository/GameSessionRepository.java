package chess.api.repository;

import chess.api.model.GameSession;
import chess.api.model.GameStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, String> {

    List<GameSession> findByStatus(GameStatus status);

    List<GameSession> findByWhitePlayerIdOrBlackPlayerId(String playerId1, String playerId2);

    Optional<GameSession> findByGameIdAndStatus(String gameId, GameStatus status);
}
