package chess.game;

import java.util.List;

public interface IPlayer {

    public void run(Point point,List<Point> p);

    public boolean hasWin();

    public void setChessBoard(IChessBoard chessBoard);

    public List<Point> getMyPoint();
}
