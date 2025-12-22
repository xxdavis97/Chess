package chess.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PawnPromotionRequest {
    @NotBlank(message = "Promotion piece is required")
    @Pattern(regexp = "[QRBN]", message = "Invalid promotion piece (must be Q, R, B, or N)")
    private String promoteTo;
}
