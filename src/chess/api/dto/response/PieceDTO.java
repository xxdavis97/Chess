package chess.api.dto.response;

import chess.api.dto.PieceType;
import chess.domain.enums.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceDTO {
    private PieceType type;
    private PieceColor color;
    private String symbol; // "♙", "♘", etc. for UI display
}
