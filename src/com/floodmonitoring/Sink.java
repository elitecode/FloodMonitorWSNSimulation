package com.floodmonitoring;

public class Sink extends Node {

	private long dataPackets;
	private long totalDelay;
	Sink(int x, int y) {
		super(x, y, null);
		id = "Sink";
		dataPackets = 0;
		totalDelay = 0;
		
	}
	
	@Override
	public void receivePacket(Packet packet, long time){
		if(!memory.inMemory(packet)) {
			packetQueue.add(packet);
			log("Received packet", time);
		}
		else {
			log("Received packet already in memory", time);
		}
		batteryLevel = Utility.decreaseBattery("RECEIVE", batteryLevel);

		PacketType type = packet.getType();
		
		switch(type){
			case RREQ:
				Packet responsePacket = new Packet(time, this, PacketType.RREP);
				responsePacket.addToPath(this);
				packetQueue.add(responsePacket);
				memory.addPacket(responsePacket);
				break;
			case STANDARD:				
				//TODO: LOG STUFF and DELAYS
				dataPackets++;
				totalDelay = totalDelay + packet.getTravelTime();
				break;
			default: //log packet
		}
		
	}
	@Override
	public void sendPacket(Packet packet, long time){
		packet.updateTravelTime(time);
		boolean success = true;
		getCarrierLock();

		
		switch(packet.getType()) {
			case RREP:
				success = sendBroadcast(packet, time);
				break;
			default:
				// log packet
		}
		if(success) 
			batteryLevel = Utility.decreaseBattery("SEND", batteryLevel);
		else 
			resetCarrierLock();
				
	}

	public long getTotalDataPacketsReceived(){
		return dataPackets;
	}
	public double getAverageDelay(){
		return (double)totalDelay/(double)dataPackets;
	}
}
