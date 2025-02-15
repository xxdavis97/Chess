package chess.utils;

import static chess.domain.enums.Direction.*;

public class PieceMovement {

    public static boolean movingDiagonally(int[] currPosition, int[] newPosition) {
        double slopeNum = Math.abs(newPosition[1] - currPosition[1]);
        double slopeDenom = Math.abs(newPosition[0] - currPosition[0]);
        double slope = slopeNum / slopeDenom;
        return slope == 1;
    }

    public static boolean movingStraight(int[] currPosition, int[] newPosition) {
        return !(currPosition[0] != newPosition[0] && currPosition[1] != newPosition[1]);
    }

    public static boolean isL(int[] currPosition, int[] newPosition) {
        int deltaX = Math.abs(newPosition[0] - currPosition[0]);
        int deltaY = Math.abs(newPosition[1] - currPosition[1]);
        return (deltaY == 2 && deltaX == 1) || (deltaY == 1 && deltaX == 2);
    }

    public static boolean oneSquareAnyDirection(int[] currPosition, int[] newPosition) {
        return (Math.abs(currPosition[0] - newPosition[0]) < 2 && Math.abs(currPosition[1] - newPosition[1]) < 2);
    }

    public static boolean movingOneDirection(int[] currPosition, int[] newPosition) {
        return movingStraight(currPosition, newPosition) || movingDiagonally(currPosition, newPosition);
    }

    public static int[] getMovementDirection(int[] currPosition, int[] newPosition) {
        /*
        Return movement direction as an int array.. (i.e. if going from a1->a8 the result is [0,1]
        because it is not moving side to side and is moving up.
        Orientation is white so +1 means going from white start towards black start and vice versa
         */
        boolean goingUp = newPosition[1] > currPosition[1];
        boolean goingDown = newPosition[1] < currPosition[1];
        boolean goingRight = newPosition[0] > currPosition[0];
        boolean goingLeft = newPosition[0] < currPosition[0];
        if (goingUp && goingRight) {
            return UP_RIGHT.getDirection();
        }
        if (goingUp && goingLeft) {
            return UP_LEFT.getDirection();
        }
        if (goingDown && goingRight) {
            return DOWN_RIGHT.getDirection();
        }
        if (goingDown && goingLeft) {
            return DOWN_LEFT.getDirection();
        }
        if (goingUp) {
            return UP.getDirection();
        }
        if (goingDown) {
            return DOWN.getDirection();
        }
        if (goingRight) {
            return RIGHT.getDirection();
        }
        if (goingLeft) {
            return LEFT.getDirection();
        }
        throw new IllegalArgumentException("Current position can not be equal to New Position");
    }
}
