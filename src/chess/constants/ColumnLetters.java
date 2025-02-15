package chess.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ColumnLetters {
    public static final Map<Character, Integer> COLUMN_LETTERS = Map.of('a', 0, 'b', 1, 'c', 2,
            'd', 3, 'e', 4, 'f', 5, 'g', 6, 'h', 7);

    public static final List<String> COLUMN_LABELS = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");

    public static final List<String> ROW_LABELS = IntStream.range(1, 9)
            .mapToObj(Integer::toString).collect(Collectors.toList());

}
