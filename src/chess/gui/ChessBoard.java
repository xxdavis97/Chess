package chess.gui;
import java.awt.*;
import javax.swing.*;

public class ChessBoard extends JFrame{

    private final Color c1 = new Color(0, 153, 51);
    private final Color c2 = new Color(255, 255, 255);

    public ChessBoard() {
        setSize( 500, 500 );
        setVisible( true );
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
    }

    private void setBackgroundColor(int iter, Graphics g) {
        if (iter % 2 == 0) {
            g.setColor(c1);
        } else {
            g.setColor(c2);
        }
    }
}
