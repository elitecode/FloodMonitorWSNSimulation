package com.floodmonitoring;

public class Packet {

	Node source;
	Node destination;
	int timestamp;
	int travelTime;
	
	Packet(int time) {
		timestamp = time;
	}
	
	public void updateTravelTime(int newTimestamp) {
		travelTime = newTimestamp - timestamp;
	}
}
