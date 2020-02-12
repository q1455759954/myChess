package chess.game;

import java.util.LinkedList;
import java.util.List;

public class ChessBoard implements IChessBoard{

    private List<Point> allFreePoint ;

    public ChessBoard(){
        allFreePoint = new LinkedList<>();
        for (int i=0;i<15;i++){
            for (int j=0;j<15;j++){
                allFreePoint.add(new Point(i,j));
            }
        }
    }

    @Override
    public int getMaxX() {
        return 15;
    }

    @Override
    public int getMaxY() {
        return 15;
    }

    @Override
    public List<Point> getFreePoint() {
        return allFreePoint;
    }
}
