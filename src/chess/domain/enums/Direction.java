package chess.domain.enums;

public enum Direction {
    UP(new int[]{0, 1}),
    DOWN(new int[]{0, -1}),
    LEFT(new int[]{-1, 0}),
    RIGHT(new int[]{1, 0}),
    UP_RIGHT(new int[]{1, 1}),
    UP_LEFT(new int[]{-1, 1}),
    DOWN_RIGHT(new int[]{1, -1}),
    DOWN_LEFT(new int[]{-1, -1});

    private final int[] direction;

    Direction(int[] direction) {
        this.direction = direction;
    }

    public int[] getDirection() {
        return direction;
    }
}
