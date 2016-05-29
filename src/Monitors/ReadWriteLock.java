package Monitors;

public class ReadWriteLock {
	public ReadLock readLock;
	public WriteLock writeLock;
	private boolean writer;
	private int reader;
	public ReadWriteLock(){
		this.readLock=new ReadLock();
		this.writeLock=new WriteLock();
		this.writer=false;
		this.reader=0;
	}
	class ReadLock{
		public void lock(){
			synchronized(ReadWriteLock.this){
					try {
						while(writer)
							ReadWriteLock.this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					reader++;
			}
		}
		public void unlock(){
			synchronized(ReadWriteLock.this){
				reader--;
				if(reader==0)
					ReadWriteLock.this.notifyAll();
			}
		}
	}
	class WriteLock{
		public void lock(){
			synchronized(ReadWriteLock.this){
				
					try {
						while(writer||reader>0)
							ReadWriteLock.this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					writer=true;
			}
		}
		public void unlock(){
			synchronized(ReadWriteLock.this){
				writer=false;
				ReadWriteLock.this.notifyAll();
			}
		}
	}
}
