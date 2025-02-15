package chess.domain.enums;

public enum PieceColor {
    WHITE,
    BLACK,
    EMPTY;

    public static PieceColor getInverseColor(PieceColor that) {
        return switch (that) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case WHITE -> "White";
            case BLACK -> "Black";
            case EMPTY -> "Empty";
        };
    }
}
