package chess.game;

import java.util.List;

public interface IChessBoard {

    public int getMaxX();

    public int getMaxY();

    public List<Point> getFreePoint();
}
