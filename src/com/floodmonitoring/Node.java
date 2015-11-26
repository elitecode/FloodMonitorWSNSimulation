package com.floodmonitoring;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	private double batteryLevel;
	private int x;
	private int y;
	private String id;
	private double sensedData;
	private boolean status;
	private Queue<Packet> packetQueue;
	private List<Node> neighbours;
	private MemoryQueue memory;
	private boolean carrierLock;
	private long nextDataGenerationTime;
	private long timePeriod;
	private boolean gatherData;
	
	Node(int x, int y){
		this.x = x;
		this.y = y;
		id = "("+x+","+y+")";
		packetQueue = new LinkedList<Packet>();
		batteryLevel =  100;
		status = true;
		gatherData = false;
		memory = new MemoryQueue(Constants.MEMORY_SIZE);
//		locationLat , locationLong = fetchLocation();
	}
	
	public void setNeighbours(List<Node> neighbours) {
		this.neighbours = neighbours;
	}
	
	public boolean Execute(long time){
		
		if(status) {
			batteryLevel = Utility.decreaseBattery("REGULAR", batteryLevel);
			
			// Data generation check
			
			if(!packetQueue.isEmpty()) {
				if(getCarrierLock()) {
					sendPacket(packetQueue.remove(), time);
				}
			}
			if(batteryLevel<=0 || Utility.checkNodeFault()) {
				nodeFailure(time);
			}
			return true;
		}
		else
			return false;
	}
	
	public void sendPacket(Packet packet, long time) {
		packet.updateTravelTime(time);
		
		switch(packet.getType()) {
		case RREP:
			sendBroadcast(packet, time);
			break;
		case KHOPREQUEST:
			break;
		case KHOPRESPONSE:
			break;
		case RREQ:
			sendBroadcast(packet, time);
			break;
		case STANDARD:
			break;
		default:
			log("Invalid Packet", time);
			break;
		}
		
		memory.addPacket(packet);
		batteryLevel = Utility.decreaseBattery("SEND", batteryLevel);
		
	}
	
	public void sendBroadcast(Packet packet, long time) {
		boolean lock = true;
		for(Node node : neighbours) {
			lock = lock && node.canGetCarrierLock();
		}
		if(lock) {
			for(Node node : neighbours) {
				node.getCarrierLock();
				log("Packet sent to "+node.id, time);
				node.receivePacket(packet, time);
			}
		}
		else {
			packetQueue.add(packet);
			resetCarrierLock();
		}
	}
	
	public void sendDirected(Packet packet, Node destination, long time) {
		boolean lock = true;
		if(lock) {
			for(Node node : neighbours) {
				node.getCarrierLock();
				log("Packet sent to "+node.id, time);
				node.receivePacket(packet, time);
			}
		}
		else {
			packetQueue.add(packet);
			resetCarrierLock();
		}
	}
	
	public void receivePacket(Packet packet, long time) {
		
		// Check if packet is not in memory
		
		// IF shortest path packet, set next node in path to sink
		// If k-hop request packet, send to neighbour and send response back
		// if k-hop response packet, forward it using node list in packet
		// If node failure packet, delete that node from neighbours
		// If normal packet, add to queue
		if(!memory.inMemory(packet)) {
			packetQueue.add(packet);
			log("Received packet", time);
		}
		else {
			log("Received packet already in memory", time);
		}
		batteryLevel = Utility.decreaseBattery("RECEIVE", batteryLevel);
	}
	
	public void nodeFailure(long time) {
		log("Node Failure " + x + " " + y + " Battery: " + batteryLevel, time);
		status = false;
	}
	
	public void onFloodDetect(long time) {
		gatherData = true;
		timePeriod = Constants.DATA_GATHER_RATE;
		nextDataGenerationTime = time + Utility.generate.nextLong()%timePeriod;
		log("Flood Detected", time);
		
		Packet packet = new Packet(time, this, PacketType.RREP);
		packetQueue.add(packet);
	}
	
	public boolean canGetCarrierLock() {
		return !carrierLock;
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
	
	public void log(Object o, long time) {
		System.out.println("Time: "+time+" | Node "+id+": " + o);
	}
	// Routing Data & Neighbours
}
