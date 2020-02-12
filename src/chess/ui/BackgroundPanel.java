package chess.ui;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private ImageIcon icon;
    private String name;
    public BackgroundPanel(){
        this.name=name;
        icon = new ImageIcon("src/chess/chess.jpg");
    }

    @Override
    protected void paintComponent(Graphics g) {
        int x=0,y=0;
        g.drawImage(icon.getImage(),x,y,icon.getIconWidth(),icon.getIconHeight(),this);
    }
}
