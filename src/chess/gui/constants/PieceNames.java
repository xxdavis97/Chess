package chess.gui.constants;

import java.util.Arrays;
import java.util.List;

public class PieceNames {

    public static final String WHITE_PREFIX = "white-";
    public static final String BLACK_PREFIX = "black-";
    public static final String KNIGHT = "knight";
    public static final String QUEEN = "queen";
    public static final String KING = "king";
    public static final String BISHOP = "bishop";
    public static final String PAWN = "pawn";
    public static final String ROOK = "rook";
    public static final String PNG = ".png";

    public static final List<String> whitePieceOrder = Arrays.asList(
            createPieceName(WHITE_PREFIX, ROOK),
            createPieceName(WHITE_PREFIX, KNIGHT),
            createPieceName(WHITE_PREFIX, BISHOP),
            createPieceName(WHITE_PREFIX, QUEEN),
            createPieceName(WHITE_PREFIX, KING),
            createPieceName(WHITE_PREFIX, BISHOP),
            createPieceName(WHITE_PREFIX, KNIGHT),
            createPieceName(WHITE_PREFIX, ROOK)
    );

    public static final List<String> blackPieceOrder = Arrays.asList(
        createPieceName(BLACK_PREFIX, ROOK),
        createPieceName(BLACK_PREFIX, KNIGHT),
        createPieceName(BLACK_PREFIX, BISHOP),
        createPieceName(BLACK_PREFIX, QUEEN),
        createPieceName(BLACK_PREFIX, KING),
        createPieceName(BLACK_PREFIX, BISHOP),
        createPieceName(BLACK_PREFIX, KNIGHT),
        createPieceName(BLACK_PREFIX, ROOK)
    );

    public static String createPieceName(String prefix, String piece) {
        return prefix + piece + PNG;
    }
}
