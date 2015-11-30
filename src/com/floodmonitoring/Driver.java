package com.floodmonitoring;

public class Driver {

	public static void main(String[] args) {


		double[] floodProb = {.2,.4,.6,.8};
		int[] dataGather = {10,20,30,40};
		int[] floodtime = {100,200,300,400};
		double[] Nodefault = {0.0008,0.0016,0.0024,0.0032};
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				for(int k=0;k<4;k++){
					for(int l=0;l<4;l++){
						Constants.setFloodProbability(floodProb[i]);
						Constants.setDataGatherRate(dataGather[j]);
						Constants.setFloodTimeRate(floodtime[k]);
						Constants.setNODEFAULTERSEED(Nodefault[l]);
                        Simulator simulator = new Simulator(16);
                        simulator.simulate(2000);
                        System.out.println("Avg Delay = "+simulator.getAverageDelay());
                        System.out.println("Avg khop Delay = "+simulator.getAverageKHopDelay());
                        System.out.println("Total packets = "+simulator.getTotalDataPacketsReceived());
                        System.out.println("Total KhopReq = "+simulator.getKHopRequests());
                        System.out.println("Flood Probability = "+floodProb[i]);
                        System.out.println("Data Gather rate = "+dataGather[j]);
                        System.out.println("Flood Time rate = "+floodtime[k]);
                        System.out.println("Node fault Probability = "+Nodefault[l]);
					}
				}
			}
		}

	}


}
