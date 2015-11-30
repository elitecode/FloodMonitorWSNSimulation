package com.floodmonitoring;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

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
	public int packetDrop;
	protected Simulator simulator;
	protected long lastKHopRequest;
	
	public String getNextNodeTosink(){
		if(nextNodeToSink == null)
			return "Not Assigned";
		return nextNodeToSink.getId();		
	}
	
	public String getId(){
		return id;
	}
	Node(int x, int y, Simulator simulator){
		this.simulator = simulator;
		this.x = x;
		this.y = y;
		id = "("+x+","+y+")";
		packetQueue = new LinkedList<Packet>();
		batteryLevel =  100;
		status = true;
		gatherData = false;
		memory = new MemoryQueue(Constants.MEMORY_SIZE);
		pathToSinkAvailable = false;
		packetDrop = 0;
		lastKHopRequest = 0;
	}
	
	public void setNeighbours(List<Node> neighbours) {
		this.neighbours = neighbours;
	}
	
	public boolean Execute(long time){
		
		if(status) {
			batteryLevel = Utility.decreaseBattery("REGULAR", batteryLevel);
			
			// Data generation check
			if(gatherData) {
				if(time == nextDataGenerationTime) {
					generateData(time);
				}
			}
			
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
			Node destination = packet.getNextInPath();
			success = sendDirected(packet, destination, time);
			if(!success) {
				packet.addToPath(destination);
			}
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
					Packet rreqPacket = new Packet(time, this, PacketType.RREQ);
					packetQueue.add(rreqPacket);
					pathToSinkAvailable = false;
					log("Generated RREQ",time);
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
				log("Packet #"+packet.getPacketId()+" sent to "+node.id, time);
				packet.incrementHops();
				packet.updateTravelTime(time);
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

		if(destination.canGetCarrierLock()) {
			destination.getCarrierLock();
			log("Packet #"+packet.getPacketId()+" directed to "+destination.id, time);
			packet.incrementHops();
			packet.updateTravelTime(time);
			destination.receivePacket(packet, time);
			return true;
		}
		else {
			packetQueue.add(packet);
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
				packetQueue.add(packet);
				break;
			case KHOPREQUEST:
				generateKHopResponse(packet, time);
				if(packet.getNumberHops() < Constants.KHOP_DISTANCE) {
					packet.addToPath(this);
					packetQueue.add(packet);
				}
				break;
			case KHOPRESPONSE:
				if(packet.getDestination().getId().equals(this.getId())) {
					log("KHop Response packet #"+packet.getPacketId()+" received",time);
					simulator.onKHopResponse(time - lastKHopRequest);
				}
				else {
					packetQueue.add(packet);
				}
				break;
			case RREQ:
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
	
	public void generateData(long time) {
		sensedData = Utility.generate.nextDouble();
		nextDataGenerationTime = time + timePeriod + (Utility.generate.nextInt()%10)-5;
		Packet packet = new Packet(time, this, PacketType.STANDARD);
		packetQueue.add(packet);
		log("Data packet #"+packet.getPacketId()+" generated",time);
	}
	
	public void nodeFailure(long time) {
		log("Node Failure " + x + " " + y + " Battery: " + batteryLevel, time);
		status = false;
	}
	
	public void onFloodDetect(long time) {
		gatherData = true;
		timePeriod = Constants.DATA_GATHER_RATE;
		nextDataGenerationTime = time;
		log("Flood Detected", time);
	}
	
	public void onKHopRequest(long time) {
		lastKHopRequest = time;
		Packet packet = new Packet(time, this, PacketType.KHOPREQUEST);
		packet.addToPath(this);
		packetQueue.add(packet);
		batteryLevel = Utility.decreaseBattery("RECEIVE", batteryLevel);
		log("KHop Request packet #"+packet.getPacketId()+" generated",time);
	}
	
	public void generateKHopResponse(Packet reqPacket, long time) {
		Packet packet = new Packet(time, this, PacketType.KHOPRESPONSE);
		packet.setPath((Stack<Node>)reqPacket.path.clone());
		packet.setDestination(reqPacket.getSource());
		packetQueue.add(packet);
		log("KHop Response packet #"+packet.getPacketId()+" generated",time);
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
