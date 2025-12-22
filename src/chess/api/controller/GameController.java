package chess.api.controller;

import chess.api.dto.request.CreateGameRequest;
import chess.api.dto.request.MakeMoveRequest;
import chess.api.dto.request.PawnPromotionRequest;
import chess.api.dto.response.*;
import chess.api.dto.websocket.GameUpdateMessage;
import chess.api.dto.websocket.GameUpdateType;
import chess.api.mapper.BoardMapper;
import chess.api.model.GameSession;
import chess.api.service.GameSessionService;
import chess.api.service.GameUpdatePublisher;
import chess.api.service.LegalMovesService;
import chess.business.Game;
import chess.domain.enums.PieceColor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/games")
@Tag(name = "Chess Game", description = "Endpoints for managing chess games and making moves")
public class GameController {

    private final GameSessionService gameSessionService;
    private final LegalMovesService legalMovesService;
    private final BoardMapper boardMapper;
    private final GameUpdatePublisher gameUpdatePublisher;

    public GameController(GameSessionService gameSessionService,
                          LegalMovesService legalMovesService,
                          BoardMapper boardMapper,
                          GameUpdatePublisher gameUpdatePublisher) {
        this.gameSessionService = gameSessionService;
        this.legalMovesService = legalMovesService;
        this.boardMapper = boardMapper;
        this.gameUpdatePublisher = gameUpdatePublisher;
    }

    @PostMapping
    @Operation(summary = "Create a new chess game", description = "Creates a new chess game session in single-player or multiplayer mode")
    public ResponseEntity<GameStateResponse> createGame(@Valid @RequestBody CreateGameRequest request) {
        GameSession session = gameSessionService.createGame(request);
        GameStateResponse response = mapToGameStateResponse(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get game state", description = "Retrieves the current state of a chess game including board position and move history")
    public ResponseEntity<GameStateResponse> getGameState(
            @Parameter(description = "Unique game identifier") @PathVariable String gameId) {
        GameSession session = gameSessionService.getGameSession(gameId);
        GameStateResponse response = mapToGameStateResponse(session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/moves")
    @Operation(summary = "Make a move", description = "Makes a chess move from one square to another with full validation")
    public ResponseEntity<MoveResponse> makeMove(
            @Parameter(description = "Unique game identifier") @PathVariable String gameId,
            @Valid @RequestBody MakeMoveRequest request) {

        GameSession session = gameSessionService.makeMove(gameId, request);
        GameStateResponse gameState = mapToGameStateResponse(session);

        // Get the last move from history
        Game game = session.getGame();
        MoveDTO lastMove = null;
        if (!game.getMoveHistory().isEmpty()) {
            lastMove = mapToMoveDTO(game.getMoveHistory().get(game.getMoveHistory().size() - 1));
        }

        // Publish update to Redis (will be broadcast to all server instances)
        GameUpdateMessage wsMessage = new GameUpdateMessage(
            GameUpdateType.MOVE,
            gameId,
            gameState
        );
        gameUpdatePublisher.publishGameUpdate(gameId, wsMessage);

        MoveResponse response = new MoveResponse(true, gameState, lastMove);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/promote")
    public ResponseEntity<GameStateResponse> promotePawn(
            @PathVariable String gameId,
            @Valid @RequestBody PawnPromotionRequest request,
            @RequestParam String square) {

        GameSession session = gameSessionService.promotePawn(gameId, square, request.getPromoteTo());
        GameStateResponse response = mapToGameStateResponse(session);

        // Publish update to Redis (will be broadcast to all server instances)
        GameUpdateMessage wsMessage = new GameUpdateMessage(
            GameUpdateType.MOVE,
            gameId,
            response
        );
        gameUpdatePublisher.publishGameUpdate(gameId, wsMessage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameId}/legal-moves/{square}")
    @Operation(summary = "Get legal moves", description = "Returns all legal moves for a piece at the specified square")
    public ResponseEntity<LegalMovesResponse> getLegalMoves(
            @Parameter(description = "Unique game identifier") @PathVariable String gameId,
            @Parameter(description = "Square notation (e.g., 'e2')") @PathVariable String square) throws Exception {

        GameSession session = gameSessionService.getGameSession(gameId);
        List<String> legalMoves = legalMovesService.getLegalMoves(session.getGame(), square);

        LegalMovesResponse response = new LegalMovesResponse(square, legalMoves);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameId}/history")
    public ResponseEntity<List<MoveDTO>> getMoveHistory(@PathVariable String gameId) {
        GameSession session = gameSessionService.getGameSession(gameId);
        List<MoveDTO> history = session.getGame().getMoveHistory().stream()
                .map(this::mapToMoveDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable String gameId) {
        gameSessionService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    private GameStateResponse mapToGameStateResponse(GameSession session) {
        Game game = session.getGame();
        BoardStateDTO boardState = boardMapper.toDTO(game.getBoard());

        PieceColor kingInCheck = game.getBoard().getKingInCheck();
        if (kingInCheck == PieceColor.EMPTY) {
            kingInCheck = null;
        }

        PieceColor winner = game.getBoard().getWinner();
        if (winner == PieceColor.EMPTY) {
            winner = null;
        }

        List<MoveDTO> moveHistory = game.getMoveHistory().stream()
                .map(this::mapToMoveDTO)
                .collect(Collectors.toList());

        // Check if awaiting pawn promotion
        boolean awaitingPromotion = false;
        String promotionSquare = null;
        // This would need additional logic to detect pawn promotion state

        GameStateResponse response = new GameStateResponse();
        response.setGameId(session.getGameId());
        response.setBoard(boardState);
        response.setCurrentTurn(game.getTurn());
        response.setGameResult(null); // Would need to track this in session
        response.setKingInCheck(kingInCheck);
        response.setWinner(winner);
        response.setAwaitingPawnPromotion(awaitingPromotion);
        response.setPromotionSquare(promotionSquare);
        response.setMoveHistory(moveHistory);

        return response;
    }

    private MoveDTO mapToMoveDTO(chess.api.model.Move move) {
        MoveDTO dto = new MoveDTO();
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
    }
}
