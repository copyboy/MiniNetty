package com.deepj.pool;

import java.nio.channels.SocketChannel;

public interface Worker {

	void registerNewChannelTask(SocketChannel channel);

}
