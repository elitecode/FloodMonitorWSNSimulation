package com.floodmonitoring;

import java.lang.Math;
import java.util.Random;

public class Utility{
	private static long packetCount = 0;
    public static Random generate = new Random(Constants.RANDOMSEED);    
    public static boolean checkNodeFault()
    {
        double random = generate.nextDouble();
        if(random < Constants.NODEFAULTERSEED)
            return true;
        return false;
    }
    public static double decreaseBattery(String Mode,double Battery){
        if(Mode.equals("SEND"))
            Battery = Battery - Constants.SENDBATTERYDECREASE;
        else if(Mode.equals("RECEIVE"))
            Battery = Battery - Constants.RECEIVEBATTERYDECREASE;
        else{
            Battery = Battery - Constants.REGULARBATTERYDECREASE;
        }
        if(Battery>=0)
            return Battery;
        return 0;
    }
    
    public static long getPacketId(){
    	packetCount++;
    	return packetCount;
    }

}