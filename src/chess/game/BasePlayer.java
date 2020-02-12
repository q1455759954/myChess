package chess.game;

import java.util.LinkedList;
import java.util.List;

public abstract class BasePlayer implements IPlayer{

    protected List<Point> myPoint = new LinkedList<Point>();

    protected int maxX;

    protected int maxY;

    protected List<Point> allFreePoint ;

    protected IChessBoard chessBoard;



    private final Point temp = new Point(0,0);
    @Override
    public final boolean hasWin(){
        if(myPoint.size()<5){
            return false;
        }
        Point point = myPoint.get(myPoint.size()-1);
        int count = 1;
        int x=point.getX(),y=point.getY();
        //横向--
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setX(temp.getX()-1)) && temp.getX()>=0 && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setX(temp.getX()+1)) && temp.getX()<maxX && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        //纵向|
        count = 1;
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setY(temp.getY()-1)) && temp.getY()>=0) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setY(temp.getY()+1)) && temp.getY()<maxY && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        //正斜向 /
        count =1;
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setX(temp.getX()-1).setY(temp.getY()+1)) && temp.getX()>=0 && temp.getY()<maxY) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setX(temp.getX()+1).setY(temp.getY()-1)) && temp.getX()<maxX && temp.getY()>=0 && count<6) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        //反斜 \
        count = 1;
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setX(temp.getX()-1).setY(temp.getY()-1)) && temp.getX()>=0 && temp.getY()>=0) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoint.contains(temp.setX(temp.getX()+1).setY(temp.getY()+1)) && temp.getX()<maxX && temp.getY()<maxY && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        return false;
    }

    @Override
    public void setChessBoard(IChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.maxX=chessBoard.getMaxX();
        this.maxY=chessBoard.getMaxY();
        this.allFreePoint=chessBoard.getFreePoint();
        myPoint.clear();
    }

    @Override
    public final List<Point> getMyPoint() {
        return myPoint;
    }
}
