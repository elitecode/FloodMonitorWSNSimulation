package com.floodmonitoring;

import java.util.LinkedList;
import java.util.Queue;

public class Node {

	private double batteryLevel;
	private double x;
	private double y;
	private double sensedData;
	private boolean status;
	private Queue<Packet> data;
	
	Node(int lat, int lon){
		x = lat;
		y = lon;
		data = new LinkedList<Packet>();
		batteryLevel =  100;
		status = true;
//		locationLat , locationLong = fetchLocation();
	}
	
	public void Execute(long time){
		batteryLevel = NodeUtility.decreaseBattery("REGULAR", batteryLevel);
		if(!data.isEmpty()) {
			Packet packet = data.remove();
			packet.updateTravelTime(time);
			sendPacket(packet);
		}
		if(batteryLevel<=0 || NodeUtility.checkNodeFault()) {
			nodeFailure();
		}
	}
	
	public void sendPacket(Packet packet) {
		batteryLevel = NodeUtility.decreaseBattery("SEND", batteryLevel);
	}
	
	public void receivePacket(Packet packet) {
		data.add(packet);
		batteryLevel = NodeUtility.decreaseBattery("RECEIVE", batteryLevel);
	}
	
	public void nodeFailure() {
		status = false;
	}
	
	// Routing Data & Neighbours
}
