package com.finish.ALS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class elevator {
    public static int j=0;//0是关门，1是开门
    private int number = 1; //电梯当前楼层。
    private int estate = 0; //电梯当前状态，0为停止，1为上升，2为下降。
    private int destination=1; //电梯的目的楼层。

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public int getEstate() {
        return estate;
    }
    public void setEstate(int estate) {
        this.estate = estate;
    }
    public int getDestination() {
        return destination;
    }
    public void setDestination(int destination) {
        this.destination = destination;
    }

    public elevator() {

    }


}
