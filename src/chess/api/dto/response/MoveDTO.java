package chess.api.dto.response;

import chess.api.dto.PieceType;
import chess.domain.enums.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveDTO {
    private int moveNumber;
    private PieceColor color;
    private String from;
    private String to;
    private PieceType pieceType;
    private boolean isCapture;
    private boolean isCheck;
    private boolean isCheckmate;
    private PieceType promotedTo;  // null or QUEEN, ROOK, BISHOP, KNIGHT
    private LocalDateTime timestamp;
    private String algebraicNotation;
}
