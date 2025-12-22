package chess.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardStateDTO {
    private SquareDTO[][] squares;  // 8x8 grid
}
