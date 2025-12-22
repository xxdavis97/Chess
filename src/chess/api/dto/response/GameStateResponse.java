package chess.api.dto.response;

import chess.api.model.GameStatus;
import chess.domain.enums.GameResult;
import chess.domain.enums.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateResponse {
    private String gameId;
    private BoardStateDTO board;
    private PieceColor currentTurn;
    private GameResult gameResult;      // CONTINUE, CHECKMATE, STALEMATE, DRAW_BY_REPETITION
    private PieceColor kingInCheck;     // null or WHITE/BLACK
    private PieceColor winner;          // null or WHITE/BLACK
    private boolean awaitingPawnPromotion;
    private String promotionSquare;     // e.g., "e8"
    private List<MoveDTO> moveHistory;
}
