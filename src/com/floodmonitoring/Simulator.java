package com.floodmonitoring;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
	private List<Node> nodeList;
	private int gridDim;
	public void simulate(long T){
		
		long t = 0;
		while( t<T ){
			int i = 0;
			// SIMULATE
			if( (t % Constants.FLOOD_TIME_RATE) == 0 )
				simulateFlood(t);
			for( i=0; i<nodeList.size(); i++){
				nodeList.get(i).Execute(t);				
			}
			t++;
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
	}
	
	private void simulateFlood(long t){
		double randomNumber = NodeUtility.generate.nextDouble();
		
		if( randomNumber < Constants.FLOOD_PROBABILITY ){

			double xFlood = gridDim*NodeUtility.generate.nextDouble();
			double yFlood = gridDim*NodeUtility.generate.nextDouble();
			
			int x1 = (int)xFlood + 1;
			int y1 = (int)yFlood + 1;
			
			nodeList.get(getIndex(x1,y1)).onFloodDetect(t);
			nodeList.get(getIndex(x1,y1+1)).onFloodDetect(t);
			nodeList.get(getIndex(x1+1,y1)).onFloodDetect(t);
			nodeList.get(getIndex(x1+1,y1+1)).onFloodDetect(t);
			
			
		}		
	}
	
	private int getIndex(int x, int y){
		return ((x-1)*gridDim + (y-1));
	}
}
