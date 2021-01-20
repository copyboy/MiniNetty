package com.deepj;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.deepj.pool.NioSelectorRunablePool;

public class Starter {

	public static void main(String[] args) {

		// 初始化线程池
		ExecutorService boss = Executors.newCachedThreadPool();
		ExecutorService worker = Executors.newCachedThreadPool();
		NioSelectorRunablePool pool = new NioSelectorRunablePool(boss, worker);
		
		// 获取服务类
		ServerBootstrap bootstrap = new ServerBootstrap(pool);
		
		// 绑定端口
		bootstrap.bind(new InetSocketAddress(10101));
		
		System.out.println("Mini netty started !!!");
		
	}

}
