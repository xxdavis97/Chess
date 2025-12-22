package chess.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveResponse {
    private boolean success;
    private GameStateResponse gameState;
    private MoveDTO move;
}
