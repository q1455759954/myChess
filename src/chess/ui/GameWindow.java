package chess.ui;

import chess.game.*;
import chess.game.Point;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class GameWindow extends JFrame {

    private JFrame jFrame;
    private JPanel panel;


    private static final int x0 = 23, y0 = 48;//第一个点的坐标
    private static final int dis = 35;//两个点的距离
    private static final int floor = 16;//误差
    private BufferedImage image;
    private BufferedImage BLACK;
    private BufferedImage WHILE;
    private boolean black = true;

    private IChessBoard chessBoard;
    private IPlayer aiPlayer;
    private IPlayer human;

    public GameWindow()  {

        this.jFrame = this;
        jFrame = new JFrame();
        chessBoard = new ChessBoard();
        aiPlayer = new ComputerAIPlayer();
        human = new HumanPlayer();
        aiPlayer.setChessBoard(chessBoard);
        human.setChessBoard(chessBoard);

        jFrame.setTitle("五子棋");
        jFrame.setUndecorated(true);
        jFrame.setResizable(false);

        //加载图片
        try {
             image = ImageIO.read(new FileInputStream("src/chess/chess.jpg"));
             BLACK = ImageIO.read(new FileInputStream("src/chess/black.gif"));
             WHILE = ImageIO.read(new FileInputStream("src/chess/white.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将图片放到panel中
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image,0,0,image.getWidth(),image.getHeight(),jFrame);
            }
        };
        panel.setLayout(null);
        JButton btn_min = new JButton();
        btn_min.setContentAreaFilled(false);
        btn_min.setBorderPainted(false);
        btn_min.setBounds(480, 4, 22, 22);
        panel.add(btn_min);
        btn_min.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });
        JButton btn_close = new JButton();
        btn_close.setContentAreaFilled(false);
        btn_close.setBorderPainted(false);
        btn_close.setBounds(510, 5, 22, 22);
        panel.add(btn_close);
        btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        JButton huiQi = new JButton("悔棋");
        huiQi.setContentAreaFilled(false);
        huiQi.setMargin(new Insets(0, 0, 0, 0));
        huiQi.setBounds(240,5,50,22);
        huiQi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rePaintPanel();
            }
        });
        panel.add(huiQi);
        //panel添加点击事件
        panel.addMouseListener(new MouseAdapter() {
            private int x15 = x0 + 15 * dis;
            private int y15 = y0 + 15 * dis;
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = e.getX(), y = e.getY();
                    if ((x >= x0 - floor) && (x <= x15 + floor) && (y >= y0 - floor) && (y <= y15 + floor)) {
                        int p = -1, q = -1;
                        for (int i = 0; i < 15; i++) {
                            int xi = x0 + dis * i;
                            if (x >= xi - floor && x <= xi + floor) {
                                p = i;
                                break;
                            }
                        }
                        for (int i = 0; i < 15; i++) {
                            int yi = y0 + dis * i;
                            if (y >= yi - floor && y <= yi + floor) {
                                q = i;
                                break;
                            }
                        }
                        if (p >= 0 && q >= 0) {
                            if (chessBoard.getFreePoint().contains(new Point(p,q))){
                                onChessClick(p, q);
                            }
                        }
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            int x=0,y=0;
            @Override
            public void mouseMoved (MouseEvent e) {
                x = e.getX() + panel.getX();
                y =  e.getY() + panel.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                jFrame.setLocation(e.getLocationOnScreen().x - x, e.getLocationOnScreen().y - y);
            }
        });

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        jFrame.setSize(530,560);
        jFrame.getContentPane().add(panel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);

    }

    //悔棋
    private void rePaintPanel() {
        try {
            image = ImageIO.read(new FileInputStream("src/chess/chess.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Graphics2D g = image.createGraphics();
        Point p1 = human.getMyPoint().remove(human.getMyPoint().size()-1);
        Point p2 = aiPlayer.getMyPoint().remove(aiPlayer.getMyPoint().size()-1);
        chessBoard.getFreePoint().add(p1);
        chessBoard.getFreePoint().add(p2);
        int px,py;
        for (Point point:human.getMyPoint()){
            px =x0+dis*point.getX()-20;
            py= y0+dis*point.getY()-20;
            g.drawImage(BLACK,px,py,40,40,null);
        }
        for (Point point:aiPlayer.getMyPoint()){
            px =x0+dis*point.getX()-20;
            py= y0+dis*point.getY()-20;
            g.drawImage(WHILE,px,py,40,40,null);
        }
        g.dispose();
        panel.repaint();
    }

    private void onChessClick(int x, int y) {

        Point point = new Point(x,y);
        human.run(point,aiPlayer.getMyPoint());
        drawPaint(x,y,black);
        if (human.hasWin()){
            JOptionPane.showMessageDialog(null,
                    "黑棋胜!", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            jFrame.dispose();
            new GameWindow();
        }

        //电脑下棋
        aiPlayer.run(null,human.getMyPoint());
        Point comPoint = aiPlayer.getMyPoint().get(aiPlayer.getMyPoint().size()-1);
        drawPaint(comPoint.getX(),comPoint.getY(),!black);
        if (aiPlayer.hasWin()){
            JOptionPane.showMessageDialog(null,
                    "白棋胜!", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            jFrame.dispose();
            new GameWindow();
        }

    }

    private void drawPaint(int x, int y, boolean black) {
        int px = x0 + dis * x - 20;
        int py = y0 + dis * y - 20;
        Graphics2D g = image.createGraphics();

        if (black){
            g.drawImage(BLACK,px,py,40,40,null);
        }else{
            g.drawImage(WHILE,px,py,40,40,null);
        }
        g.dispose();
        panel.repaint();
    }

}
