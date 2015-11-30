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
	private long packetId;
	Stack<Node> path;
	PacketType type;
	Packet(long time,Node mSource,PacketType mtype){
		source = mSource;
		timestamp = time;
		packetId = Utility.getPacketId();
        previous = mSource;
		type = mtype;
		path = new Stack<Node>();
	}
	Packet(Packet packet){
		this.source=packet.getSource();
		this.previous=packet.getPrevious();
		this.destination=packet.getDestination();
		this.type=packet.getType();
		this.timestamp=packet.getTimestamp();
		this.packetId=packet.getPacketId();
		this.numberHops=packet.getNumberHops();
		this.travelTime=packet.getTravelTime();
		this.path= (Stack<Node>) packet.getPath().clone();
	}

	public boolean equals(Object other) {
		if((other == null) || (getClass() != other.getClass())){
	        return false;
	    }
		Packet packet = (Packet) other;
		if(this.packetId == packet.packetId)
			return true;
		return false;
	}
	
	private Stack<Node> getPath() {
		return path;
	}

	public void updateTravelTime(long newTimestamp) {
		travelTime = newTimestamp - timestamp;
	}
	public void addToPath(Node node){
		path.push(node);
	}
	public Node getNextInPath(){
		if(!path.isEmpty())
			return path.pop();
		return null;
	}
	public void removeFromPath(){
		if(!path.isEmpty())
			path.pop();
	}
	public boolean isPathEmpty(){
		return path.isEmpty();
	}

	public void setDestination(Node mDestination){
		destination = mDestination;
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
	public Node getSource() {
		return source;
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
	public Packet getClone(){
		Packet clone = new Packet(timestamp,source,type);
		clone.setDestination(destination);
		return null;
	}
	public void setSource(Node mSource){
		source = mSource;
	}
	public void setPrevious(Node mPrevious){
		previous = mPrevious;
	}
	public void setTimestamp(long mTimestamp){
		timestamp = mTimestamp;
	}
	public void setTravelTime(long mTravelTime){
		travelTime = mTravelTime;
	}
	public void setNumberHops(int mNumberhops){
		numberHops = mNumberhops;
	}
	public void setPacketId(int mPacketId){
		packetId = mPacketId;
	}
	public void setPath(Stack<Node> mPath){
		path = mPath;
	}
}
