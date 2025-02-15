package chess.domain.board;

import chess.domain.enums.PieceColor;
import chess.domain.pieces.*;

import static chess.constants.InitialPositions.*;

public class BoardInitializer {
    
    private final Piece[][] boardState;

    public BoardInitializer() {
        this.boardState = new Piece[8][8];
    }

    public Piece[][] initializeBoard() {
        /*
        Initializes the board boardState
         */
        this.initPieceRow(PieceColor.WHITE);
        this.initPieceRow(PieceColor.BLACK);
        this.initPawns(PieceColor.WHITE);
        this.initPawns(PieceColor.BLACK);
        this.initEmptySquares();
        return this.boardState;
    }


    private int getPawnRowForColor(PieceColor color) {
        /*
        Returns the row number to initialize pawns for a color
         */
        return switch (color) {
            case WHITE -> 1;
            case BLACK -> 6;
            default -> -1;
        };
    }

    private int getPieceRowForColor(PieceColor color) {
        /*
        Returns the row number to initialize pieces for a color
         */
        return switch (color) {
            case WHITE -> 0;
            case BLACK -> 7;
            default -> -1;
        };
    }

    private void initPawns(PieceColor color) {
        /*
        Initializes the pawns on the initial game board
         */
        int row = this.getPawnRowForColor(color);
        int i = 0;
        while (i < 8) {
            Piece p = new Pawn(color);
            this.boardState[i][row] = p;
            i++;
        }
    }

    private void initPieceRow(PieceColor color) {
        /*
        Initializes the pieces on the initial game board
         */
        int row = this.getPieceRowForColor(color);
        for (int col: ROOKS) {
            Piece p = new Rook(color);
            this.boardState[col][row] = p;
        }
        for (int col: KNIGHTS) {
            Piece p = new Knight(color);
            this.boardState[col][row] = p;
        }
        for (int col: BISHOPS) {
            Piece p = new Bishop(color);
            this.boardState[col][row] = p;
        }
        Piece q = new Queen(color);
        this.boardState[QUEEN][row] = q;
        Piece k = new King(color);
        this.boardState[KING][row] = k;
    }

    private void initEmptySquares() {
        /*
        Initializes the empty squares on the initial game board
         */
        int row = 2;
        while (row < 6) {
            int col = 0;
            while (col < 8) {
                Piece empty = new Piece();
                this.boardState[col][row] = empty;
                col++;
            }
            row++;
        }
    }
    
}
