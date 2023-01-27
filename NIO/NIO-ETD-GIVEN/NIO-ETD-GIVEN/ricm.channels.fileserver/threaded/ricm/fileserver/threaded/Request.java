package ricm.fileserver.threaded;

import ricm.channels.IChannel;

public class Request {
	IChannel channel;
	byte[] bytes;
	Request(IChannel channel,byte[] bytes) {
		this.channel = channel;
		this.bytes = bytes;
	}
}