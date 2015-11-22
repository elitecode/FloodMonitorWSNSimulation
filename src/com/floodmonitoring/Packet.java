package com.floodmonitoring;

import java.util.List;
import java.util.Stack;

public class Packet {

	private Node source;
	private Node destination;
    private Node previous;
	private long timestamp;
	private long travelTime;
	private long numberHops;
	public  long packetId;
	Stack<Node> path;
	PacketType type;
	Packet(long time,Node mSource,PacketType mtype){
		source = mSource;
		timestamp = time;
		packetId = Utility.getPacketId();
        previous = mSource;
		type = mtype;
	}
	public void updateTravelTime(long newTimestamp) {
		travelTime = newTimestamp - timestamp;
	}
	public void addToPath(Node node){
		if(!path.isEmpty())
		path.push(node);
	}
	public Node getNextInPath(){
		if(!path.isEmpty())
			return path.peek();
		return null;
	}
	public void removeFromPath(){
		if(!path.isEmpty())
			path.pop();
	}
	public boolean isPathEmpty(){
		return path.isEmpty();
	}

	public void setDestination(Node mdestination){
		destination = mdestination;
	}
	public void incrementHops(){
		numberHops++;
	}
	public long getNumberHops(){
		return numberHops;
	}
	public void updatePrevious(Node mPrevious){
		previous = mPrevious;
	}
	public Node getPrevious(){
		return previous;
	}
	public Node getDestination(){
		return destination;
	}
	public long getTimestamp(){
		return timestamp;
	}
	public long getTravelTime(){
		return travelTime;
	}
	public long getPacketId(){
		return packetId;
	}
	public PacketType getType(){
		return type;
	}
}
