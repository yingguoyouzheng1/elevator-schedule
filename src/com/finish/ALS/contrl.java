package com.finish.ALS;

import com.software.elevator.test.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class contrl {
    private String closedoor="关门";
    private String close="关";
    private String open="开";
    JFrame frame=new JFrame("电梯调度算法");//窗体
    JMenuBar menu=new JMenuBar();//菜单栏
    JPanel pan=new JPanel();//面板
    JButton but=null;//按钮
    JButton[] sign=new JButton[4];
    JButton jstate=new JButton(closedoor+"");
    JButton openkey=new JButton(open+"");
    JButton closekey=new JButton(close+"");
    private ArrayList[] fisrt;
    private ArrayList numberBrick; //numberBrick用来依次存放button,程序用button显示桔黄色表示电梯内的数字键被按下。
    private ArrayList controlBrick; //controlBrick用来依次存放button,表示每层楼电梯口的是上键和下键，程序用button显示绿色表示对应键被按下。
    private int[][] controlTable; //用二维数组controlTable与电梯口的上下键对应，其中如果值1表示button应显示绿色，值0表示button应显示灰色。
    private int[]table; //table电梯内部数字，1表示按下，0表示未按下。
    private int[] upSignalTable; //记录向上的楼层，1表示按下，0表示未按下。
    private int[] downSignalTable; //记录向下的楼层，1表示按下，0表示未按下。
    private Timer timer; //计时器。
    elevator lift=new elevator();


    //================================================构造方法===========================================================//

    public contrl() {
        JMenu elevator=new JMenu("选择调度方式");
        elevator.add(new JMenuItem("傻瓜电梯"));
        elevator.add(new JMenuItem("捎带电梯"));
        elevator.add(new JMenuItem("多部捎带电梯"));
        menu.add(elevator);
        for(int i=1;i<5;i++){
            sign[i-1]=new JButton();
            sign[i-1].setBounds((i-1)*80,0,80,40);
        }

//开关门部分
        jstate.setBounds(320, 0, 80, 40);
        jstate.setBackground(Color.yellow);

        openkey.setBounds(400, 0, 80, 40);
        openkey.addActionListener(new OpenListener()); //开门按钮添加事件

        closekey.setBounds(480, 0, 80, 40);
        closekey.addActionListener(new CloseListener()); //关门按钮添加事件

        pan.setLayout(null); //画布绝对定位
        Font fnt=new Font("Serief",Font.ITALIC,15); //设置字体样式

//实例化数组,初始化数组
        numberBrick=new ArrayList(10);
        controlBrick = new ArrayList(10);
        fisrt =new ArrayList[4];//电梯位置按钮
        for(int i=0;i<4;i++){
            fisrt[i]=new ArrayList(10);
        }
        controlTable = new int[10][2];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 2; j++)
                controlTable[i][j] = 0;

        table = new int[10];
        for (int i = 0; i < 10; i++)
            table[i] = 0;

        upSignalTable = new int[10];
        downSignalTable = new int[10];

        for (int i = 0; i < 10; i++) {
            upSignalTable[i] = 0;
            downSignalTable[i] = 0;
        }

//********************画图************************
        for(int i=10;i>0;i--){

//电梯
            for(int j=0;j<4;j++) {
                but = new JButton();
                but.setFont(fnt);
                but.setBounds(j*80, (11 - i) * 40, 80, 40);
                but.setBackground(Color.BLACK);
                fisrt[j].add(but);
                pan.add(but);
            }

//显示楼层
            but=new JButton(+i+"楼");
            but.setFont(fnt);
            but.setBounds(320, (11-i)*40, 80, 40);
            but.addActionListener(new NumberListener(i));
            but.setBackground(Color.gray);
            numberBrick.add(but);
            pan.add(but);

//向上键
            but = new JButton("上");
            but.setFont(fnt);
            but.setBounds(400, (11 - i) * 40, 80, 40);
            if(i!=10) {
                but.addActionListener(new UpListener(i));
            }
            else but.setText(" ");
            but.setBackground(Color.gray);
            controlBrick.add(but);
            pan.add(but);
//向下键

            but = new JButton("下");
            but.setFont(fnt);
            but.setBounds(480, (11 - i) * 40, 80, 40);
            if(i!=1) {
                but.addActionListener(new DownListener(i));
            }
            else but.setText(" ");
            but.setBackground(Color.gray);
            controlBrick.add(but);
            pan.add(but);

        }

//*******************运行部分******************

        pan.add(sign[0]);
        pan.add(sign[1]);
        pan.add(sign[2]);
        pan.add(sign[3]);
        pan.add(jstate);
        pan.add(openkey);
        pan.add(closekey);
        frame.add(pan);
        frame.setJMenuBar(menu);
        frame.setSize(579, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        sign[0].setText(1+"楼");
        DrawBrick();
        new Setime();
        }

    //*************************************************画图*******************************************************
    public void DrawBrick() { //电梯运行
        if (lift.getEstate() == 1) {
            ((JButton) fisrt[0].get(11 - lift.getNumber())).setBackground(Color.black);
            ((JButton) fisrt[0].get(10 - lift.getNumber())).setBackground(Color.blue);//刷蓝
        } else if (lift.getEstate() == 2) {
            ((JButton) fisrt[0].get(9 - lift.getNumber())).setBackground(Color.black);
            ((JButton) fisrt[0].get(10 - lift.getNumber())).setBackground(Color.blue);//刷蓝
        } else {
            ((JButton) fisrt[0].get(10 - lift.getNumber())).setBackground(Color.blue);//刷蓝
        }
    }

    public void DrawControlBrick(int i, int j, int k) { //外部按钮
        if (k == 1) ((JButton) controlBrick.get(i * 2 + j)).setBackground(Color.red);
        else ((JButton) controlBrick.get(i * 2 + j)).setBackground(Color.gray);
    }

    public void DrawNumberBrick(int i, int k) { //数字按钮
        if (k == 1)
            ((JButton) numberBrick.get(i)).setBackground(Color.orange);
        else
            ((JButton) numberBrick.get(i)).setBackground(Color.gray);
    }

    public void setState(int number) { //如果电梯原来停，则改变电梯状态
        if(lift.getEstate()==1&& lift.getDestination()<number){
            lift.setDestination(number);
        }
        else if(lift.getEstate()==2&& lift.getDestination()>number){
            lift.setDestination(number);
        }
        else if(lift.getEstate()==0) {//电梯不动的时候，第一个输入为目的楼层
            lift.setDestination(number);
        }
    }

    
    //*****************************************监听处理****************************************

    public class UpListener implements ActionListener{ //相应上键被按下的动作。
        int L = 1; //当前楼层。
        public UpListener(int n) {
            L = n;
        }
        public void actionPerformed(ActionEvent e){ //重新显示上下键的颜色并将向上任务加入向上任务数组。
            System.out.println("↑按下"+L);
            controlTable[10 - L][0] = 1;
            setState(L);
            DrawControlBrick(10-L,0,1);
        }
    }

    public class DownListener implements ActionListener{ //相应下键被按下的动作。
        int L=0;
        public DownListener(int n){
            L=n;
        }
        public void actionPerformed(ActionEvent e) {
            System.out.println("↓按下"+L);
            controlTable[10-L][1]=1;
            setState(L);
            DrawControlBrick(10-L,1,1) ;
        }
    }

    public class NumberListener implements ActionListener { //相应数字键被按下的动作。
        int number = 0; //相应的数字键楼层。
        public NumberListener(int n) {
            number = n;
        }
        public void actionPerformed(ActionEvent e) {
            System.out.println("楼层按下"+number);
            table[10 - number] = 1;
            setState(number);
            DrawNumberBrick(10-number,1);
        }
    }

    public class OpenListener implements ActionListener { //开门关门按钮
        OpenListener() {
        }
        public void actionPerformed(ActionEvent e) {
            if(lift.getEstate()==0) {
                new openclosThread();
            }
        }
    }

    public class CloseListener implements ActionListener { //开门关门按钮
        CloseListener() {
        }
        public void actionPerformed(ActionEvent e) {
            if(lift.getEstate()==0) {
                lift.j = 0;
                jstate.setText("关门");
                jstate.setBackground(Color.yellow);
            }
        }
    }

    public class TimeListener implements ActionListener {
        TimeListener() {       }
        public void nextaction() { //检查向下运行的电梯是否还需向下，即检查在此层下还有没有数字键被按下，如果没有，则置电梯状态为停止，并使该电梯内所有数字键还原。
            //关门状态才改变
            if(lift.j==0){
                if (lift.getNumber() > lift.getDestination() && lift.getDestination() != -1) {//向下
                    lift.setEstate(2);
                }
                else if (lift.getNumber() < lift.getDestination()) {//向上
                    lift.setEstate(1);
                }
                else if(lift.getDestination()==-1) {//停留状态，先处理电梯内部向上后下，再处理外部向下，再处理外部向上
                    for (int i = 1; i <= 10; i++) {
                        if (table[10 - i] == 1) {
                            System.out.println(i+"楼被按下-table");
                            if (i < lift.getNumber()) {
                                setState(i);
                                lift.setEstate(2);
                            }
                            else if (i > lift.getNumber()) {
                                setState(i);
                                lift.setEstate(1);
                            }
                            else {
                                lift.setEstate(0);
                            }
                        }
                        else if (controlTable[10 - i][1] == 1) {
                            System.out.println(i+"楼被按下-↓");
                            if (i < lift.getNumber()) {
                                setState(i);
                                lift.setEstate(2);
                            } else if (i > lift.getNumber()) {
                                setState(i);
                                lift.setEstate(1);
                            } else {
                                lift.setEstate(0);
                            }
                        }
                        else if (controlTable[10 - i][0] == 1) {
                            System.out.println(i+"楼被按下-↑");
                            if (i < lift.getNumber()) {
                               setState(i);
                                lift.setEstate(2);
                            } else if (i > lift.getNumber()) {
                               setState(i);
                                lift.setEstate(1);
                            } else {
                                lift.setEstate(0);
                            }
                        }
                        else {
                            lift.setEstate(0);
                        }
                    }
                }
                else {
                    lift.setEstate(0);
                }
            }
        }

        public void actionPerformed(ActionEvent event) {//按钮恢复
            if (lift.j == 0) {
                int count=0;
                nextaction();//判断状态
                if (lift.getEstate() == 1 && lift.getNumber() != 10&& lift.getNumber()!=lift.getDestination()) {
                    lift.setNumber(lift.getNumber()+1);
                    sign[0].setText(lift.getNumber()+ "楼");
                    DrawBrick();
                    System.out.println("电梯楼层：" + lift.getNumber() + "\t目的楼层：" + lift.getDestination() + "\t电梯方向:" + lift.getEstate());
                }
                if (lift.getEstate() == 2 && lift.getNumber() != 1&&lift.getDestination()!=lift.getNumber()) {
                    lift.setNumber(lift.getNumber()-1);
                    sign[0].setText(lift.getNumber() + "楼");
                    DrawBrick();
                    System.out.println("电梯楼层：" + lift.getNumber() + "\t目的楼层：" + lift.getDestination() + "\t电梯方向:" + lift.getEstate());
                }

                if (lift.getNumber() == lift.getDestination()) { //电梯到达该楼层，上下同时同时消除。
                    lift.setDestination(-1);//电梯到达
                    System.out.println(-1);
                }
                if (table[10 - lift.getNumber()] == 1) { //内部按钮恢复
                    table[10 - lift.getNumber()] = 0;
                    DrawNumberBrick(10 - lift.getNumber(), 0);
                    count=1;
                }
                if (controlTable[10 - lift.getNumber()][0] == 1&&lift.getEstate()!=2) {
                    controlTable[10 - lift.getNumber()][0] = 0;
                    DrawControlBrick(10 - lift.getNumber(), 0, 0);
                    count=1;
                }
                if (controlTable[10 - lift.getNumber()][1] == 1&&lift.getEstate()!=1) {
                    controlTable[10 - lift.getNumber()][1] = 0;
                    DrawControlBrick(10 - lift.getNumber(), 1, 0);
                    count=1;
                }
                if(count==1) new openclosThread();


               //new Returnfirstfloor();//60秒没人按下任何键，则返回第一层

            }
        }
    }

    //*****************************************ui线程****************************************

    public class openclosThread extends Thread {
        public openclosThread() {
            start();
        }
        public void run() {
            lift.j=1;
            System.out.println("开门");
            jstate.setText("开门");
            jstate.setBackground(Color.red);
            try {
                sleep(3000);
            } catch (InterruptedException e) {
            }
            System.out.println("关门");
            jstate.setText("关门");
            jstate.setBackground(Color.yellow);
            lift.j=0;
        }
    }

    public class Returnfirstfloor extends Thread { //如果在60秒内没有人按下任何键
        int count;
        public Returnfirstfloor() {
            count=0;
            start();
        }
        public void run() {
            if(lift.getEstate()==0&&lift.getNumber()!=1){
                try {
                    sleep(60000);
                }catch (InterruptedException e) {
                }
                for (int i = 1; i<=10; i++){
                    if (table[10 - i] == 1||controlTable[10-i][1]==1||controlTable[10-i][0]==1){
                        count++;
                    }
                }
                if(count==0){
                    lift.setEstate(2);
                    lift.setDestination(1);
                }
            }
        }
    }

    public class Setime extends Thread{//定时监听线程
        Setime(){
            start();
        }
        public void run() {
            ActionListener timelistener = new contrl.TimeListener();
            timer = new Timer((500), timelistener);
            timer.start();
        }
    }

}