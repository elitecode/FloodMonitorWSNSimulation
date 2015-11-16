package com.floodmonitoring;

import java.lang.Math;
import java.util.Random;

public class NodeUtitlity{
    public static Random generate = New Random(Constants.randomSeed);
    public static boolean checkNodeFault()
    {
        double random = generate.nextDouble();
        if(random < Constants.randomseed)
            return true;
        return false;
    }
    public static double decreaseBattery(String Mode,double Battery){
        if(Mode.equals("SEND"))
            Battery = Battery - Constants.SendBatteryDecrease;
        else if(Mode.equals("RECIEVE"))
            Battery = Battery - Constants.RecieveBatteryDecrease;
        else{
            Battery = Battery - Constants.RegularBatteryDecrease;
        }
        if(Battery>=0)
            return Battery;
        return 0;
    }
}