package chess.api.model;

import chess.api.dto.PieceType;
import chess.domain.enums.PieceColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int moveNumber;
    private PieceColor color;
    private String from;
    private String to;
    private PieceType pieceType;
    private boolean isCapture;
    private boolean isCheck;
    private boolean isCheckmate;
    private PieceType promotedTo;
    private LocalDateTime timestamp;
    private String algebraicNotation;

    public Move(int moveNumber, PieceColor color, String from, String to, PieceType pieceType) {
        this.moveNumber = moveNumber;
        this.color = color;
        this.from = from;
        this.to = to;
        this.pieceType = pieceType;
        this.timestamp = LocalDateTime.now();
    }
}
