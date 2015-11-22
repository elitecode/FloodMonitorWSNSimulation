package com.floodmonitoring;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	private double batteryLevel;
	private int x;
	private int y;
	private double sensedData;
	private boolean status;
	private Queue<Packet> data;
	private List<Node> neighbours;
	private List<Integer> packetMemory;
	private boolean carrierLock;
	private long nextDataGenerationTime;
	private long timePeriod;
	private boolean gatherData;
	
	Node(int x, int y){
		this.x = x;
		this.y = y;
		data = new LinkedList<Packet>();
		batteryLevel =  100;
		status = true;
		gatherData = false;
//		locationLat , locationLong = fetchLocation();
	}
	
	public void setNeighbours(List<Node> neighbours) {
		this.neighbours = neighbours;
	}
	
	public boolean Execute(long time){
		
		if(status) {
			batteryLevel = NodeUtility.decreaseBattery("REGULAR", batteryLevel);
			
			// Data generation check
			
			if(!data.isEmpty()) {
				Packet packet = data.remove();
				packet.updateTravelTime(time);
				sendPacket(packet);
				
				// set packet to memory using id
			}
			if(batteryLevel<=0 || NodeUtility.checkNodeFault()) {
				nodeFailure();
			}
			return true;
		}
		else
			return false;
	}
	
	public void sendPacket(Packet packet) {
		batteryLevel = NodeUtility.decreaseBattery("SEND", batteryLevel);
		
	}
	
	public void receivePacket(Packet packet) {
		
		// Check if packet is not in memory
		
		// IF shortest path packet, set next node in path to sink
		// If k-hop request packet, send to neighbour and send response back
		// if k-hop response packet, forward it using node list in packet
		// If node failure packet, delete that node from neighbours
		// If normal packet, add to queue
		
		data.add(packet);
		batteryLevel = NodeUtility.decreaseBattery("RECEIVE", batteryLevel);
	}
	
	public void nodeFailure() {
		log("Node Failure " + x + " " + y + " Battery: " + batteryLevel);
		status = false;
	}
	
	public void onFloodDetect(long time) {
		gatherData = true;
		timePeriod = Constants.DATA_GATHER_RATE;
		nextDataGenerationTime = time + NodeUtility.generate.nextLong()%timePeriod;
		log("Flood Detected");
	}
	
	public boolean getCarrierLock() {
		if(carrierLock)
			return false;
		carrierLock = true;
		return carrierLock;
	}
	
	public void resetCarrierLock() {
		carrierLock = false;
	}
	
	public void log(Object o) {
		System.out.println(o);
	}
	// Routing Data & Neighbours
}
