package com.floodmonitoring;

import java.util.ArrayList;
import java.util.List;

public class Simulator {

	public void simulate(long T, int N){
		
		long t = 0;
		List<Node> nodeList = instantiate(N);
		while( t<T ){
			int i = 0;
			// SIMULATE
			for( i=0; i<nodeList.size(); i++){
				nodeList.get(i).Execute(t);				
			}
			t++;
		}
	}

	private List<Node> instantiate(int N) {

		List<Node> nodeList = new ArrayList<Node>();
		int dim = (int) Math.sqrt(N);
		int  i, j;
		for ( i=1; i<dim; i++ ){
			for( j=1; j<dim; j++ ){
				Node node = new Node(i, j);
				nodeList.add(node);				
			}
		}
		
		return nodeList;
	}
}
