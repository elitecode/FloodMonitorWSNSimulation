package com.floodmonitoring;

public class Constants {
	
	public static int RANDOMSEED = 0;
	public static double REGULARBATTERYDECREASE=0.01;
	public static double RECEIVEBATTERYDECREASE=0.1;
	public static double SENDBATTERYDECREASE=0.3;
	public static double NODEFAULTERSEED=0.000;
	public static double FLOOD_PROBABILITY = 0.8;
	public static long FLOOD_TIME_RATE =  100;
	public static long DATA_GATHER_RATE = 20;
	public static int MEMORY_SIZE = 5;

	public static void setRANDOMSEED(int RANDOMSEED) {
		Constants.RANDOMSEED = RANDOMSEED;
	}

	public static double getFloodProbability() {
		return FLOOD_PROBABILITY;
	}

	public static double getNODEFAULTERSEED() {
		return NODEFAULTERSEED;
	}

	public static int getRANDOMSEED() {
		return RANDOMSEED;
	}

	public static double getREGULARBATTERYDECREASE() {
		return REGULARBATTERYDECREASE;
	}

	public static void setREGULARBATTERYDECREASE(double REGULARBATTERYDECREASE) {
		Constants.REGULARBATTERYDECREASE = REGULARBATTERYDECREASE;
	}

	public static double getRECEIVEBATTERYDECREASE() {
		return RECEIVEBATTERYDECREASE;
	}

	public static void setRECEIVEBATTERYDECREASE(double RECEIVEBATTERYDECREASE) {
		Constants.RECEIVEBATTERYDECREASE = RECEIVEBATTERYDECREASE;
	}

	public static double getSENDBATTERYDECREASE() {
		return SENDBATTERYDECREASE;
	}

	public static void setSENDBATTERYDECREASE(double SENDBATTERYDECREASE) {
		Constants.SENDBATTERYDECREASE = SENDBATTERYDECREASE;
	}

	public static void setNODEFAULTERSEED(double NODEFAULTERSEED) {
		Constants.NODEFAULTERSEED = NODEFAULTERSEED;
	}

	public static void setFloodProbability(double floodProbability) {
		FLOOD_PROBABILITY = floodProbability;
	}

	public static long getFloodTimeRate() {
		return FLOOD_TIME_RATE;
	}

	public static void setFloodTimeRate(long floodTimeRate) {
		FLOOD_TIME_RATE = floodTimeRate;
	}

	public static long getDataGatherRate() {
		return DATA_GATHER_RATE;
	}

	public static void setDataGatherRate(long dataGatherRate) {
		DATA_GATHER_RATE = dataGatherRate;
	}

	public static int getMemorySize() {
		return MEMORY_SIZE;
	}

	public static void setMemorySize(int memorySize) {
		MEMORY_SIZE = memorySize;
	}
}
