package ricm.channels;

public interface IBrokerListener {

	/**
	 * Callback invoked when a message has been fully received
	 * All bytes has been received on the given channel
	 */
	public void connected(IChannel c);
	
	/**
	 * A connection was refused 
	 * @param port
	 */
	public void refused(String host, int port);

	/**
	 * Callback invoked when a message has been fully received
	 * All bytes has been received on the given channel
	 */
	public void accepted(IChannel c);
}
