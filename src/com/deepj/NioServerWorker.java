package com.deepj;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

import com.deepj.pool.NioSelectorRunablePool;
import com.deepj.pool.Worker;

public class NioServerWorker extends AbstractNioSelector implements Worker{

	public NioServerWorker(Executor executor, String threadName, NioSelectorRunablePool selectorRunablePool) {
		super(executor, threadName, selectorRunablePool);
	}

	@Override
	protected void process(Selector selector) throws IOException {
		Set<SelectionKey> selectKeys = selector.selectedKeys();
		if (selectKeys.isEmpty()) {
			return;
		}
		for (Iterator<SelectionKey> ite = selectKeys.iterator(); ite.hasNext();) {
			SelectionKey key = ite.next();
			ite.remove();
			
			SocketChannel channel = (SocketChannel) key.channel();
			
			// 数据总长度
			int ret = 0;
			boolean failure = true;
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			try {
				channel.read(buffer);
				failure = false;
			} catch (Exception e) {
				// ignore
			}
			// 判断连接是否已断开
			if (ret < 0 || failure) {
				key.cancel();
				System.out.println(this.threadName +"客户端已断开链接");
			} else {
				System.out.println("收到数据:" + new String(buffer.array()));
				
				// 回写数据
				ByteBuffer outBuffer = ByteBuffer.wrap("\n received..".getBytes());
				channel.write(outBuffer);
			}
			
			
		}
	}

	@Override
	protected int select(Selector selector) throws IOException {
		return selector.select(500);
	}
	
	public void registerNewChannelTask(final SocketChannel channel) {
		final Selector selector = this.selector;
		registerTask(new Runnable() {
			
			@Override
			public void run() {
				try {
					channel.register(selector, SelectionKey.OP_READ);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}



	

}
