package com.deepj.pool;

import java.nio.channels.ServerSocketChannel;

public interface Boss {

	void registerAcceptChannelTask(ServerSocketChannel serverChannel);

}
