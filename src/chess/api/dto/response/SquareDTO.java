package chess.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SquareDTO {
    private String position;  // "e4"
    private PieceDTO piece;   // null if empty
}
