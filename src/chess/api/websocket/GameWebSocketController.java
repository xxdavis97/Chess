package chess.api.websocket;

import chess.api.dto.request.JoinGameRequest;
import chess.api.dto.request.MakeMoveRequest;
import chess.api.dto.response.GameStateResponse;
import chess.api.dto.websocket.GameUpdateMessage;
import chess.api.dto.websocket.GameUpdateType;
import chess.api.mapper.BoardMapper;
import chess.api.model.GameSession;
import chess.api.service.GameSessionService;
import chess.api.service.GameUpdatePublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final GameSessionService gameSessionService;
    private final GameUpdatePublisher gameUpdatePublisher;
    private final BoardMapper boardMapper;

    public GameWebSocketController(GameSessionService gameSessionService,
                                   GameUpdatePublisher gameUpdatePublisher,
                                   BoardMapper boardMapper) {
        this.gameSessionService = gameSessionService;
        this.gameUpdatePublisher = gameUpdatePublisher;
        this.boardMapper = boardMapper;
    }

    /**
     * Handle moves sent via WebSocket
     * Client sends to: /app/game/{gameId}/move
     * Server broadcasts to: /topic/game/{gameId}
     */
    @MessageMapping("/game/{gameId}/move")
    public void handleMove(@DestinationVariable String gameId,
                          @Payload MakeMoveRequest request) {
        try {
            // Make the move using the same service as REST API
            GameSession session = gameSessionService.makeMove(gameId, request);

            // Convert to response
            GameStateResponse gameState = mapToGameStateResponse(session);

            // Publish to Redis (broadcasts to all server instances)
            GameUpdateMessage message = new GameUpdateMessage(
                GameUpdateType.MOVE,
                gameId,
                gameState
            );
            gameUpdatePublisher.publishGameUpdate(gameId, message);

        } catch (Exception e) {
            // Send error message
            GameUpdateMessage errorMessage = new GameUpdateMessage(
                GameUpdateType.MOVE,
                gameId,
                "Error: " + e.getMessage()
            );
            gameUpdatePublisher.publishGameUpdate(gameId, errorMessage);
        }
    }

    /**
     * Handle player joining the game with color selection
     * Client sends to: /app/game/{gameId}/join
     */
    @MessageMapping("/game/{gameId}/join")
    public void handlePlayerJoin(@DestinationVariable String gameId,
                                 @Payload JoinGameRequest request) {
        try {
            // Add player to the game
            GameSession session = gameSessionService.joinGame(gameId, request.getPlayerName(), request.getColor());

            // Get updated game state
            GameStateResponse gameState = mapToGameStateResponse(session);

            // Broadcast join message with game state
            GameUpdateMessage message = new GameUpdateMessage(
                GameUpdateType.PLAYER_JOINED,
                gameId,
                gameState
            );
            message.setMessage(request.getPlayerName() + " joined as " + request.getColor());
            gameUpdatePublisher.publishGameUpdate(gameId, message);

        } catch (Exception e) {
            // Send error message
            GameUpdateMessage errorMessage = new GameUpdateMessage(
                GameUpdateType.PLAYER_JOINED,
                gameId,
                "Error: " + e.getMessage()
            );
            gameUpdatePublisher.publishGameUpdate(gameId, errorMessage);
        }
    }

    private GameStateResponse mapToGameStateResponse(GameSession session) {
        GameStateResponse response = new GameStateResponse();
        response.setGameId(session.getGameId());
        response.setBoard(boardMapper.toDTO(session.getGame().getBoard()));
        response.setCurrentTurn(session.getGame().getTurn());
        response.setKingInCheck(session.getGame().getBoard().getKingInCheck());
        response.setWinner(session.getGame().getBoard().getWinner());
        response.setMoveHistory(session.getGame().getMoveHistory().stream()
            .map(move -> {
                var dto = new chess.api.dto.response.MoveDTO();
                dto.setMoveNumber(move.getMoveNumber());
                dto.setColor(move.getColor());
                dto.setFrom(move.getFrom());
                dto.setTo(move.getTo());
                dto.setPieceType(move.getPieceType());
                dto.setCapture(move.isCapture());
                dto.setCheck(move.isCheck());
                dto.setCheckmate(move.isCheckmate());
                dto.setPromotedTo(move.getPromotedTo());
                dto.setTimestamp(move.getTimestamp());
                dto.setAlgebraicNotation(move.getAlgebraicNotation());
                return dto;
            })
            .collect(java.util.stream.Collectors.toList()));
        return response;
    }
}
