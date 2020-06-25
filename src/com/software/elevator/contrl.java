package com.software.elevator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class contrl {
    public int octime=0; //计算开关门事件
    public int j=0;
    public String closedoor="关门";
    public String close="关";
    public String open="开";
    JFrame frame=new JFrame("电梯调度算法");//窗体
    JMenuBar menu=new JMenuBar();//菜单栏
    JPanel pan=new JPanel();//面板
    JButton but=null;//按钮
    JButton[] sign=new JButton[4];
    JButton state=new JButton(closedoor+"");
    JButton openkey=new JButton(open+"");
    JButton closekey=new JButton(close+"");
    public ArrayList[] fisrt; //二维数组存放多部电梯
    public ArrayList controlBrick; //controlBrick用来依次存放button,表示每层楼电梯口的是上键和下键，程序用button显示绿色表示对应键被按下。
    public int[][] controlTable; //用二维数组controlTable与电梯口的上下键对应，其中如果值1表示button应显示绿色，值0表示button应显示灰色。
    public ArrayList numberBrick; //numberBrick用来依次存放button,程序用button显示桔黄色表示电梯内的数字键被按下。
    public int[]table; //table电梯内部数字，1表示按下，0表示未按下。
    public int[] upSignalTable; //记录向上的楼层，1表示按下，0表示未按下。
    public int[] downSignalTable; //记录向下的楼层，1表示按下，0表示未按下。
    public Timer timer; //计时器。
    public LiftThread lift; //电梯数组



//================================================构造方法(下)初始化===========================================================//

    public contrl(){
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
        state.setBounds(320, 0, 80, 40);
        state.setBackground(Color.yellow);

        openkey.setBounds(400, 0, 80, 40);
        openkey.addActionListener(new OpencolseListener()); //开门按钮添加事件

        closekey.setBounds(480, 0, 80, 40);
        closekey.addActionListener(new OpencolseListener()); //关门按钮添加事件

        pan.setLayout(null); //画布绝对定位
        Font fnt=new Font("Serief",Font.ITALIC,15); //设置字体样式

//实例化数组,初始化数组
        numberBrick=new ArrayList(10);
        fisrt =new ArrayList[4];//电梯位置按钮
        for(int i=0;i<4;i++){
            fisrt[i]=new ArrayList(10);
        }
        controlBrick = new ArrayList(10);


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
            but.addActionListener(new UpListener(i));
            but.setBackground(Color.gray);
            controlBrick.add(but);
            pan.add(but);

//向下键

            but = new JButton("下");
            but.setFont(fnt);
            but.setBounds(480, (11 - i) * 40, 80, 40);
            but.addActionListener(new DownListener(i));
            but.setBackground(Color.gray);
            controlBrick.add(but);
            pan.add(but);
        }

//*******************运行部分******************

        pan.add(sign[0]);
        pan.add(sign[1]);
        pan.add(sign[2]);
        pan.add(sign[3]);
        pan.add(state);
        pan.add(openkey);
        pan.add(closekey);
        frame.add(pan);
        frame.setJMenuBar(menu);
        frame.setSize(579, 500);
        lift = new LiftThread();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

    }
//====================================================构造方法(上)=======================================================//

    //*************************************************填充方块和设置方法*******************************************************
    public void DrawBrick() { //电梯运行
        for (int i = 0; i <10; i++){
            for(int j=0;j<10;j++)//全刷黑
                ((JButton) fisrt[0].get(j)).setBackground(Color.black);
            ((JButton) fisrt[0].get(10-lift.number)).setBackground(Color.blue);//刷蓝

        }
    }

    public void DrawControlBrick() { //外部按钮
        for (int i = 0; i < 10; i++)
            for(int j=0;j<2;j++)
                if (controlTable[i][j] == 1){
                    ((JButton) controlBrick.get(i * 2 + j)).setBackground(Color.red);
                }else{
                    ((JButton) controlBrick.get(i * 2 + j )).setBackground(Color.gray);
                }
    }

    public void DrawNumberBrick() { //数字按钮
        for (int i = 0; i < 10; i++)
            if (table[i] == 1)
                ((JButton) numberBrick.get(i )).setBackground(Color.orange);
            else
                ((JButton) numberBrick.get(i )).setBackground(Color.gray);
    }

    public boolean WorkState() { //判断当前电梯内的数字键是否被按下，如有键被按下，则值为1，如果没有任何键被按下，则值为0。
        for (int i = 0; i < 10; i++) {
            if (table[i] == 1)
                return true;
        }
        return false;
    }

    public void setState(int number){ //如果电梯原来停，则改变电梯状态
        lift.destination = number;
        int x = lift.number;
        if (lift.state == 0) {
            if (number > x)
                lift.state = 1;
            if (number < x)
                lift.state = 2;
        }
    }

//*****************************************事件处理部分****************************************

    public class UpListener implements ActionListener{ //相应上键被按下的动作。
        int L = 1; //当前楼层。
        public UpListener(int n) {
            L = n;
        }
        public void actionPerformed(ActionEvent e){ //重新显示上下键的颜色并将向上任务加入向上任务数组。
            controlTable[10 - L][0] = 1;
            setState(L);
            DrawControlBrick();
        }
    }

    public class DownListener implements ActionListener{ //相应下键被按下的动作。
        int L=0;
        public DownListener(int n){
            L=n;
        }
        public void actionPerformed(ActionEvent e) {
            controlTable[10-L][1]=1;
            setState(L);
            DrawControlBrick() ;
        }
    }

    public class NumberListener implements ActionListener { //相应数字键被按下的动作。
        int number = 0; //相应的数字键楼层。
        public NumberListener(int n) {
            number = n;
        }
        public void actionPerformed(ActionEvent e) {
            table[10 - number] = 1;
            setState(number);
            DrawNumberBrick();
        }
    }

    public class OpencolseListener implements ActionListener { //开门关门按钮
        //int number = 0;
        OpencolseListener() {
//number = n;
        }
        public void actionPerformed(ActionEvent e) {
            octime=500;
            System.out.println("事件"+octime);
        }
    }

//***************************************主要监听器********************************************

    public class TimeListener implements ActionListener { //电梯线程的监听器。************************************
        TimeListener() {}

        public void actionDown(){ //检查向下运行的电梯是否还需向下，即检查在此层下还有没有数字键被按下，如果没有，则置电梯状态为停止，并使该电梯内所有数字键还原。
            if (lift.state == 2) {
                int count = 0;
                for (int i = lift.number; i > 0; i--)
                    if (table[10 - i] == 1||controlTable[10-i][1]==1||controlTable[10-i][0]==1)
                        count++;
                if (count == 0&& lift.number <= lift.destination) {
                    count=0;
                    for(int j=lift.number;j<10;j++){
                        if(table[10-j]==1||controlTable[10-j][1]==1||controlTable[10-j][0]==1){
                            count++;
                        }
                    }
                    if(count>0){
                        lift.state=1;
//System.out.println("改变方向：向上");
                    }else{
                        lift.state = 0;
                    }
                }
            }
        }


        public void actionUp(){ //检查向上运行的电梯是否还需向上，即检查在此层上还有没有数字键被按下，如果没有，则置电梯状态为停止，并使该电梯内所有数字键还原。
            if (lift.state == 1) {
                int count = 0;
                for (int i = lift.number; i < 10; i++)
                    if (table[10 - i] == 1||controlTable[10-i][0]==1||controlTable[10-i][1]==1)
                        count++;
                if (count == 0&& lift.number >=lift.destination) {
                    count=0;
                    for(int j=lift.number;j>0;j--){
                        if(table[10-j]==1||controlTable[10-j][1]==1||controlTable[10-j][0]==1){
                            count++;
                        }
                    }
                    if(count>0){
                        lift.state=2;
//System.out.println("改变方向：向下");
                    }else{
                        lift.state = 0;
                    }
                }
            }

        }

        public void actionPerformed(ActionEvent event) {
            int state = lift.state; //记录电梯当前状态。

            if (lift.state!=0&&table[10 - lift.number] == 1) { //如果电梯经过电梯内数字键显示的要到达的楼层，则该数字键状态恢复未按，并重新显示数字键颜色。
                table[10 - lift.number] = 0;
                DrawNumberBrick();
                new openclosThread();
            }

            if (state != 0&& lift.number == lift.destination) { //如果电梯属于逆向到达，则此任务完成，从任务数组中删除并重新显示上下键的颜色。
                if (state == 2&& controlTable[10 - lift.number][state - 1]== 0) {
                    controlTable[10 - lift.number][2 - state] = 0;
                    DrawControlBrick();
                    new openclosThread();
                }
                if (state == 1&& controlTable[10 - lift.number][state - 1]== 0) {
                    controlTable[10 - lift.number][2 - state] = 0;
                    new openclosThread();
                    DrawControlBrick();
                }
            }

            if (state == 1) { //向上电梯经过的向上任务完成，从任务数组中删除并重新显示上下键的颜色。
                if (controlTable[10 - lift.number][0] == 1) {
                    controlTable[10 - lift.number][0] = 0;
                    new openclosThread();
                    DrawControlBrick();
                }
            }

            if (state == 2) { //向下电梯经过的向下任务完成，从任务数组中删除并重新显示上下键的颜色。
                if (controlTable[10 - lift.number][1] == 1) {
                    controlTable[10 - lift.number][1] = 0;
                    new openclosThread();
                    DrawControlBrick();
                }
            }

            actionUp(); //判断是否继续向上

            actionDown(); //判断是否继续向下

            state = lift.state; //如果电梯状态不为停止，则按照运行方向运行。
            if (state == 1){
                lift.number++;
//System.out.println("向上"+lift.number);
            }

            if (state == 2){
                lift.number--;
//System.out.println("向下"+lift.number);
            }
//设置相应电梯的显示器
            sign[0].setText(lift.number+"楼");
            DrawBrick();

            //  new Returnfirstfloor(state);//10秒没人按下任何键，则返回第一层

        }
    }
//***************************************************电梯线程时间控制。****************************************************************************************

    public class openclosThread extends Thread {
        public openclosThread() {
            start();
        }
        public void run() {
            state.setText("开门");

            try {
                sleep(1000);
            } catch (InterruptedException e) {
            }
            state.setText("关门");
        }
    }

//    public class Returnfirstfloor extends Thread { //如果在30秒内没有人按下任何键，电梯则自动返回第一次；省电(*^__^*) 嘻嘻……
//        int stater;
//        int count;
//        public Returnfirstfloor(int state) {
//            count=0;
//            this.stater=state;
//            start();
//        }
//        public void run() {
//            if(stater==0&&lift.number!=1){
//                try {
//                    sleep(10000);
//                }catch (InterruptedException e) {
//                }
//                for (int i = 1; i<=10; i++){
//                    if (table[10 - i] == 1||controlTable[10-i][1]==1||controlTable[10-i][0]==1){
//                        count++;
//                    }
//                }
//                if(count==0){//如果10秒后没人按，就设第一层为1,电梯方向为向下。
//                    table[9] = 1;
//                    lift.state=2;
//                }
//            }
//        }
//    }

    public class LiftThread extends Thread {
        public int number = 1; //电梯当前楼层。
        public int state = 0; //电梯当前状态，0为停止，1为上升，2为下降。
        int destination; //电梯的目的楼层。
        int i=0;

        public LiftThread() {
            start();
            i=octime;
            System.out.println(""+i);
        }

        public void run() {
            ActionListener timelistener = new contrl.TimeListener();
            timer = new Timer((1500+i), timelistener);
            timer.start();
        }
    }
}
