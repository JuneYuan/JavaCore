package unsynch;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 银行：包含若干账户
 * A bank with a number of bank accounts.
 */
public class Bank
{
   private final double[] accounts;
   // 增加锁对象和条件对象
   private Lock bankLock;
   private Condition sufficientFunds;

   /**
    * Constructs the bank.
    * @param n the number of accounts
    * @param initialBalance the initial balance for each account
    */
   public Bank(int n, double initialBalance)
   {
      accounts = new double[n];
      for (int i = 0; i < accounts.length; i++)
         accounts[i] = initialBalance;
      bankLock = new ReentrantLock();
      sufficientFunds = bankLock.newCondition();
   }

   /**
    * 执行转账操作，并打印明细和余额
    * Transfers money from one account to another.
    * @param from the account to transfer from
    * @param to the account to transfer to
    * @param amount the amount to transfer
    * @throws InterruptedException 
    */
	public void transfer(int from, int to, double amount) throws InterruptedException {
		bankLock.lock();  // 进入临界区
		try {
			while (accounts[from] < amount)  // 条件不满足时等待条件锁
				sufficientFunds.await();	 // 将当前线程放到条件的等待集中
				
			System.out.print(Thread.currentThread());
			accounts[from] -= amount;
			System.out.printf(" %10.2f from %d to %d", amount, from, to);
			accounts[to] += amount;
			System.out.printf(" Total Balance: %10.2f%n", getTotalBalance());
			
			sufficientFunds.signalAll();	 // 解除此条件等待集中所有线程的阻塞状态
		} finally {
			bankLock.unlock();
		}
/*		if (accounts[from] < amount)
			return;*/

	}

   /**
    * Gets the sum of all account balances.
    * @return the total balance
    */
   public double getTotalBalance()
   {
      double sum = 0;

      for (double a : accounts)
         sum += a;

      return sum;
   }

   /**
    * Gets the number of accounts in the bank.
    * @return the number of accounts
    */
   public int size()
   {
      return accounts.length;
   }
}
