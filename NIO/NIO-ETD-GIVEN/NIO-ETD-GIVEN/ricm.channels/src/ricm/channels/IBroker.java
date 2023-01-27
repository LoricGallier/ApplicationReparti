package ricm.channels;

/**
 * Main interface to the broker.
 * The broker allows a process to connect to another process,
 * if that other process is accepting connection 
 * Nota Bene: 
 *   this interface is symmetric, a process can either 
 *   connect or accept, or do both.
 */
public interface IBroker {

	/*
	 * Set a listener to be notified when 
	 *   - a connect succeeds or fails
	 *   - a connection is accepted
	 */
	void setListener(IBrokerListener l);

	/**
	 * Request a connection to the given host and port.
	 * If the connection is attempted, it may be accepted
	 * or refused by the remote host. The outcome will be
	 * notified through the IBrokerListener listener.
	 * @param host
	 * @param port
	 * @return true if the connection was attempted,
	 *         false otherwise.
	 */
	boolean connect(String host, int port);

	/**
	 * Request to accept connections onto the given port.
	 * Accepted connections will be notified through 
	 * the IBrokerListener listener.
	 * @param port
	 * @return true if the accept was put in place,
	 *         false otherwise.
	 */
	boolean accept(int port);
	
}
