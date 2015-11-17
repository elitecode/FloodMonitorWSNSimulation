package com.floodmonitoring;

public class Packet {

	Node source;
	Node destination;
	long timestamp;
	long travelTime;
	
	Packet(long time) {
		timestamp = time;
	}
	
	public void updateTravelTime(long newTimestamp) {
		travelTime = newTimestamp - timestamp;
	}
}
