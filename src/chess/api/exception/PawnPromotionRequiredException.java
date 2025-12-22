package chess.api.exception;

public class PawnPromotionRequiredException extends RuntimeException {
    private final String promotionSquare;

    public PawnPromotionRequiredException(String promotionSquare) {
        super("Pawn promotion required at square: " + promotionSquare);
        this.promotionSquare = promotionSquare;
    }

    public String getPromotionSquare() {
        return promotionSquare;
    }
}
