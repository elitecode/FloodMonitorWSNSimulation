package com.floodmonitoring;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	protected double batteryLevel;
	protected int x;
	protected int y;
	protected String id;
	protected double sensedData;
	protected boolean status;
	protected Queue<Packet> packetQueue;
	protected List<Node> neighbours;
	protected MemoryQueue memory;
	protected boolean carrierLock;
	protected long nextDataGenerationTime;
	protected long timePeriod;
	protected boolean gatherData;
	protected Node nextNodeToSink;
	protected boolean pathToSinkAvailable;
	
	public String getNextNodeTosink(){
		if(nextNodeToSink == null)
			return "Not Assigned";
		return nextNodeToSink.getId();		
	}
	
	public String getId(){
		return id;
	}
	Node(int x, int y){
		this.x = x;
		this.y = y;
		id = "("+x+","+y+")";
		packetQueue = new LinkedList<Packet>();
		batteryLevel =  100;
		status = true;
		gatherData = false;
		memory = new MemoryQueue(Constants.MEMORY_SIZE);
		pathToSinkAvailable = false;
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
				if(canGetCarrierLock()) {
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
		
		boolean success = true;
		getCarrierLock();
		switch(packet.getType()) {
		case RREP:
			success = sendBroadcast(packet, time);
			break;
		case KHOPREQUEST:
			success = sendBroadcast(packet, time);
			break;
		case KHOPRESPONSE:
			success = sendDirected(packet, packet.getNextInPath(), time);
			break;
		case RREQ:
			success = sendBroadcast(packet, time);
			break;
		case STANDARD:
			if(pathToSinkAvailable) {
				if(nextNodeToSink.getStatus()) {
					success = sendDirected(packet, nextNodeToSink, time);
					break;
				}
				else {
					// TODO: generate RREQ
					Packet rreqPacket = new Packet(time, this, PacketType.RREQ);
					packetQueue.add(rreqPacket);
				}
			}
			packetQueue.add(packet);
			success = false;
			break;
		default:
			log("Invalid Packet", time);
			break;
		}
		
		if(success) {
			batteryLevel = Utility.decreaseBattery("SEND", batteryLevel);
		}
		else {
			resetCarrierLock();
		}
	}
	
	public boolean sendBroadcast(Packet packet, long time) {
		boolean lock = true;
		
		// TODO: check for failure nodes
		for(Node node : neighbours) {
			lock = lock && node.canGetCarrierLock();
		}
		if(lock) {
			for(Node node : neighbours) {
				node.getCarrierLock();
				log("Packet sent to "+node.id, time);
				node.receivePacket(new Packet(packet), time);
			}
			return true;
		}
		else {
			packetQueue.add(packet);
			return false;
		}
	}
	
	public boolean sendDirected(Packet packet, Node destination, long time) {
		
		// TODO: Correct the code
		boolean lock = true;
		if(lock) {
			for(Node node : neighbours) {
				node.getCarrierLock();
				log("Packet sent to "+node.id, time);
				node.receivePacket(packet, time);
			}
			return true;
		}
		else {
			packetQueue.add(packet);
			resetCarrierLock();
			return false;
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
			log("Received packet", time);
			memory.addPacket(packet);
			
			switch(packet.getType()) {
			case RREP:
				pathToSinkAvailable = true;
				nextNodeToSink = packet.getNextInPath();
				packet.addToPath(this);
				packet.incrementHops();
				packetQueue.add(packet);
				break;
			case KHOPREQUEST:
				packetQueue.add(packet);
				break;
			case KHOPRESPONSE:
				packetQueue.add(packet);
				break;
			case RREQ:
				packet.incrementHops();
				packetQueue.add(packet);
				break;
			case STANDARD:
				packetQueue.add(packet);
				break;
			default:
				log("Invalid Packet", time);
				break;
			}
			
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
	
	public boolean getStatus() {
		return status;
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
