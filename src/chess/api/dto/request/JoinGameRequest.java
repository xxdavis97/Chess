package chess.api.dto.request;

import chess.domain.enums.PieceColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinGameRequest {
    @NotBlank(message = "Player name is required")
    private String playerName;

    @NotNull(message = "Color selection is required")
    private PieceColor color; // WHITE or BLACK
}
