package chess.api.service;

import chess.api.dto.request.CreateGameRequest;
import chess.api.dto.request.MakeMoveRequest;
import chess.api.exception.GameNotFoundException;
import chess.api.exception.IllegalMoveException;
import chess.api.exception.PawnPromotionRequiredException;
import chess.api.model.GameMode;
import chess.api.model.GameSession;
import chess.api.model.GameStatus;
import chess.api.repository.GameSessionRepository;
import chess.business.Game;
import chess.domain.enums.GameResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GameSessionService {

    private final GameSessionRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String GAME_SESSION_KEY_PREFIX = "game_session:";

    public GameSessionService(GameSessionRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public GameSession createGame(CreateGameRequest request) {
        String gameId = UUID.randomUUID().toString();
        Game game = new Game();
        GameSession session = new GameSession(gameId, game, request.getGameMode());

        if (request.getPlayerName() != null) {
            session.setWhitePlayerId(request.getPlayerName());
        }

        // Save using RedisTemplate with JSON serialization
        String key = GAME_SESSION_KEY_PREFIX + gameId;
        redisTemplate.opsForValue().set(key, session);
        return session;
    }

    public GameSession getGameSession(String gameId) {
        String key = GAME_SESSION_KEY_PREFIX + gameId;
        GameSession session = (GameSession) redisTemplate.opsForValue().get(key);
        if (session == null) {
            throw new GameNotFoundException(gameId);
        }
        return session;
    }

    public GameSession makeMove(String gameId, MakeMoveRequest request) {
        GameSession session = getGameSession(gameId);
        Game game = session.getGame();

        try {
            // Make the move
            GameResult result = game.userInputMovePiece(request.getFrom(), request.getTo());

            // Check if pawn promotion is needed
            int[] endPosition = parsePosition(request.getTo());
            boolean needsPromotion = game.getBoard().isPawnPromotion(endPosition);

            if (needsPromotion) {
                throw new PawnPromotionRequiredException(request.getTo());
            }

            // Update session
            session.setGame(game);
            session.setLastMoveAt(LocalDateTime.now());

            // Check if game is complete
            if (result != GameResult.CONTINUE) {
                session.setStatus(GameStatus.COMPLETED);
            }

            // Save using RedisTemplate with JSON serialization
            String key = GAME_SESSION_KEY_PREFIX + gameId;
            redisTemplate.opsForValue().set(key, session);
            return session;

        } catch (PawnPromotionRequiredException e) {
            // Save the session even if promotion is needed
            session.setGame(game);
            String key = GAME_SESSION_KEY_PREFIX + gameId;
            redisTemplate.opsForValue().set(key, session);
            throw e;
        } catch (Exception e) {
            throw new IllegalMoveException(e.getMessage());
        }
    }

    public GameSession promotePawn(String gameId, String square, String promoteTo) {
        GameSession session = getGameSession(gameId);
        Game game = session.getGame();

        try {
            int[] position = parsePosition(square);

            // Verify there's a pawn at this position that needs promotion
            if (!game.getBoard().isPawnPromotion(position)) {
                throw new IllegalArgumentException("No pawn promotion available at square: " + square);
            }

            // Handle promotion
            GameResult result = game.getBoard().handlePawnPromotion(promoteTo, position);

            // Update session
            session.setGame(game);
            session.setLastMoveAt(LocalDateTime.now());

            if (result != GameResult.CONTINUE) {
                session.setStatus(GameStatus.COMPLETED);
            }

            // Save using RedisTemplate with JSON serialization
            String key = GAME_SESSION_KEY_PREFIX + gameId;
            redisTemplate.opsForValue().set(key, session);
            return session;

        } catch (Exception e) {
            throw new IllegalMoveException("Pawn promotion failed: " + e.getMessage());
        }
    }

    public void deleteGame(String gameId) {
        String key = GAME_SESSION_KEY_PREFIX + gameId;
        Boolean deleted = redisTemplate.delete(key);
        if (deleted == null || !deleted) {
            throw new GameNotFoundException(gameId);
        }
    }

    private int[] parsePosition(String square) {
        if (square.length() != 2) {
            throw new IllegalArgumentException("Invalid square notation: " + square);
        }

        char file = square.charAt(0);
        char rank = square.charAt(1);

        // Get column from letter (a=0, b=1, etc.)
        int col = file - 'a';
        int row = Character.getNumericValue(rank) - 1;

        if (col < 0 || col > 7 || row < 0 || row > 7) {
            throw new IllegalArgumentException("Square out of bounds: " + square);
        }

        return new int[]{col, row};
    }
}
