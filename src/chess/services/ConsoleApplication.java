package chess.services;

import chess.business.Game;

public class ConsoleApplication {

    public static void main(String[] args) throws Exception {
        Game g = new Game();
        g.gameLoop();
    }
}
