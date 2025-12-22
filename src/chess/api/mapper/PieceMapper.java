package chess.api.mapper;

import chess.api.dto.PieceType;
import chess.api.dto.response.PieceDTO;
import chess.domain.enums.PieceColor;
import chess.domain.pieces.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PieceMapper {

    private static final Map<String, PieceType> PIECE_TYPE_MAP = Map.of(
        "Pawn", PieceType.PAWN,
        "Knight", PieceType.KNIGHT,
        "Bishop", PieceType.BISHOP,
        "Rook", PieceType.ROOK,
        "Queen", PieceType.QUEEN,
        "King", PieceType.KING
    );

    private static final Map<PieceType, Map<PieceColor, String>> PIECE_SYMBOLS = Map.of(
        PieceType.PAWN, Map.of(PieceColor.WHITE, "♙", PieceColor.BLACK, "♟"),
        PieceType.KNIGHT, Map.of(PieceColor.WHITE, "♘", PieceColor.BLACK, "♞"),
        PieceType.BISHOP, Map.of(PieceColor.WHITE, "♗", PieceColor.BLACK, "♝"),
        PieceType.ROOK, Map.of(PieceColor.WHITE, "♖", PieceColor.BLACK, "♜"),
        PieceType.QUEEN, Map.of(PieceColor.WHITE, "♕", PieceColor.BLACK, "♛"),
        PieceType.KING, Map.of(PieceColor.WHITE, "♔", PieceColor.BLACK, "♚")
    );

    public PieceDTO toDTO(Piece piece) {
        if (piece.getColor() == PieceColor.EMPTY) {
            return null;
        }

        String className = piece.getClass().getSimpleName();
        PieceType type = PIECE_TYPE_MAP.get(className);
        String symbol = PIECE_SYMBOLS.get(type).get(piece.getColor());

        return new PieceDTO(type, piece.getColor(), symbol);
    }

    public static PieceType getPieceType(Piece piece) {
        return PIECE_TYPE_MAP.get(piece.getClass().getSimpleName());
    }
}
