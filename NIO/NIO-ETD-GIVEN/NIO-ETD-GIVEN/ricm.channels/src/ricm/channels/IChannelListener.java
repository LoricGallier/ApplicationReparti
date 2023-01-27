package ricm.channels;

public interface IChannelListener {

	/*
	 * Callback invoked when a message has been fully received
	 * All bytes has been received on the given channel
	 */
	public void received(IChannel c, byte[] bytes);

	/*
	 * Callback invoked when a channel was closed internally,
	 * mostly because the other side closed the channel. 
	 * Closing a local channel, invoking the method Channel.close(),
	 * does not invoke this listener.
	 * @param c is the closed channel
	 * @param e is the cause for the channel closing down, if available.
	 *        this parameter may be null.
	 */
	public void closed(IChannel c, Exception e);
}
