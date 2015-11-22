package com.floodmonitoring;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
	private List<Node> nodeList;
	private int gridDim;
	private Sink sink;
	
	public void simulate(long T){
		
		long currentTime = 0;
		while( currentTime<T ){
			int i = 0;
			// SIMULATE
			if( (currentTime % Constants.FLOOD_TIME_RATE) == 0 )
				simulateFlood(currentTime);
			for( i=0; i<nodeList.size(); i++){
				nodeList.get(i).Execute(currentTime);				
			}
			for( i=0; i<nodeList.size(); i++){
				nodeList.get(i).resetCarrierLock();				
			}
			currentTime++;
		}
	}

	Simulator(int N) {

		nodeList = new ArrayList<Node>();
		gridDim = (int) Math.sqrt(N);
		int  i, j;
		for ( i=1; i<=gridDim; i++ ){
			for( j=1; j<=gridDim; j++ ){
				Node node = new Node(i, j);
				nodeList.add(node);				
			}
		}
		sink = new Sink(gridDim/2, gridDim/2);
		setNeighbours();
	}
	
	private void simulateFlood(long t){
		double randomNumber = Utility.generate.nextDouble();
		
		if( randomNumber < Constants.FLOOD_PROBABILITY ){

			double xFlood = gridDim*Utility.generate.nextDouble();
			double yFlood = gridDim*Utility.generate.nextDouble();
			
			int x1 = (int)xFlood + 1;
			int y1 = (int)yFlood + 1;
			
			nodeList.get(getIndex(x1,y1)).onFloodDetect(t);
			nodeList.get(getIndex(x1,y1+1)).onFloodDetect(t);
			nodeList.get(getIndex(x1+1,y1)).onFloodDetect(t);
			nodeList.get(getIndex(x1+1,y1+1)).onFloodDetect(t);
			
			
		}		
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
		neighbourList.add(nodeList.get(getIndex(sinkPos+1, sinkPos+1)));
		neighbourList.add(nodeList.get(getIndex(sinkPos+1, sinkPos-1)));
		neighbourList.add(nodeList.get(getIndex(sinkPos-1, sinkPos-1)));
		neighbourList.add(nodeList.get(getIndex(sinkPos-1, sinkPos+1)));
		sink.setNeighbours(neighbourList);
		
	}
	private int getIndex(int x, int y){
		return ((x-1)*gridDim + (y-1));
	}
}
