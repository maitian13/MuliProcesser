package Monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<E> {
	private ReentrantLock lock;
	private Condition notFull;
	private Condition notEmpty;
	private final int capacity;
	private int head;
	private int tail;
	private int count;
	private E[] data;
	public BlockingQueue(int c){
		this.lock=new ReentrantLock();
		this.notEmpty=lock.newCondition();
		this.notFull=lock.newCondition();
		this.capacity=c;
		this.count=0;
		this.head=0;
		this.tail=0;
		data=(E[])new Object[c+1];
	}
	public void put(E e){
		lock.lock();
		try{
			while(this.count==this.capacity)
				notFull.await();
			tail++;
			count++;
			if(tail==this.capacity+1)tail=0;
			data[tail]=e;
			//System.out.println("put:tail "+tail+":head:"+head+":count:"+count);
			notEmpty.signal();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	public int getCap(){
		return this.capacity;
	}
	public boolean isEmpty(){
		lock.lock();
		try{
			return count==0?true:false;
		}finally{
			lock.unlock();
		}
	}
	public boolean isFull(){
		lock.lock();
		try{
			return count==capacity?true:false;
		}finally{
			lock.unlock();
		}
	}
	public E take() throws InterruptedException{
		lock.lock();
		try{
			while(this.count==0)
				notEmpty.await();
			head++;
			count--;
			if(head==this.capacity+1)head=0;
			E x=data[head];
			//System.out.println("take:tail "+tail+":head:"+head+":count:"+count);
			notFull.signal();
			return x;
		}finally{
			lock.unlock();
		}
	}
}
