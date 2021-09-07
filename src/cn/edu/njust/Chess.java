package cn.edu.njust;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JPanel;

public class Chess {
	private static final int SIZE = 30;  //棋子大小
	private static final int MARGIN = 20;  //棋盘外边距
	private static final int SPACE = 40;  //棋子间距
	private String name;  //棋子名称
	private String suffix = ".png";  //棋子图片后缀
	private int player;  //棋子阵营，0:红  1:黑
	private int x, y;  //棋子绘制时的坐标位置
	private Point p;  //棋子的网格坐标(Point类)
	private Point initP;  //棋子的初始网格坐标，不可改变(判断将帅在王宫中移动时红黑双方不确定在上还是在下，但需要保证己方玩家在下面，故引入此变量)
	private int index;  //保存每个棋子的索引位置(实现吃子的逻辑时，需要知道chesses数组中每个棋子的索引，故引入此变量)
	
	
	//setter和getter方法
	public String getName() {
		return name;
	}
 void setName(String name) {
		this.name = name;
	}
	
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {  //有修改，这里的改动是因为棋盘中上半部分和下半部分的棋子共用一套Point坐标，会导致上半部分棋子绘制不出来或者点击为null，reverse()方法也有改动
		this.p = (Point) p.clone();  //将传入的网格坐标(参数)的拷贝赋给棋子的网格坐标(属性)
		if(initP == null){
			initP = this.p;  //保证网格坐标只能赋值一次
		}
		calXY();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	
	//提取棋子规则的方法(代码的封装性)
	//判断棋子初始在上方还是下方    1:上  2:下  0:其他
	public int isUpOrDown(){
		if(initP.y < 6){  //上方返回1
			
			return 1;
		}else if(initP.y > 5){  //下方返回2
			
			return 2;
		}
		
		return 0;  //其他情况返回0
	}
	
	//判断目标点是否在王宫范围内    true:在  false:不在
	public boolean isHome(Point tp){
		if(tp.x < 4 || tp.x > 6){  //限定x坐标
			return false;
		}
		int upOrDown = isUpOrDown();
		if(upOrDown == 1){  //上方
			if(tp.y < 1 || tp.y > 3){
				return false;
			}
		}else if(upOrDown == 2){  //下方
			if(tp.y < 8 || tp.y > 10){
				return false;
			}
		}
		
		return true;
	}
	
	//判断起点和目标点是否成直线、正斜线    -2:其他情况  -1:纵向日字  0:横向日字  1:正斜线  2:y轴直线  3:x轴直线  (把正斜线返回1是为了之后判断方便)
	public int line(Point tp){
		if(Math.abs(p.x - tp.x) == Math.abs(p.y - tp.y)){
			//正斜线
			return 1;
		}else if(p.x == tp.x){
			//y轴直线
			return 2;
		}else if(p.y == tp.y){
			//x轴直线
			return 3;
		}else{
			//日字
			if(Math.abs(p.x - tp.x) == 2 && Math.abs(p.y - tp.y) == 1){
				//横向日字
				return 0;  //之前写的0代表其他情况，考虑到情况增多，故数字向下扩展
			}else if(Math.abs(p.x - tp.x) == 1 && Math.abs(p.y - tp.y) == 2){
				//纵向日字
				return -1;
			}
		}
		
		return -2;  //其他情况返回-2
//		return 0;  //其他情况返回0
	}

	//计算起点和目标点之间走了几格    返回格数，0为非直线非斜线的其他情况
	public int getStep(Point tp){
		int line = line(tp);
		if(line == 1){  
			//正斜线
			return Math.abs(p.x - tp.x);
		}else if(line == 2){
			//y轴直线
			return Math.abs(p.y - tp.y);
		}else if(line == 3){
			//x轴直线
			return Math.abs(p.x - tp.x);
		}
		
		return 0;
	}
	
	//判断象、相和马是否蹩脚    true:蹩脚  false:不蹩脚
	public boolean isBieJiao(Point tp, GamePanel gamePanel){
		Point center = new Point();  //对象来说是中心点，对马来说是蹩脚点
		if("xiang".equals(name)){
			//象、相的中心点
			center.x = (p.x + tp.x) / 2;
			center.y = (p.y + tp.y) / 2;
			if(gamePanel.getChessbyP(center) == null){  //中心点center处为空则不蹩脚，返回false
//				System.out.println("没蹩脚");
				return false;  
			}
			
		}else if("ma".equals(name)){
			//马的蹩脚点
			int line = line(tp);
			if(line == 0){
				//横向日字
				center.y = p.y;  //网格坐标y坐标一致
				center.x = (p.x + tp.x) / 2;  //网格坐标的x坐标在起点与目标点中间
				if(gamePanel.getChessbyP(center) == null){  //中心点center处为空则不蹩脚，返回false
					return false;
				}
			}else if(line == -1){
				//纵向日字
				center.x = p.x;  //网格坐标x坐标一致
				center.y = (p.y + tp.y) / 2;  //网格坐标的y坐标在起点与目标点中间
				if(gamePanel.getChessbyP(center) == null){  //中心点center处为空则不蹩脚，返回false
					return false;
				}
			}
		}
//		System.out.println("蹩脚了");
		return true;
	}
	
	//判断棋子到目标点是否过河    true:过河  false:没过河   注意兵和象传入的参数不同，象传入的是目标点，兵传入的是移动前的点(兵是判断自身所在的点有没有过河，再对走棋进行进一步判断)
	public boolean isOverRiver(Point tp){
		int upOrDown = isUpOrDown();
		if(upOrDown == 1){
			//上方
			if(tp.y > 5){
				return true;
			}
			
		}else if(upOrDown == 2){
			//下方
			if(tp.y < 6){
				return true;
			}
			
		}
		System.out.println("没有过河");
		return false;
	}
	
	//计算起始点和目标点之间的棋子数量(不包括起点和目标点)
	public int getCount(Point tp, GamePanel gamePanel){  //因为要获取网格坐标上的棋子，要使用getChessByP()，还需传入GamePanel类的对象
		int start = 0;  //起点(是指起始点往终点方向的下一格，注意区分)注意start和end是计算直线上棋子数量的两个点，仅代表计算的起点和终点，其方向不一定与棋子走的方向相同
		int end = 0;  //终点(就是目标点，因为循环里写的是小于)
		int count = 0;  //统计棋子数量
		int line = line(tp);
		Point np = new Point();  //局部变量，为区别于Chess的属性p，用np命名，用于循环中，一直在变
		//先判断是x轴直线还是y轴直线
		if(line == 2){
			//y轴直线
			np.x = tp.x;  //在y轴上则x坐标相同
			//再判断从哪个方向到哪个方向
			if(tp.y > p.y){
				//从上到下
				start = p.y + 1;
				end = tp.y;  //要注意的是，由于循环里的条件start < end，所以四种情况里的end要么是p的横或纵坐标、要么是tp的横或纵坐标，没有加一减一啥的
//				System.out.println("start:" + start);
//				System.out.println("end:" + end);
			}else if(tp.y < p.y){
				//从下到上
				start = tp.y + 1;
				end = p.y;
//				System.out.println("start:" + start);
//				System.out.println("end:" + end);
			}
			for(int i = start; i < end; i++){  //注意在之前的赋值中，start要保证小于end，否则这里的循环会出错(哪怕计算的直线方向与棋子走的方向不一致，也没有问题)
				np.y = i;  //网格坐标p在循环中不断变化
				if(gamePanel.getChessbyP(np) != null){  
					count++;
				}
			}
		}else if(line == 3){
			//x轴直线
			np.y = tp.y;  //在x轴上则y坐标相同
			if(tp.x > p.x){
				//从左到右
				start = p.x + 1;
				end = tp.x;
//				System.out.println("start:" + start);
//				System.out.println("end:" + end);
			}else if(tp.x < p.x){
				//从右到左
				start = tp.x + 1;
				end = p.x;
//				System.out.println("start:" + start);
//				System.out.println("end:" + end);
			}
			for(int i = start; i < end; i++){
				np.x = i;  //网格坐标p在循环中不断变化
				if(gamePanel.getChessbyP(np) != null){  
					count++;
				}
			}
		}
//		System.out.println("中间有" + count + "个棋子");
		return count;
	}
	
	//判断棋子是否后退    true:后退  false:没有后退(包括前进和左右移动)
	public boolean isBackward(Point tp){
		int upOrDown = isUpOrDown();
		if(upOrDown == 1){
			//上方
			if(tp.y < p.y){  
				return true;
			}
		}else if(upOrDown == 2){
			//下方
			if(tp.y > p.y){  
				return true;
			}
		}
		
		return false;
	}
	
	
	
	//判断棋子能否移动到指定位置    true:能  false:不能
	public boolean isAbleMove(Point tp, GamePanel gamePanel){  //tp表示目标点的网格坐标，由于要判断蹩脚问题，需要在chesses数组中看是否有棋子处于蹩脚点，故参数中要传入该数组，现在出现的问题是，要在Chess类(本类)中使用GamePanel类中的getChessbyP(Point p)方法，解决方法:将传入的数组改为GamePanel类
		if("boss".equals(name)){
			//将、帅
			return line(tp) > 1 && isHome(tp) && getStep(tp) == 1;  //直线、在王宫内、步数为1
		}else if("shi".equals(name)){
			//仕、士
			return line(tp) == 1 && isHome(tp) && getStep(tp) == 1;  //正斜线、在王宫内、步数为1
		}else if("xiang".equals(name)){
			//象、相
//			if(line(tp) == 1 && getStep(tp) == 2 && !isBieJiao(tp, gamePanel) && !isOverRiver(tp)){  //这一句导致象哪都能走不知道为啥【？】
//				return true;  
//			}
			return line(tp) == 1 && getStep(tp) == 2 && !isBieJiao(tp, gamePanel) && !isOverRiver(tp);  //正斜线、步数为2、不蹩脚、不过河
		}else if("ma".equals(name)){
			//马
			return (line(tp) == 0 || line(tp) == -1) && !isBieJiao(tp, gamePanel);  //日字、不蹩脚
		}else if("che".equals(name)){
			//车
			return line(tp) > 1 && (getCount(tp, gamePanel) == 0);  //直线、中间没有棋子
		}else if("pao".equals(name)){
			//炮
			Chess c = gamePanel.getChessbyP(tp);  //Chess实例c接收目标点tp处的棋子
			if(c != null){  //目标处有棋子
				if(c.getPlayer() != this.player){  //棋子阵营不同，吃子
					return line(tp) > 1 && (getCount(tp, gamePanel) == 1);  //直线、中间有一颗棋子
				}
			}else{
				//移动
				return line(tp) > 1 && (getCount(tp, gamePanel) == 0);  //直线、中间没有棋子
			}
		}else if("bing".equals(name)){
			//卒、兵
			if(line(tp) < 2 || getStep(tp) > 1){  //只能走直线、只能走一格
				return false;
			}
			if(isBackward(tp)){  //只能前进
				return false;
			}
			if(!isOverRiver(p) && Math.abs(tp.x - p.x) > 0){  //不能未过河就左右移动
				return false;
			}
		}
		
		return true;
	}
	
	
	//绘制棋子的方法
	public void draw(Graphics g, JPanel panel){
		String path = "pic" + File.separator + name + player + suffix;  //将图片路径拼接起来
		Image img = Toolkit.getDefaultToolkit().getImage(path);
		g.drawImage(img, x, y, SIZE, SIZE, panel);
	}
	
	//为选择好的棋子画一个矩形边框，表示选中棋子
	public void drawRect(Graphics g){
		g.drawRect(x, y, SIZE, SIZE);
	}
	
	//计算实际坐标x、y的方法
	public void calXY(){
		x = MARGIN - SIZE / 2 + SPACE * (p.x - 1);
		y = MARGIN - SIZE / 2 + SPACE * (p.y - 1);
	}	
	
	//根据实际坐标x、y,计算相应的网格坐标p,和calXY()方法是相反的
	//static修饰的方法，为类方法或静态方法，可以使用类名.方法()或实例.方法()调用，只能使用类属性(属性要加static)
	public static Point getPointFromXY(int x, int y){
		Point p = new Point();  //要重新创建一个对象
		p.x = (x - MARGIN + SIZE / 2) / SPACE + 1;
		p.y = (y - MARGIN + SIZE / 2) / SPACE + 1;
		if(p.x < 1 || p.x > 9 || p.y < 1 || p.y > 10){  //鼠标点击的位置在网格之外无意义，返回空
			return null;
		}
		return p;
	}
	
	//反转棋子的网格坐标
	public void reverse(){
		p.x = 10 - p.x;
		p.y = 11 - p.y;
		initP = p; //reverse()方法只调用一次，不用if判断了
		calXY();
	}

	
	
	@Override
	//toString()方法打印棋子信息
	public String toString() {
		return "Chess [name=" + name + ", suffix=" + suffix + ", player="
				+ player + ", x=" + x + ", y=" + y + ", p=" + p + ", initP="
				+ initP + ", index=" + index + "]";
	}
	
}
