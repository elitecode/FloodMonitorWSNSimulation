package com.floodmonitoring;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
	private List<Node> nodeList;
	private int gridDim;
	private Sink sink;
	private long totalDataPackets;
	private long totalKHopRequests;
	private long totalKHopDelay;
	private double averageDelay;
	
	public void simulate(long T){
		
		long currentTime = 1;
		Packet p = new Packet(currentTime, sink, PacketType.RREP);
		p.addToPath(sink);
		sink.sendPacket(p, currentTime);
		
		while( currentTime<T ){
			int i = 0;
			long USER_QUERY_RATE = 500;
			// SIMULATE
			if( (currentTime % Constants.FLOOD_TIME_RATE) == 0 )
				simulateFlood(currentTime);
			if( (currentTime % USER_QUERY_RATE) == 0 )
				userQuery(currentTime);

			for( i=0; i<nodeList.size(); i++){
				nodeList.get(i).Execute(currentTime);				
			}
			for( i=0; i<nodeList.size(); i++){
				nodeList.get(i).resetCarrierLock();				
			}
			sink.resetCarrierLock();
			currentTime++;
		}
		
		totalDataPackets = sink.getTotalDataPacketsReceived();
		averageDelay = sink.getAverageDelay();
		for( int i=0; i<nodeList.size(); i++){
			System.out.println(nodeList.get(i).getId() +" nearest neighbour "+ nodeList.get(i).getNextNodeTosink()) ;				
		}
	}

	Simulator(int N) {

		nodeList = new ArrayList<Node>();
		gridDim = (int) Math.sqrt(N);
		totalDataPackets = 0;
		totalKHopDelay = 0;
		totalKHopRequests = 0;
		averageDelay = 0.0;
		int  i, j;
		for ( i=1; i<=gridDim; i++ ){
			for( j=1; j<=gridDim; j++ ){
				Node node = new Node(i, j, this);
				nodeList.add(node);				
			}
		}
		sink = new Sink(gridDim/2, gridDim/2);
		setNeighbours();
	}
	
	private void simulateFlood(long t){
		double randomNumber = Utility.generate.nextDouble();
		
		if( randomNumber < Constants.FLOOD_PROBABILITY ){

			double xFlood = (gridDim-1)*Utility.generate.nextDouble();
			double yFlood = (gridDim-1)*Utility.generate.nextDouble();
			
			int x1 = (int)xFlood + 1;
			int y1 = (int)yFlood + 1;
						
			nodeList.get(getIndex(x1,y1)).onFloodDetect(t);
			nodeList.get(getIndex(x1,y1+1)).onFloodDetect(t);
			nodeList.get(getIndex(x1+1,y1)).onFloodDetect(t);
			nodeList.get(getIndex(x1+1,y1+1)).onFloodDetect(t);
			
			
		}		
	}
	private void userQuery(long t){
		double xUserQuery = (gridDim-1)*Utility.generate.nextDouble();
		double yUserQuery = (gridDim-1)*Utility.generate.nextDouble();
		
		int x1 = (int)xUserQuery + 1;
		int y1 = (int)yUserQuery + 1;
					
		//nodeList.get(getIndex(x1,y1)).onKHopRequest(t);
		
	}
	private void setNeighbours(){
		int  i, j;
		int sinkPos = gridDim/2;
		for ( i=1; i<=gridDim; i++ ){
			for( j=1; j<=gridDim; j++ ){
				ArrayList<Node> neighbourList = new ArrayList<Node>();
				if( j<gridDim )
					neighbourList.add(nodeList.get(getIndex(i, j+1)));
				if( j>1 )
					neighbourList.add(nodeList.get(getIndex(i, j-1)));
				if( i>1 )
					neighbourList.add(nodeList.get(getIndex(i-1, j)));
				if( i<gridDim)
					neighbourList.add(nodeList.get(getIndex(i+1, j)));
				
				if( (Math.abs(i-sinkPos)<=1) && (Math.abs(j-sinkPos)<=1))
					neighbourList.add(sink);
				
				nodeList.get(getIndex(i, j)).setNeighbours(neighbourList);
			}
		}

		ArrayList<Node> neighbourList = new ArrayList<Node>();
		neighbourList.add(nodeList.get(getIndex(sinkPos+1, sinkPos)));
		neighbourList.add(nodeList.get(getIndex(sinkPos-1, sinkPos)));
		neighbourList.add(nodeList.get(getIndex(sinkPos, sinkPos-1)));
		neighbourList.add(nodeList.get(getIndex(sinkPos, sinkPos+1)));
		neighbourList.add(nodeList.get(getIndex(sinkPos, sinkPos)));
		sink.setNeighbours(neighbourList);
		
	}
	private int getIndex(int x, int y){
		return ((x-1)*gridDim + (y-1));
	}
	public long getTotalDataPacketsReceived(){
		return totalDataPackets;
	}
	public double getAverageDelay(){
		return averageDelay;
	}
	
	public void onKHopResponse(long delay){
		totalKHopRequests++;
		totalKHopDelay += delay ;
	}

}
