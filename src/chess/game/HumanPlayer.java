package chess.game;

import java.util.List;

public class HumanPlayer extends BasePlayer{
    @Override
    public void run(Point point, List<Point> computerPoints) {
        getMyPoint().add(point);
        //这是将人下的棋子去掉
        allFreePoint.remove(point);
        //我加的  将电脑下的棋子去掉
//        if(computerPoints.size()!=0){
//            allFreePoint.remove(computerPoints.get(computerPoints.size()-1));
//        }
    }
}
