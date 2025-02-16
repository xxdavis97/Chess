package chess.gui;

import chess.domain.enums.PieceColor;
import chess.gui.constants.PieceNames;
import chess.gui.pictures.DTPicture;
import chess.gui.pictures.PictureTransferHandler;
import chess.gui.utils.BufferedImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ChessFrame extends JFrame implements MouseMotionListener {
    //TODO: PROBABLY WANT TO MAKE EACH CHESS SQUARE A JPANEL
    private final PictureTransferHandler picHandler = new PictureTransferHandler();
    private final JLayeredPane lpane = new JLayeredPane();

    public ChessFrame() {
        setSize( 500, 500 );
        setPreferredSize(new Dimension(600, 400));

        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setLayout(new BorderLayout());
        this.add(lpane, BorderLayout.CENTER);


        ChessBoard boardPanel = new ChessBoard();
        lpane.add(boardPanel, 0, 0);
        try {
            this.addDragPictures();
        } catch (IOException io) {
            System.out.println("IOException in ChessFrame adding drag/drop pictures");
        }

        this.pack();
        this.setVisible(true);
    }

    public void addDragPictures() throws IOException {
        /*
        Adds the images to the board with drag/drop functionality
         */
        DTPicture.setInstallInputMapBindings(false);

        int x = 50;
        int y = 52;
        for (String pieceName : PieceNames.blackPieceOrder) {
            this.addPiece(pieceName, x, y);
            x += 50;
        }
        this.addPawns(PieceColor.BLACK, 102);
        this.addBlankDragTargets();
        this.addPawns(PieceColor.WHITE, 352);
        x = 50;
        y = 402;
        for(String pieceName : PieceNames.whitePieceOrder) {
            this.addPiece(pieceName, x, y);
            x += 50;
        }
    }

    private void addPiece(String pieceName, int x, int y) throws IOException {
        BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(pieceName));
        image = BufferedImageUtils.resize(image, 45, 45);
        this.addDragPanel(image, x, y);
    }

    private void addPawns(PieceColor color, int y) throws IOException {
        String pawnName = PieceNames.WHITE_PREFIX + PieceNames.PAWN + PieceNames.PNG;
        if (color == PieceColor.BLACK) {
            pawnName = PieceNames.BLACK_PREFIX + PieceNames.PAWN + PieceNames.PNG;
        }
        BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(pawnName));
        image = BufferedImageUtils.resize(image, 45, 45);
        int x = 50;
        while (x < 450) {
            this.addDragPanel(image, x, y);
            x += 50;
        }
    }

    private void addBlankDragTargets() {
        /*
        Populates the 3rd to 6th rows with blank drag and drops, these serve as drop locations for the pieces
         */
        int x = 50;
        int y = 152;
        while (y < 352)
        {
            while(x < 450) {
                this.addDragPanel(null, x, y);
                x += 50;
            }
            x = 50;
            y += 50;
        }
    }

    private void addDragPanel(Image image, int x, int y) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        DTPicture dropTarget = new DTPicture(image, x, y, 45, 45);
        dropTarget.setTransferHandler(picHandler);
        panel.setBounds(x, y, 45, 45);
        panel.add(dropTarget);
        setPreferredSize(new Dimension(450, 630));
        lpane.add(panel, x,  y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
