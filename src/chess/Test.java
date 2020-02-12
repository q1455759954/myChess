package chess;

import chess.ui.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

public class Test extends JPanel{
//    private ImageIcon icon;
//    public Test(){
//        JFrame j = new JFrame();
//        icon = new ImageIcon("src/chess/chess.jpg");
////        image.setImage(Test.class.getResource("chess.jpg"));
//        j.add(new JLabel(icon));
//
//        j.setBounds(500, 200, 500, 400);
//        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        j.setVisible(true);
//        j.pack();
//
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        int x=0,y=0;
//        g.drawImage(icon.getImage(),x,y,icon.getIconWidth(),icon.getIconHeight(),this);
//    }
    public static void main(String[] args) {
        new GameWindow();
    }
}
