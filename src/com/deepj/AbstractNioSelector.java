package com.deepj;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.deepj.pool.NioSelectorRunablePool;

/**
 * 抽象selector线程类
 */
public abstract class AbstractNioSelector implements Runnable{


	/**
	 * 线程池
	 */
	private final Executor executor;
	
	/**
	 * 选择器
	 */
	protected Selector selector;
	
	/**
	 * 选择器wakenUp状态标记
	 */
	protected final AtomicBoolean wakenUp = new AtomicBoolean();
	
	/**
	 * 任务队列
	 */
	private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
	
	/**
	 * 线程名称
	 */
	protected String threadName;
	
	private NioSelectorRunablePool selectorPool;
	
	AbstractNioSelector(Executor executor, String threadName, 
			NioSelectorRunablePool selectorRunablePool) {
		this.executor = executor;
		this.threadName = threadName;
		this.selectorPool = selectorRunablePool;
		openSelector();
	}

	/**
	 * 获取selector并启动线程
	 */
	private void openSelector() {

		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create a selector.");
		}
		executor.execute(this);
	}

	@Override
	public void run() {
		Thread.currentThread().setName(this.threadName);
		while(true) {
			try {
				wakenUp.set(false);
				
				select(selector);
				
				processTaskQueue();
				
				process(selector);
			} catch (Exception e) {
				// ignore;
			}
		}
	}

	protected abstract void process(Selector selector) throws IOException ;
	
	protected abstract int select(Selector selector) throws IOException;

	private void processTaskQueue() {
		for (;;) {
			final Runnable task = taskQueue.poll();
			if (task == null) {
				break;
			}
			task.run();
		}
	}
	
	/**
	 * 获取线程管理对象
	 */
	public NioSelectorRunablePool getSelectorRunnablePool() {
		return selectorPool;
	}

	/**
	 * 注册一个任务并激活selector
	 */
	protected final void registerTask(Runnable task) {

		taskQueue.add(task);
		
		Selector selector = this.selector;
		
		if (selector != null) {
			if (wakenUp.compareAndSet(false, true)) {
				selector.wakeup();
			}
		} else {
			taskQueue.remove(task);
		}
	}

}
