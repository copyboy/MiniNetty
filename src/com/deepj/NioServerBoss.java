package com.deepj;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

import com.deepj.pool.Boss;
import com.deepj.pool.NioSelectorRunablePool;
import com.deepj.pool.Worker;

public class NioServerBoss extends AbstractNioSelector implements Boss{

	public NioServerBoss(Executor executor, String threadName,
			NioSelectorRunablePool selectorRunablePool) {
		super(executor, threadName, selectorRunablePool);
	}


	@Override
	protected void process(Selector selector) throws IOException {
		Set<SelectionKey> selectKeys = selector.selectedKeys();
		if (selectKeys.isEmpty()) {
			return;
		}
		for (Iterator<SelectionKey> i = selectKeys.iterator(); i.hasNext();) {
			SelectionKey key = i.next();
			i.remove();
			
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			// 新客户端
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			
			// 获取工作worker
			Worker nextWorker = getSelectorRunnablePool().nextWorker();
			// 注册新客户端接入任务
			nextWorker.registerNewChannelTask(channel);
			System.out.println("您有新的客户上线！！！");
			
		}
	}
	
	public void registerAcceptChannelTask(final ServerSocketChannel serverChannel) {
		final Selector selector = this.selector;
		registerTask(new Runnable() {
			
			@Override
			public void run() {
				try {
					serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	protected int select(Selector selector) throws IOException {
		return selector.select();
	}

}
