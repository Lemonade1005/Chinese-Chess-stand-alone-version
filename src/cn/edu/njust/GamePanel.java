package cn.edu.njust;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.JPanel;

public class GamePanel extends JPanel{
	//定义一个保存所有棋子的成员变量，Chess类型的数组(定义成员变量的目的是需要生命周期长的变量来保存棋子)
	//0到15为红子，16到32为黑子
	private Chess[] chesses = new Chess[32];
	//当前选中的棋子
	private Chess selectedChess;
	//当前棋子的阵营
	private int curPlayer = 0;
	
	
	//构造方法，目的是一创建游戏面板类便创建并保存所有棋子
	public GamePanel(){
		createChesses();  //创建所有棋子，并保存到数组中
		
		//如何操作棋子？
		//1、点击棋盘
		//2、判断点击的地方是否有棋子
		//3、区分第一次选择棋子、重新选择棋子、棋子移动、吃子
		
		//棋盘规则
		//1、不可以操作对方的棋子:curPlayer保存当前棋子阵营
		//2、一方走完后，另一方才能走:
		
		addMouseListener(new MouseAdapter() {  //鼠标监听事件
			@Override
			//添加点击事件
			public void mouseClicked(MouseEvent e){
				System.out.println("点击的坐标为：x=" + e.getX() + " y=" + e.getY());
				Point p = Chess.getPointFromXY(e.getX(), e.getY());  //p是根据鼠标点击的位置转换成网格坐标后的Point实例
				System.out.println("点击的网格坐标对象为：p=" + p);
				//判断第一次选择or重新选择or移动or吃子
				if(selectedChess == null){
					//第一次点击
					selectedChess = getChessbyP(p);
					if(selectedChess != null && selectedChess.getPlayer() != curPlayer){  //选中的棋子不为空且阵营与当前棋子的阵营不同时，赋为空，无法操作
						selectedChess = null;
					}
				}else{
					//第n次点击:重新选择or移动or吃子
					Chess c = getChessbyP(p);  
					if(c != null){
						//第n次点击时有棋子:重新选择or吃子----根据棋子的属性player判断
						if(c.getPlayer() == selectedChess.getPlayer()){
							//重新选择
							System.out.println("重新选择");
							selectedChess = c;  //把第n次选中的棋子赋给当前选中的棋子
						}else{
							//吃子
							System.out.println("吃子");
							if(selectedChess.isAbleMove(p, GamePanel.this)){  //传入GamePanel游戏面板类对象gamePanel，不是点击事件对象，不能只用this
								//思路:从数组中删除被吃掉的棋子、修改要移动的棋子的坐标
								chesses[c.getIndex()] = null;  //删除第n次选择的棋子c
								selectedChess.setP(p);  //修改要移动的棋子的坐标，p是第n次选择棋子的网格坐标
								overMyTurn();  //结束回合
							}
						}
					}else{
						//第n次点击时没有棋子，点的是空白处:移动
						System.out.println("移动");
						if(selectedChess.isAbleMove(p, GamePanel.this)){  //若能够移动，则修改选中棋子的网格坐标
							selectedChess.setP(p);
							overMyTurn();  //结束回合
						}
					}
				}
				
				System.out.println("点击的棋子对象为：selectedChess===" + selectedChess);
				//每次点击后刷新棋盘，即重新执行paint()方法，否则点击后没有选中的黑框
				repaint();
			}
		});
	}
	
	
	
	//结束玩家回合，在移动和吃子后使用，重新选择棋子时不使用
	private void overMyTurn(){
		curPlayer = curPlayer == 0 ? 1 : 0;  //当前棋子的阵营只在0、1之间切换
		selectedChess = null;  //走完以后当前棋子为空
	}

	//根据网格坐标对象p查找棋子对象
	public Chess getChessbyP(Point p){
		for(Chess item : chesses){  //遍历棋子数组，若传入网格坐标与棋子的网格坐标相同则找到棋子
			if(item != null && item.getP().equals(p)){  //要判断不为空才画棋子，是因为实现吃子逻辑时将chesses数组元素赋为空了
				return item;
			}
		}
		
		return null;
	}
	
	//创建所有棋子，并保存到数组中
	private void createChesses(){
		String[] names = new String[]{"che", "ma", "xiang", "shi", "boss", "shi", "xiang", "ma",
				"che", "pao", "pao", "bing", "bing", "bing", "bing", "bing"};
		Point[] ps = {new Point(1, 1), new Point(2, 1), new Point(3, 1), new Point(4, 1), 
				new Point(5, 1), new Point(6, 1), new Point(7, 1), new Point(8, 1), new Point(9, 1), 
				new Point(2, 3), new Point(8, 3), new Point(1, 4), new Point(3, 4), new Point(5, 4), 
				new Point(7, 4), new Point(9, 4)};
		//黑子
		for(int i = 0; i < names.length; i++){
			Chess c = new Chess();  //创建棋子对象，根据数组中的数据为每个棋子的属性赋值
			c.setName(names[i]);  //指定棋子名称
			c.setP(ps[i]);  //指定棋子网格坐标
			c.setPlayer(1);  //指定棋子阵营
			c.setIndex(i);  //设置棋子在数组中的索引
			chesses[c.getIndex()] = c;  //将棋子保存到数组中
		}
		//红子
		for(int i = 0; i < names.length; i++){
			Chess c = new Chess();  //创建棋子对象，根据数组中的数据为每个棋子的属性赋值
			c.setName(names[i]);
			c.setP(ps[i]);
			c.setPlayer(0);
			c.reverse();
			c.setIndex(i + 16);  //设置棋子在数组中的索引
			chesses[c.getIndex()] = c;  //将棋子保存到数组中(注意这里要加16,否则红子会覆盖掉数组中的黑子)
		}
		
		System.out.println(Arrays.toString(chesses));  //创建完棋子之后调用重写的toString()方法打印所有棋子信息，方便调试
	}
	
	
	//绘制所有棋子
	private void drawChesses(Graphics g){  //for-each循环
		for(Chess item : chesses){
			if(item != null){  //要判断不为空才画棋子，是因为实现吃子逻辑时将chesses数组元素赋为空了
				item.draw(g, this);  //调用Chess类中的draw()方法
			}
		}
	}
	
	@Override
	//绘制，包括绘制棋盘和调用绘制棋子的方法
	public void paint(Graphics g){
		//绘制图片步骤:
		//1、获取图像
		String bgPath = "pic" + File.separator + "qipan.jpg";
		//2、获取图像实例
		Image bgImg = Toolkit.getDefaultToolkit().getImage(bgPath);
		//3、调用Graphics实例的getImage()方法
		g.drawImage(bgImg, 0, 0, this);  //绘制棋盘
		
		drawChesses(g);  //调用绘制棋子的方法
		
		if(selectedChess != null){
			selectedChess.drawRect(g);
		}
		
		//第二个版本
//		String[] names = new String[]{"che", "ma", "xiang", "shi", "boss", "shi", "xiang", "ma",
//				"che", "pao", "pao", "bing", "bing", "bing", "bing", "bing"};
//		Point[] ps = {new Point(1, 1), new Point(2, 1), new Point(3, 1), new Point(4, 1), 
//				new Point(5, 1), new Point(6, 1), new Point(7, 1), new Point(8, 1), new Point(9, 1), 
//				new Point(2, 3), new Point(8, 3), new Point(1, 4), new Point(3, 4), new Point(5, 4), 
//				new Point(7, 4), new Point(9, 4)};
//		//黑子
//		for(int i = 0; i < names.length; i++){
//			Chess c = new Chess();  //创建棋子对象，根据数组中的数据为每个棋子的属性赋值
//			c.setName(names[i]);  //指定棋子名称
//			c.setP(ps[i]);  //指定棋子网格坐标
//			c.setPlayer(1);  //指定棋子阵营
//			chesses[i] = c;  //将棋子保存到数组中
//		}
//		//红子
//		for(int i = 0; i < names.length; i++){
//			Chess c = new Chess();  //创建棋子对象，根据数组中的数据为每个棋子的属性赋值
//			c.setName(names[i]);
//			c.setP(ps[i]);
//			c.setPlayer(0);
//			c.reverse();
//			chesses[i + 16] = c;  //将棋子保存到数组中(注意这里要加16,否则红子会覆盖掉数组中的黑子)
//		}
		

		//第一个版本
		//利用数组和循环绘制棋子
//		String[] names = new String[]{"che", "ma", "xiang", "shi", "boss", "shi", "xiang", "ma", "che", "pao", "pao", "bing", "bing", "bing", "bing", "bing"};  //储存棋子图片路径(一部分)的数组
//		int player = 0;  //棋子的阵营
//		//规定棋盘:横向9个点从1到9、纵向十个点从1到10
//		int[] xs = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 8, 1, 3, 5, 7, 9};  //储存棋子在棋盘上的位置的x坐标的数组
//		int[] ys = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 4, 4, 4, 4, 4};  //储存棋子在棋盘上的位置的y坐标的数组
//		int size = 30;  //棋子大小
//		int margin = 20;  //棋子外边距
//		int space = 40; //棋子之间的间距
//		//绘制红子
//		for(int i = 0; i < names.length; i++){
//			String path = "pic" + File.separator + names[i] + player + ".png";  //将图片路径拼接起来
//			Image img = Toolkit.getDefaultToolkit().getImage(path);
//			g.drawImage(img, margin - size / 2 + space * (xs[i] - 1), margin - size / 2 + space * (ys[i] - 1), size, size, this);  //计算过程自己体会
//		}
//		//绘制黑子
//		player = 1;
//		for(int i = 0; i < names.length; i++){
//			String path = "pic" + File.separator + names[i] + player + ".png";  //将图片路径拼接起来
//			Image img = Toolkit.getDefaultToolkit().getImage(path);
//			g.drawImage(img, margin - size / 2 + space * (reserveX(xs[i]) - 1), margin - size / 2 + space * (reserveY(ys[i]) - 1), size, size, this);  //计算过程自己体会
//		}
//		//这些注释掉是因为都写到Chess类的paint方法中了
		
		
	}
	
	//反转棋子在棋盘上的x坐标
	private int reserveX(int x){
		return 10 - x;
	}
	
	//反转棋子在棋盘上的x坐标
	private int reserveY(int y){
		return 11 - y;
	}
}











