package com.deepj;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.deepj.pool.NioSelectorRunablePool;

public class Starter {

	public static void main(String[] args) {

		// ��ʼ���̳߳�
		ExecutorService boss = Executors.newCachedThreadPool();
		ExecutorService worker = Executors.newCachedThreadPool();
		NioSelectorRunablePool pool = new NioSelectorRunablePool(boss, worker);
		
		// ��ȡ������
		ServerBootstrap bootstrap = new ServerBootstrap(pool);
		
		// �󶨶˿�
		bootstrap.bind(new InetSocketAddress(10101));
		
		System.out.println("Mini netty started !!!");
		
	}

}
