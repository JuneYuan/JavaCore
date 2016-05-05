package synch2;

// wait方法
public class ProducerConsumer {
	public static void main(String[] args) {
		// 生产者、消费者线程访问同一个资源
		SyncStack ss = new SyncStack();
		Producer p = new Producer(ss);
		Consumer c = new Consumer(ss);
		
		new Thread(p).start();
		new Thread(p).start();
		new Thread(p).start();
		new Thread(c).start();
	}
}

class WoTou {
	int id;

	WoTou(int id) {
		this.id = id;
	}

	public String toString() {
		return "WoTou : " + id;
	}
}

// 手工实现栈，表示一个工厂
class SyncStack {
	int index = 0;
	WoTou[] arrWT = new WoTou[6];

	public synchronized void push(WoTou wt) {
		while (index == arrWT.length) {			
			try {
				this.wait();  // 令访问当前对象的线程
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		arrWT[index++] = wt;
		
		this.notifyAll();	// 叫醒正在等待当前对象的线程

	}

	public synchronized WoTou pop() {
		while (index == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		WoTou ret = arrWT[--index];
		
		this.notifyAll();	// 叫醒正在等待当前对象的线程

		return ret;
	}
}

class Producer implements Runnable {
	SyncStack ss = null;

	Producer(SyncStack ss) {
		this.ss = ss;
	}

	public void run() {
		for (int i = 0; i < 20; i++) {
			WoTou wt = new WoTou(i);
			ss.push(wt);
			System.out.println("＋＋生产了：" + wt);
			
			try {
				Thread.sleep((int) (Math.random() * 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Consumer implements Runnable {
	SyncStack ss = null;

	Consumer(SyncStack ss) {
		this.ss = ss;
	}

	public void run() {
		for (int i = 0; i < 60; i++) {
			WoTou wt = ss.pop();
			System.out.println("－－消费了：" + wt);
			
			try {
				Thread.sleep((int) (Math.random() * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}