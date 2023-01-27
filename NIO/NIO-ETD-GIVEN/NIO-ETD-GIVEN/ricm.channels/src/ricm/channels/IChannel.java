package ricm.channels;

/**
 * Represents a channel between two hosts.
 */
public interface IChannel {
	
	/*
	 * Register the listener to be notified about
	 * received messages and the unexpected close
	 * of this channel due to internal exceptions.
	 */
	public void setListener(IChannelListener l);

	/*
	 * Send the given message to the server to which the client is connected
	 * The message stays aliased until it is sent to the server
	 * @throws: IllegalStateException if closed.
	 */
	public void send(byte[] bytes, int offset, int count);
	
	/*
	 * Send the given message to the server to which the client is connected
	 * The message stays aliased until it is sent to the server
	 * @throws: IllegalStateException if closed.
	 */
	public void send(byte[] bytes);
	
	/*
	 * Close the channel
	 */
	public void close();

	/*
	 * @return true if this channel was closed.
	 */
	public boolean closed();

}

