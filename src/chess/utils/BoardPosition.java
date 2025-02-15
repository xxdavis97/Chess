package chess.utils;

public class BoardPosition {

    public static int[] getPositionFromCoords(int col, int row) {
        /*
        Given a column and a row, return the int[] position
         */
        return new int[]{col, row};
    }

    public static boolean isPositionOutOfBounds(int[] pos) {
        /*
        Given a position, determine if it is out of the bounds of the chess board
         */
        return pos[0] > 7 || pos[0] < 0 || pos[1] > 7 || pos[1] < 0;
    }

}
