package com.deepj.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import com.deepj.NioServerBoss;
import com.deepj.NioServerWorker;

/**
 *  管理线程池
 */
public class NioSelectorRunablePool {

	/**
	 * boss线程数组
	 */
	private final AtomicInteger bossIndex = new AtomicInteger();
	private Boss[] bosses;
	/**
	 * worker线程数组
	 */
	private final AtomicInteger workerIndex = new AtomicInteger();
	private Worker[] workeres;
	
	
	
	public NioSelectorRunablePool(Executor boss, Executor worker) {
		initBoss(boss, 1);
		initWorker(worker, Runtime.getRuntime().availableProcessors() * 2);
	}
	/**
	 * 初始化boss线程
	 */
	private void initBoss(Executor boss, int count) {
		this.bosses = new NioServerBoss[count];
		for (int i = 0; i < bosses.length; i++) {
			bosses[i] = new NioServerBoss(boss, "boss thread " + (i+1), this);
		}
	}

	/**
	 * 初始化worker线程
	 */
	private void initWorker(Executor worker, int count) {
		this.workeres = new NioServerWorker[count];
		for (int i = 0; i < workeres.length; i++) {
			workeres[i] = new NioServerWorker(worker, "worker thread " + (i+1), this);
		}
	}

	/**
	 * 获取一个worker
	 */
	public Worker nextWorker() {
		return workeres[Math.abs(workerIndex.getAndIncrement() % workeres.length)];
	}

	/**
	 * 获取一个boss
	 */
	public Boss nextBoss() {
		return bosses[Math.abs(bossIndex.getAndIncrement() % bosses.length)];
	}

}
