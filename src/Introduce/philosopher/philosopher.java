package Introduce.philosopher;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class philosopher extends Thread{
	@Override
	public void run(){
		ConcurrentHashMap map=new ConcurrentHashMap();
		Hashtable table=new Hashtable();
		ConcurrentLinkedQueue queue=new ConcurrentLinkedQueue();
		ArrayBlockingQueue queue2=new ArrayBlockingQueue(10);
		//BlockingQueue queue=new LinkedBlockingQueue();
		LinkedList<Integer> list=new LinkedList<Integer>();
		Object oj=new Object();
		synchronized(oj) {
			System.out.println("hello");
		}
		for(Integer i:list){
			
		}
	}
}
