package com.floodmonitoring;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by sony on 11/23/2015.
 */
public class MemoryQueue {
    Queue<Packet> memQueue;
    private int size;
    MemoryQueue(int mSize){
        memQueue = new ArrayBlockingQueue<Packet>(mSize);
    }
    public void addPacket(Packet packet){
        if(memQueue.size()<5){
            memQueue.add(packet);
        }
        else{
            memQueue.remove();
            memQueue.add(packet);
        }
    }
    public boolean inMemory(Packet packet){
        return memQueue.contains(packet);
    }
}
