package chess.api.mapper;

import chess.api.dto.response.BoardStateDTO;
import chess.api.dto.response.SquareDTO;
import chess.domain.board.Board;
import chess.domain.pieces.Piece;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    private final PieceMapper pieceMapper;

    public BoardMapper(PieceMapper pieceMapper) {
        this.pieceMapper = pieceMapper;
    }

    public BoardStateDTO toDTO(Board board) {
        SquareDTO[][] squares = new SquareDTO[8][8];
        Piece[][] state = board.getState();

        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                Piece piece = state[col][row];
                String position = formatSquare(col, row);
                squares[col][row] = new SquareDTO(position, pieceMapper.toDTO(piece));
            }
        }

        return new BoardStateDTO(squares);
    }

    private String formatSquare(int col, int row) {
        char file = (char) ('a' + col);
        int rank = row + 1;
        return "" + file + rank;
    }
}
