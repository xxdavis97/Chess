package chess.api.service;

import chess.api.dto.request.CreateGameRequest;
import chess.api.dto.request.MakeMoveRequest;
import chess.api.exception.*;
import chess.api.model.GameMode;
import chess.api.model.GameSession;
import chess.api.model.GameStatus;
import chess.api.repository.GameSessionRepository;
import chess.business.Game;
import chess.domain.enums.GameResult;
import chess.domain.enums.PieceColor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    /**
     * Add a player to a multiplayer game with color selection
     */
    public GameSession joinGame(String gameId, String playerName, PieceColor color) {
        GameSession session = getGameSession(gameId);

        validateJoinRequest(session, color);

        // Check if player already has this color
        if (isPlayerAssignedToColor(session, playerName, color)) {
            return session;
        }

        // Check if color is available
        if (isColorTaken(session, color)) {
            throw new ColorAlreadyTakenException(color, gameId);
        }

        // Assign player and update status
        assignPlayerToColor(session, playerName, color);
        if (isBothPlayersJoined(session)) {
            session.setStatus(GameStatus.IN_PROGRESS);
        }

        saveSession(session);
        return session;
    }

    private void validateJoinRequest(GameSession session, PieceColor color) {
        if (session.getGameMode() == GameMode.SINGLE_PLAYER) {
            throw new IllegalMoveException("Cannot join single-player game");
        }
        if (color != PieceColor.WHITE && color != PieceColor.BLACK) {
            throw new IllegalArgumentException("Color must be WHITE or BLACK");
        }
    }

    private boolean isPlayerAssignedToColor(GameSession session, String playerName, PieceColor color) {
        return (color == PieceColor.WHITE && playerName.equals(session.getWhitePlayerId())) ||
               (color == PieceColor.BLACK && playerName.equals(session.getBlackPlayerId()));
    }

    private boolean isColorTaken(GameSession session, PieceColor color) {
        return (color == PieceColor.WHITE && session.getWhitePlayerId() != null) ||
               (color == PieceColor.BLACK && session.getBlackPlayerId() != null);
    }

    private void assignPlayerToColor(GameSession session, String playerName, PieceColor color) {
        if (color == PieceColor.WHITE) {
            session.setWhitePlayerId(playerName);
        } else {
            session.setBlackPlayerId(playerName);
        }
    }

    private boolean isBothPlayersJoined(GameSession session) {
        return session.getWhitePlayerId() != null && session.getBlackPlayerId() != null;
    }

    private void saveSession(GameSession session) {
        String key = GAME_SESSION_KEY_PREFIX + session.getGameId();
        redisTemplate.opsForValue().set(key, session);
    }

    public GameSession makeMove(String gameId, MakeMoveRequest request) {
        GameSession session = getGameSession(gameId);
        Game game = session.getGame();

        // Validate player authorization for multiplayer games
        if (session.getGameMode() == GameMode.MULTIPLAYER && request.getPlayerName() != null) {
            validatePlayerAuthorization(session, request.getPlayerName(), game.getTurn());
        }

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

            saveSession(session);
            return session;

        } catch (PawnPromotionRequiredException e) {
            // Save the session even if promotion is needed
            session.setGame(game);
            saveSession(session);
            throw e;
        } catch (Exception e) {
            throw new IllegalMoveException(e.getMessage());
        }
    }

    private void validatePlayerAuthorization(GameSession session, String playerName, PieceColor currentTurn) {
        // Determine which color this player controls
        boolean isWhitePlayer = playerName.equals(session.getWhitePlayerId());
        boolean isBlackPlayer = playerName.equals(session.getBlackPlayerId());

        // Player must be in the game
        if (!isWhitePlayer && !isBlackPlayer) {
            throw new UnauthorizedMoveException(playerName, "any color - player not in this game");
        }

        // Check if it's the player's turn
        PieceColor playerColor = isWhitePlayer ? PieceColor.WHITE : PieceColor.BLACK;
        if (playerColor != currentTurn) {
            throw new NotYourTurnException(playerName + ", it's " + currentTurn + "'s turn");
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
