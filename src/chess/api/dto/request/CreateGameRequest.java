package chess.api.dto.request;

import chess.api.model.GameMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest {
    @NotNull(message = "Game mode is required")
    private GameMode gameMode;

    private String playerName;
}
