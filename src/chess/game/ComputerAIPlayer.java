package chess.game;

import java.security.AlgorithmConstraints;
import java.util.*;
import java.util.List;

public class ComputerAIPlayer extends BasePlayer {

    // 四个方向，横- 、纵| 、正斜/ 、反斜\
    private static final int HENG = 0;
    private static final int ZONG = 1;
    private static final int ZHENG_XIE = 2;
    private static final int FAN_XIE = 3;
    //往前往后
    private static final boolean FORWARD = true;
    private static final boolean BACKWARD = false;


    //标示分析结果当前点位是两头通（ALIVE）还是只有一头通（HALF_ALIVE），封死的棋子分析过程自动屏蔽，不作为待选棋子
    private static final int ALIVE = 1;
    private static final int HALF_ALIVE = 0;





    private Point doAnalysis(List<Point> myPoint, List<Point> humanPoints) {
        if (humanPoints.size()==1){
            return myFirstPoint(humanPoints);
        }

        //清理上一次分析的结果
        initAnalysisResult();

        //第一次分析
        Point bestPoint = doFirstAnalysis(myPoint,humanPoints);
        if(bestPoint!=null){
            return bestPoint;//只能下这个棋，要么我赢，要么对方要赢下这个堵
        }
        //分析第一次结果，
        bestPoint = doComputerSencondAnalysis(computerFirstResults,computerSencodResults);
        if (bestPoint!=null){
            return bestPoint;//下了这个棋可以形成活四。（第一次分析就排除了绝杀棋）
        }
        computerFirstResults.clear();
        bestPoint = doHumanSencondAnalysis(humanFirstResults,humanSencodResults);
        if (bestPoint!=null){
            return bestPoint;//玩家下了这个棋可以形成活四，电脑需要下这个堵。（第一次分析就排除了绝杀棋）
        }
        humanFirstResults.clear();
        //第三次分析
        return doThirdAnalysis();

    }


    private Point doThirdAnalysis() {
//        if (!computer4HalfAlives.isEmpty()){//半活四
//            return computerSencodResults.get(0).point;
//        }
        Point mostBestPoint = getBestPoint();
        if (mostBestPoint!=null){
            return mostBestPoint;
        }
        return computerSencodResults.get(0).point;
    }

    private Point getBestPoint() {
        //电脑半活4
        Point mostBest = getBestPoint(computer4HalfAlives, computerSencodResults);
        if (mostBest != null)
            return mostBest;
        //人能形成3，4
        mostBest = getBestPointForHuman(human4HalfAlives, human3Alives);
        if (mostBest != null)
            return mostBest;
        //人能形成活四，判断在哪一边堵，堵错了玩家可能会形成三四
        mostBest = getBestPointForHuman2(human4Alives, human3Alives);
        if (mostBest != null)
            return mostBest;
        //电脑双活三
         mostBest = getBestPoint(computerDouble3Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;
        //电脑单活三
        mostBest = getBestPoint(computer3Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;
        //人双活三
        mostBest = getBestPoint(humanDouble3Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;
        //人单活三
        mostBest = getBestPoint(human3Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;
        // 电脑双活二
        mostBest = getBestPoint(computerDouble2Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;
        //电脑单活二
        mostBest = getBestPoint(computer2Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;
        //电脑半活三
        mostBest = getBestPoint(computer3HalfAlives, humanSencodResults);
        if (mostBest != null)
            return mostBest;
        //人半活四
        mostBest = getBestPoint(human4HalfAlives, computerSencodResults);
        if (mostBest != null)
            return mostBest;
        //人双活二
        mostBest = getBestPoint(humanDouble2Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;
        //人单活二
        mostBest = getBestPoint(human2Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;
        //人单半活三
        mostBest = getBestPoint(human3HalfAlives, computerSencodResults);
        return mostBest;

    }
    //人能形成双三后，判断在哪一边堵，堵错了玩家可能会形成三四
    private Point getBestPointForHuman2(List<SencondAnalysisResult> human4Alives, List<SencondAnalysisResult> human3Alives) {
        if(!human4Alives.isEmpty()){
            if (human4Alives.size()>=1){
                for (SencondAnalysisResult enemy : human3Alives){
                    if (human4Alives.contains(enemy)){
                        return enemy.point;
                    }
                }
                return human4Alives.get(0).point;
            }
            return human4Alives.get(0).point;
        }
        return null;
    }

    //人可以形成3，4
    private Point getBestPointForHuman(List<SencondAnalysisResult> myPoints, List<SencondAnalysisResult> enemyPoints) {
        if(!myPoints.isEmpty()){
            if (myPoints.size()>=1){
                for (SencondAnalysisResult enemy : enemyPoints){
                    if (myPoints.contains(enemy)){
                        return enemy.point;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private Point getBestPoint(List<SencondAnalysisResult> myPoints, List<SencondAnalysisResult> enemyPoints) {
        if(!myPoints.isEmpty()){
            if (myPoints.size()>1){
                for (SencondAnalysisResult enemy : enemyPoints){
                    if (myPoints.contains(enemy)){
                        return enemy.point;
                    }
                }
                return myPoints.get(0).point;
            }
            return myPoints.get(0).point;
        }
        return null;
    }

    //电脑第二次分析
    private Point doComputerSencondAnalysis(Map<Point,List<FirstAnalysisResult>> firstResults, List<SencondAnalysisResult> sencodResults) {
        List<FirstAnalysisResult> list = null;
        SencondAnalysisResult sr = null;
        for (Point p:firstResults.keySet()){
            sr = new SencondAnalysisResult(p);
            list =  firstResults.get(p);
            for (FirstAnalysisResult result : list){
                if (result.count==4){
                    if (result.state==ALIVE){
                        return result.point;//这是活4，下在这里就赢了
                    }else{
                        sr.halfAlive4++;
                        computer4HalfAlives.add(sr);
                    }
                }else if(result.count==3){
                    if (result.state==ALIVE){
                        sr.alive3++;
                        if (sr.alive3==2){
                            computerDouble3Alives.add(sr);
                        }else{
                            computer3Alives.add(sr);
                        }
                    }else{
                        sr.halfAlive3++;
                        computer3HalfAlives.add(sr);
                    }
                }else {//在第一次分析时就将单活二去掉了
                    sr.alive2++;
                    if (sr.alive2==2){
                        computerDouble2Alives.add(sr);
                    }else{
                        computer2Alives.add(sr);
                    }
                }
            }
            sencodResults.add(sr);
        }
        return null;
    }
    //玩家第二次分析
    private Point doHumanSencondAnalysis(Map<Point,List<FirstAnalysisResult>> firstResults, List<SencondAnalysisResult> sencodResults) {
        SencondAnalysisResult sr = null;
        List<FirstAnalysisResult> list = null;
        for (Point p:firstResults.keySet()){
            list = firstResults.get(p);
            sr = new SencondAnalysisResult(p);
            for (FirstAnalysisResult result : list){
                if (result.count==4){
                    if (result.state==ALIVE){
                        sr.alive4++;
                        human4Alives.add(sr);
                    }else{
                        sr.halfAlive4++;
                        human4HalfAlives.add(sr);
                    }
                }else if(result.count==3){
                    if (result.state==ALIVE){
                        sr.alive3++;
                        if (sr.alive3==2){
                            humanDouble3Alives.add(sr);
                        }else{
                            human3Alives.add(sr);
                        }
                    }else{
                        sr.halfAlive3++;
                        human3HalfAlives.add(sr);
                    }
                }else {//在第一次分析时就将单活二去掉了
                    sr.alive2++;
                    if (sr.alive2==2){
                        humanDouble2Alives.add(sr);
                    }else{
                        human2Alives.add(sr);
                    }
                }
            }
            sencodResults.add(sr);
        }
        return null;
    }

    private Point doFirstAnalysis(List<Point> myPoint, List<Point> humanPoints) {
        FirstAnalysisResult firstAnalysisResult;
        int x,y;
        Point computerPoint=null;
        Point humans = null;
        for (int i=0;i<allFreePoint.size();i++){
             computerPoint = allFreePoint.get(i);
             x = computerPoint.getX();
             y = computerPoint.getY();
//            x = myPoint.get(0).getX();
//            y = myPoint.get(0).getY()-1;
//            computerPoint = new Point(x,y);
//            System.out.println("aaaaaaaaaaaaaaaddddddddddd");
//            System.out.println(computerPoint);
//        System.out.println("aaaaaaaaaaaaaaaddddddddddd");
//            System.out.println(x);
//            System.out.println(y);
            //尝试在这个点下棋，在横方向看看可不可以形成4个啥的(电脑)
             firstAnalysisResult = tryAndCountResult(myPoint, humanPoints, computerPoint, HENG);
            computerPoint.setX(x).setY(y);//回复点位的原值，以供下次分析
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                 if (firstAnalysisResult.count==5){
                     return computerPoint;//下了这个棋就赢了，不在向下分析
                 }
                 addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
             }
             //纵方向
            firstAnalysisResult = tryAndCountResult(myPoint, humanPoints, computerPoint, ZONG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    return computerPoint;//下了这个棋就赢了，不在向下分析
                }
                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }
            //正斜  (/)
            firstAnalysisResult = tryAndCountResult(myPoint, humanPoints, computerPoint, ZHENG_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    return computerPoint;//下了这个棋就赢了，不在向下分析
                }
                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }
            //反斜  （\）
            firstAnalysisResult = tryAndCountResult(myPoint, humanPoints, computerPoint, FAN_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    return computerPoint;//下了这个棋就赢了，不在向下分析
                }
                addToFirstAnalysisResult(firstAnalysisResult,computerFirstResults);
            }

            //玩家
            //尝试在这个点下棋，在横方向看看可不可以形成4个啥的(玩家)
            firstAnalysisResult = tryAndCountResult(humanPoints, myPoint, computerPoint, HENG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    humans = computerPoint;//玩家下了这个棋就赢了，只能下这个
                }
                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }
            //纵方向
            firstAnalysisResult = tryAndCountResult(humanPoints, myPoint, computerPoint, ZONG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    humans = computerPoint;//玩家下了这个棋就赢了，只能下这个
                }
                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }
            //正斜  (/)
            firstAnalysisResult = tryAndCountResult(humanPoints, myPoint, computerPoint, ZHENG_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    humans = computerPoint;//玩家下了这个棋就赢了，只能下这个
                }
                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }
            //反斜  （\）
            firstAnalysisResult = tryAndCountResult(humanPoints, myPoint, computerPoint, FAN_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult!=null){//没有返回结果表示这个点在这个方向下不成5个
                if (firstAnalysisResult.count==5){
                    humans = computerPoint;//玩家下了这个棋就赢了，只能下这个
                }
                addToFirstAnalysisResult(firstAnalysisResult,humanFirstResults);
            }
        }
        return humans;
    }

    //加入到第一次分析结果中
    private void addToFirstAnalysisResult(FirstAnalysisResult result, Map<Point, List<FirstAnalysisResult>> dest) {
        if (dest.containsKey(result.point)) {
            dest.get(result.point).add(result);
        } else {
            List<FirstAnalysisResult> list = new ArrayList<FirstAnalysisResult>(1);
            list.add(result);
            dest.put(result.point, list);
        }
    }

    //第一次分析结果
    private final Map<Point, List<FirstAnalysisResult>> computerFirstResults = new HashMap<Point, List<FirstAnalysisResult>>();
    private final Map<Point, List<FirstAnalysisResult>> humanFirstResults = new HashMap<Point, List<FirstAnalysisResult>>();

    //第二次总结果
    protected final List<SencondAnalysisResult> computerSencodResults = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> humanSencodResults = new ArrayList<SencondAnalysisResult>();
    //第二次分结果，电脑
    protected final List<SencondAnalysisResult> computer4HalfAlives = new ArrayList<SencondAnalysisResult>(2);
    protected final List<SencondAnalysisResult> computerDouble3Alives = new ArrayList<SencondAnalysisResult>(4);
    protected final List<SencondAnalysisResult> computer3Alives = new ArrayList<SencondAnalysisResult>(5);
    protected final List<SencondAnalysisResult> computerDouble2Alives = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> computer2Alives = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> computer3HalfAlives = new ArrayList<SencondAnalysisResult>();

    //第二次分结果，玩家
    protected final List<SencondAnalysisResult> human4Alives = new ArrayList<SencondAnalysisResult>(2);
    protected final List<SencondAnalysisResult> human4HalfAlives = new ArrayList<SencondAnalysisResult>(5);
    protected final List<SencondAnalysisResult> humanDouble3Alives = new ArrayList<SencondAnalysisResult>(2);
    protected final List<SencondAnalysisResult> human3Alives = new ArrayList<SencondAnalysisResult>(10);
    protected final List<SencondAnalysisResult> humanDouble2Alives = new ArrayList<SencondAnalysisResult>(3);
    protected final List<SencondAnalysisResult> human2Alives = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> human3HalfAlives = new ArrayList<SencondAnalysisResult>();


    //用于临时存放数据
    private final FirstAnalysisResult far = new FirstAnalysisResult(1,null,HENG);
    //计算这个方向有多少空位,可不可以形成5个棋
    private FirstAnalysisResult tryAndCountResult(List<Point> myPoint, List<Point> enemyPoints, Point computerPoint, int direction) {
        int x = computerPoint.getX();
        int y = computerPoint.getY();

        FirstAnalysisResult fr = null;
//        System.out.println(computerPoint);
        //在此方向可不可以形成5个棋
        int maxCount = countMaxPoints(myPoint,enemyPoints,computerPoint,direction);
//        System.out.println(computerPoint);
//        System.out.println("qwerqwer");
//        System.out.println(computerPoint);
//        System.out.println(maxCount);
        if (maxCount<5){
            return null;//此方向不可能形成5个
        }else if(maxCount==5){
            //此方向上最多只能形成5个(包括空位)，只有5个位置，相当于单活
            fr = far.init(computerPoint.setX(x).setY(y),direction,HALF_ALIVE);
        }else{
            //此方向上可以形成个数大于5个，相当于双活
            fr = far.init(computerPoint.setX(x).setY(y),direction,ALIVE);
//            System.out.println(computerPoint);
//            System.out.println(fr.point);
//            System.out.println("abcabcabc");
        }

        //计算这个方向上有多少个棋子
        countPoint(myPoint,enemyPoints,computerPoint.setX(x).setY(y),fr,direction,FORWARD);
        countPoint(myPoint,enemyPoints,computerPoint.setX(x).setY(y),fr,direction,BACKWARD);
//        System.out.println(computerPoint);
//        System.out.println("111111111111");
//        System.out.println(fr.count);
//        System.out.println(fr.state);
//        System.out.println("222222");
//        System.out.println(fr.point);
//        System.out.println("333333333");

        if (fr.count <= 1 || (fr.count == 2 && fr.state == HALF_ALIVE)) {//活1，半活2及其以下结果，抛弃
            return null;
        }
        return fr.cloneMe();
    }


    private void countPoint(List<Point> myPoints, List<Point> enemyPoints, Point point, FirstAnalysisResult fr, int direction, boolean forward) {
        if (myPoints.contains(pointToNext(point, direction, forward))) {
            fr.count++;
            if (myPoints.contains(pointToNext(point, direction, forward))) {
                fr.count++;
                if (myPoints.contains(pointToNext(point, direction, forward))) {
                    fr.count++;
                    if (myPoints.contains(pointToNext(point, direction, forward))) {
                        fr.count++;
                    } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
                        fr.state = HALF_ALIVE;
                    }
                } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
                    fr.state = HALF_ALIVE;
                }
            } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
                fr.state = HALF_ALIVE;
            }
        } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
            fr.state = HALF_ALIVE;
        }
    }

//    //计算这个点在这个方向上有多少个棋子（电脑或玩家）
//    private void countPoint(List<Point> myPoint, List<Point> humanPoints, Point point, FirstAnalysisResult fr, int direction, boolean forward) {
//        //第一次判断，判断这个点的这个方向有没有我的棋
//        if(myPoint.contains(pointToNext(point,direction,forward))){
//            fr.count++;
//        }else if(humanPoints.contains(pointToNext(point,direction,forward))||pointIsOutWall(point)){
//            fr.state=HALF_ALIVE;
//        }
//        //第二次判断
//        if(myPoint.contains(pointToNext(point,direction,forward))){
//            fr.count++;
//        }else if(humanPoints.contains(pointToNext(point,direction,forward))||pointIsOutWall(point)){
//            fr.state=HALF_ALIVE;
//        }
//        //第三次判断
//        if(myPoint.contains(pointToNext(point,direction,forward))){
//            fr.count++;
//        }else if(humanPoints.contains(pointToNext(point,direction,forward))||pointIsOutWall(point)){
//            fr.state=HALF_ALIVE;
//        }
//        //第四次判断
//        if(myPoint.contains(pointToNext(point,direction,forward))){
//            fr.count++;
//        }else if(humanPoints.contains(pointToNext(point,direction,forward))||pointIsOutWall(point)){
//            fr.state=HALF_ALIVE;
//        }
//    }

    //棋子出了墙
    private boolean isOutSideOfWall(Point point, int direction) {
        if (direction == HENG) {
            return point.getX() < 0 || point.getX() >= maxX;//最大的X和Y值均在墙外所以用等号
        } else if (direction == ZONG) {
            return point.getY() < 0 || point.getY() >= maxY;
        } else {//这里可能有问题
            return point.getX() < 0 || point.getY() < 0 || point.getX() >= maxX || point.getY() >= maxY;
        }
    }

    //得到这个点的下一个或上一个
    private Point pointToNext(Point point, int direction, boolean forward) {
        switch (direction) {
            case HENG:
                if (forward)
                    point.setX(point.getX()+1);
                else
                    point.setX(point.getX()-1);
                break;
            case ZONG:
                if (forward)
                    point.setY(point.getY()+1);
                else
                    point.setY(point.getY()-1);
                break;
            case ZHENG_XIE:
                if (forward) {
                    point.setX(point.getX()+1);
                    point.setY(point.getY()-1);
                } else {
                    point.setX(point.getX()-1);
                    point.setY(point.getY()+1);
                }
                break;
            case FAN_XIE:
                if (forward) {
                    point.setX(point.getX()+1);
                    point.setY(point.getY()+1);
                } else {
                    point.setX(point.getX()-1);
                    point.setY(point.getY()-1);
                }
                break;
        }
        return point;
    }

    private int countMaxPoints(List<Point> myPoint, List<Point> humanPoints, Point computerPoint, int direction) {
        int x=computerPoint.getX();
        int y=computerPoint.getY();
        int count=1;
        switch (direction){
            case HENG:
                while (!humanPoints.contains(computerPoint.setX(computerPoint.getX()-1))&&computerPoint.getX()>=0&&count<6){
                    count++;
                }
                computerPoint.setX(x);
                while (!humanPoints.contains(computerPoint.setX(computerPoint.getX()+1))&&computerPoint.getX()<=15&&count<6){
                    count++;
                }
                break;
            case ZONG:
                while (!humanPoints.contains(computerPoint.setY(computerPoint.getY()-1))&&computerPoint.getY()>=0&&count<6){
                    count++;
                }
                computerPoint.setY(y);
                while (!humanPoints.contains(computerPoint.setY(computerPoint.getY()+1))&&computerPoint.getY()<=15&&count<6){
                    count++;
                }
                break;
            case FAN_XIE:
                while (!humanPoints.contains(computerPoint.setX(computerPoint.getX()-1).setY(computerPoint.getY()-1))&&computerPoint.getX()>=0&&computerPoint.getY()>=0&&count<6){
                    count++;
                }
                computerPoint.setX(x).setY(y);
                while (!humanPoints.contains(computerPoint.setX(computerPoint.getX()+1).setY(computerPoint.getY()+1))&&computerPoint.getX()<=15&&computerPoint.getY()<=15&&count<6){
                    count++;
                }
                break;
            case ZHENG_XIE:
                while (!humanPoints.contains(computerPoint.setX(computerPoint.getX()-1).setY(computerPoint.getY()+1))&&computerPoint.getX()>=0&&computerPoint.getY()<=15&&count<6){
                    count++;
                }
                computerPoint.setX(x).setY(y);
                while (!humanPoints.contains(computerPoint.setX(computerPoint.getX()+1).setY(computerPoint.getY()+1))&&computerPoint.getX()<=15&&computerPoint.getY()>=0&&count<6){
                    count++;
                }

        }
        return count;
    }


    private class FirstAnalysisResult {
         int count ;
         Point point;
         int direction;
         int state;
        public FirstAnalysisResult(int count, Point point, int direction) {
            this(count, point, direction, ALIVE);
        }

        public FirstAnalysisResult(int count, Point point, int direction, int state) {
            this.count=count;
            this.point=point;
            this.direction=direction;
            this.state=state;
        }

        public FirstAnalysisResult init(Point computerPoint, int direction, int state) {
            this.point=computerPoint;
            this.direction=direction;
            this.state=state;
            this.count=1;
            return this;
        }

        public FirstAnalysisResult cloneMe() {
            return new FirstAnalysisResult(count,point,direction,state);
        }
    }

    private class SencondAnalysisResult {
        int alive4 = 0;
        //活3数量
        int alive3 = 0;
        //半活4，一头封的
        int halfAlive4 = 0;
        //半活3，一头封的
        int halfAlive3 = 0;
        //活2数量
        int alive2 = 0;
        //点位
        Point point;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((point == null) ? 0 : point.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            SencondAnalysisResult other = (SencondAnalysisResult) obj;
            if (point == null) {
                if (other.point != null)
                    return false;
            } else if (!point.equals(other.point))
                return false;
            return true;
        }

        private SencondAnalysisResult(Point point) {
            this.point = point;
        }


//        //第三次分析时，对第二次分析结果进行排序，此为排序回调函数
//        @Override
//        public int compareTo(SencondAnalysisResult another) {
//            return compareTowResult(this, another);
//        }
    }
    //清理上一次分析结果
    private void initAnalysisResult() {
        computerFirstResults.clear();
        humanFirstResults.clear();
        //第二次总结果
        computerSencodResults.clear();
        humanSencodResults.clear();
        //第二次分结果
        computer4HalfAlives.clear();
        computerDouble3Alives.clear();
        computer3Alives.clear();
        computerDouble2Alives.clear();
        computer2Alives.clear();
        computer3HalfAlives.clear();

        //第二次分结果，人类
        human4Alives.clear();
        human4HalfAlives.clear();
        humanDouble3Alives.clear();
        human3Alives.clear();
        humanDouble2Alives.clear();
        human2Alives.clear();
        human3HalfAlives.clear();
        System.gc();
    }

    //玩家先手，电脑走的第一步棋
    private Point myFirstPoint(List<Point> humanPoints) {
        Point point = humanPoints.get(0);
        if (point.getX() == 0 || point.getY() == 0 || point.getX() == 15 && point.getY() == 15)
            return new Point(15 / 2, 15 / 2);
        else {
            return new Point(point.getX() - 1, point.getY());
        }
    }



    @Override
    public void run(Point point, List<Point> humanPoints) {
        //这里可能还需要将allFreePoint更新，因为两个computer human不一样,我没更新
        Point result = null;
        result = doAnalysis(myPoint, humanPoints);
        System.out.println(result);
        myPoint.add(result);
        allFreePoint.remove(result);
    }

}
