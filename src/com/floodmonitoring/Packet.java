package com.floodmonitoring;

import java.util.List;
import java.util.Stack;

public class Packet {

	Node source;
	Node destination;
    Node previous;
	long timestamp;
	long travelTime;
	long numberHops;
	public static long packetId;
	Stack<Node> path;
	public enum type{
		KHOPRESPONSE,KHOPREQUEST,BROADCAST,PATHREQUEST,STANDARD;
	}
	Packet(long time,Node mSource,Node mDestination, String mtype){
		source = mSource
		timestamp = time;
		packetId++;
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
	}
	public void removeFromPath(){
		if(!path.isEmpty())
		path.pop();
	}
	public boolean isPathEmpty(){
		return path.isEmpty();
	}
}
