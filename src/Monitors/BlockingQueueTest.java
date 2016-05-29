package Monitors;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import utils.FileUtil;
import junit.framework.Assert;
import junit.framework.TestCase;

public class BlockingQueueTest extends TestCase{
	private static final int LOCKUP_DETECT_TIMEOUT=1000;
	private static final ExecutorService pool=Executors.newCachedThreadPool();
	private final AtomicInteger putSum=new AtomicInteger(0);
	private final AtomicInteger takeSum=new AtomicInteger(0);
	private final CyclicBarrier barrier;
	private final BarrierTimer timer;
	private final BlockingQueue<Integer> bq;
	private final int nTrials,nPairs;
	
	public static void TestIsEmptyWhenConstructed(){
		BlockingQueue queue=new BlockingQueue<Integer>(10);
		assertTrue(queue.isEmpty());
		assertFalse(queue.isFull());
	}
	public static void TestIsFullAfterPut(){
		BlockingQueue queue=new BlockingQueue<Integer>(10);
		for(int i=0;i<10;i++){
			queue.put(new Integer(i));
		}
		assertTrue(queue.isFull());
		assertFalse(queue.isEmpty());
		for(int i=0;i<10;i++){
			try {
				queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertTrue(queue.isEmpty());
		assertFalse(queue.isFull());
	}
	public static void TestTakeBlockWhenEmpty(){
		BlockingQueue queue=new BlockingQueue<Integer>(10);
		Thread thread=new Thread(){
			public void run(){
				try{
				int unused=(Integer)queue.take();
				fail();
				}catch(InterruptedException e){System.out.println("interrupted!");}
			}
		};
		try{
			thread.start();
			Thread.sleep(LOCKUP_DETECT_TIMEOUT);
			thread.interrupt();
			thread.join(LOCKUP_DETECT_TIMEOUT);
			assertFalse(thread.isAlive());
		}catch(Exception unexcept){
			fail();
		}
	}
	public static void main(String args[]){
		//TestIsEmptyWhenConstructed();
		//TestIsFullAfterPut();
		//TestTakeBlockWhenEmpty();
		float ans[][]=new float[4][8];
		for(int i=1,x=0;i<=1000;i*=10,x++){
			for(int j=1,y=0;j<=128;j*=2,y++){
				
				try {
					BlockingQueueTest test=new BlockingQueueTest(i, j, 100);
					float t1=test.test();
					Thread.sleep(1000);
					System.out.println();
					float t2=test.test();
					Thread.sleep(1000);
					System.out.println();
					ans[x][y]=(t1+t2)/2;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		FileUtil.MatrixToCSV(ans, 4, 8, "myqueue.csv");
		pool.shutdown();
	}
	
	BlockingQueueTest(int capacity,int npairs,int ntrials){
		this.bq=new BlockingQueue<Integer>(capacity);
		this.nPairs=npairs;
		this.nTrials=ntrials;
		this.timer=new BarrierTimer();
		this.barrier=new CyclicBarrier(npairs*2+1,timer);
	}
	static int xorShift(int y){
		y^=(y<<6);
		y^=(y>>>21);
		y^=(y<<7);
		return y;
	}
	float test(){
		try{
			timer.clear();
			for(int i=0;i<nPairs;i++){
				pool.execute(new Consumer());
				pool.execute(new Producer());
			}
			barrier.await();
			barrier.await();
			assertEquals(putSum.get(),takeSum.get());
			
			System.out.println("test with nPairs:"+nPairs+":and Cap:"+bq.getCap()+":time:"+timer.getNsTime()/(nPairs*(long)nTrials));
			return timer.getNsTime()/(nPairs*(long)nTrials);
		}catch(Exception e){
			throw new RuntimeException();
		}
	}
	class Producer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				int seed=this.hashCode()^(int)System.nanoTime();
				int sum=0;
				barrier.await();
				for(int i=nTrials;i>0;i--){
					bq.put(new Integer(seed));
					sum+=seed;
					seed=xorShift(seed);
				}
				putSum.getAndAdd(sum);
				barrier.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	class Consumer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				int sum=0;
				barrier.await();
				for(int i=nTrials;i>0;i--){
					sum+=bq.take();
				}
				takeSum.getAndAdd(sum);
				barrier.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	class BarrierTimer implements Runnable{
		long startTime;
		long endTime;
		boolean start;
		@Override
		public synchronized void run() {
			// TODO Auto-generated method stub
			long t=System.nanoTime();
			if(start){
				startTime=t;
				start=false;
			}
			else endTime=t;
		}
		public synchronized void clear(){
			start=true;
		}
		public synchronized long getNsTime(){
			return endTime-startTime;
		}
		
	}
}
