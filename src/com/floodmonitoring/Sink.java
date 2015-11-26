package com.floodmonitoring;

public class Sink extends Node {

	Sink(int x, int y) {
		super(x, y);
		id = "Sink";
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

}
