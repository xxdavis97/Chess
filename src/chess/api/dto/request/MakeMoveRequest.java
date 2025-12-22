package chess.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakeMoveRequest {
    @NotBlank(message = "Source square is required")
    @Pattern(regexp = "[a-h][1-8]", message = "Invalid square notation (must be a1-h8)")
    private String from;

    @NotBlank(message = "Destination square is required")
    @Pattern(regexp = "[a-h][1-8]", message = "Invalid square notation (must be a1-h8)")
    private String to;
}
