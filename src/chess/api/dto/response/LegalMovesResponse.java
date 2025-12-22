package chess.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalMovesResponse {
    private String piece;            // "e2"
    private List<String> legalMoves; // ["e3", "e4"]
}
