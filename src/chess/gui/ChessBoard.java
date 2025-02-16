package chess.gui;

import java.awt.*;
import javax.swing.*;


public class ChessBoard extends JPanel {

    private final Color c1 = new Color(0, 153, 51);
    private final Color c2 = new Color(247, 247, 223);

    public ChessBoard() {
        setSize( 500, 500 );
    }

    public void paint( Graphics g )
    {
        boolean oddRowStart = true;
        int cellNum =  1;
        for ( int x = 50; x <= 400; x += 50 ) {
            for ( int y = 50; y <= 400; y += 50 ) {
                this.setBackgroundColor(cellNum, g);
                g.fillRect( x, y, 50, 50 );
                cellNum++;
            }
            if (oddRowStart) {
                cellNum = 2;
            } else {
                cellNum = 1;
            }
            oddRowStart = !oddRowStart;
        }
//        try {
//            this.drawPieces(g, PieceColor.BLACK);
//        } catch (IOException io) {
//            System.out.println(io);
//        }
    }

    private void setBackgroundColor(int iter, Graphics g) {
        if (iter % 2 == 0) {
            g.setColor(c1);
        } else {
            g.setColor(c2);
        }
    }

//    private void drawPieces(PieceColor color) throws IOException {
//        this.addDragPicture(color);
//        int z = 0;
////        int x = 50;
////        int y = 52;
////        List<String> pieceNames = color == PieceColor.BLACK ? PieceNames.blackPieceOrder : PieceNames.whitePieceOrder;
////        for (String pieceName : pieceNames) {
////            BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(pieceName));
////            image = BufferedImageUtils.resize(image, 45, 45);
////            g.drawImage(image, x, y, null);
////            x += 50;
////        }
//    }


}
