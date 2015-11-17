package com.floodmonitoring;

import java.util.LinkedList;
import java.util.Queue;

public class Node {

	private double batteryLevel;
	private double locationLat;
	private double locationLong;
	private double sensedData;
	private Queue<Packet> data;
	
	Node(){
		data = new LinkedList<Packet>();
		batteryLevel =  100;
//		locationLat , locationLong = fetchLocation();
	}
	
	public void Execute(){
		
	}
	
	// Routing Data & Neighbours
}
