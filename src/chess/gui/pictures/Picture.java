package chess.gui.pictures;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class Picture extends JComponent implements MouseListener, FocusListener,
        Accessible {
    Image image;
    int x;
    int y;
    int w;
    int h;

    public Picture(Image image, int x, int y, int w, int h) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        setFocusable(true);
        addMouseListener(this);
        addFocusListener(this);
    }

    public void mouseClicked(MouseEvent e) {
        //Since the user clicked on us, let's get focus!
        requestFocusInWindow();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void focusGained(FocusEvent e) {
        //Draw the component with a red border
        //indicating that it has focus.
        this.repaint();
    }

    public void focusLost(FocusEvent e) {
        //Draw the component with a black border
        //indicating that it doesn't have focus.
        this.repaint();
    }

    protected void paintComponent(Graphics graphics) {
        //Draw in our entire space, even if isOpaque is false.

        Graphics g = graphics.create();
//        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        if (image != null) {
            //Draw image at its natural size of 125x125.
            g.drawImage(image, 0, 0, this);
        }

        //Add a border, red if picture currently has focus
//        if (isFocusOwner()) {
//            g.setColor(Color.RED);
//        } else {
//            g.setColor(Color.BLACK);
//        }
        g.drawRect(x, y, w, h);
        g.dispose();
    }
}
