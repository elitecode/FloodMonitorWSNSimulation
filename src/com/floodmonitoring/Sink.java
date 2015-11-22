package com.floodmonitoring;

public class Sink extends Node {

	Sink(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void receivePacket(Packet packet){
		
	}
	@Override
	public boolean Execute(long time){
		return true;
	}
	@Override
	public void sendPacket(Packet packet){
		
	}

}
