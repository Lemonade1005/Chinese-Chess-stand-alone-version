package cn.edu.njust;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
	
	public static void main(String[] args) {
		JFrame frm = new JFrame();
		frm.setSize(400, 500);
		frm.setLocationRelativeTo(null);  //窗口居中
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //设置点击窗口的关闭按钮后结束JVM(每个Java程序都是一个虚拟机)
		frm.add(new GamePanel());  //将游戏面板添加到窗口中
		frm.setVisible(true);
	}
}
