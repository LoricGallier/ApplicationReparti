package ricm.nio.babystep1;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Iterator;

/**
 * NIO elementary client 
 * Implements an overly simplified echo client system
 * Polytech/Info4/AR 
 * Author: F. Boyer, O. Gruber
 */

public class NioClient {
	private static final int INBUF_SZ = 2048;

	// The client channel to communicate with the server 
	private SocketChannel sc;

	// The selection key to register events of interests on the channel
	private SelectionKey skey;

	// NIO selector
	private Selector selector;

	// Buffers for outgoing messages & incoming messages
	ByteBuffer outBuffer;
	ByteBuffer inBuffer;

	// The message to send to the server
	byte[] first;  
	// The checksum of the last message sent to the server
	// Used to check if the received message is the same than the sent one
	byte[] digest;

	int nloops;

	/**
	 * NIO client initialization
	 * 
	 * @param serverName: the server name
	 * @param port: the server port
	 * @param payload: the message to send to the server
	 * @throws IOException
	 */
	public NioClient(String serverName, int port, byte[] payload) throws IOException {

		this.first = payload;

		// create a selector
		selector = SelectorProvider.provider().openSelector();

		// create a non-blocking socket channel
		sc = SocketChannel.open();
		sc.configureBlocking(false);

		// register a CONNECT interest for channel sc 
		skey = sc.register(selector, SelectionKey.OP_CONNECT);

		// request to connect to the server
		InetAddress addr;
		addr = InetAddress.getByName(serverName);
		sc.connect(new InetSocketAddress(addr, port));
	}

	/**
	 * The client forever-loops on the NIO selector - waiting for events on 
	 * registered channels - possible events are CONNECT, READ, WRITE
	 */
	public void loop() throws IOException {
		System.out.println("NioClient running");
		while (true) {
			// wait for some events
			selector.select();

			// get the keys for which the events occurred
			Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = (SelectionKey) selectedKeys.next();
				selectedKeys.remove();

				// process the event
				if (key.isValid() && key.isAcceptable())   // accept event
					handleAccept(key);
				if (key.isValid() && key.isReadable())     // read event
					handleRead(key);
				if (key.isValid() && key.isWritable())     // write event
					handleWrite(key);
				if (key.isValid() && key.isConnectable())  // connect event
					handleConnect(key);
			}
		}
	}

	/**
	 * Handle an accept event
	 * 
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleAccept(SelectionKey key) throws IOException {
		throw new Error("Unexpected accept");
	}

	/**
	 * Handle a connect event - finish to establish the connection
	 * 
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleConnect(SelectionKey key) throws IOException {
		assert (this.skey == key);
		assert (sc == key.channel());

		sc.finishConnect();
		skey.interestOps(SelectionKey.OP_READ);

		// once connected, send a message to the server
		digest = md5(first);
		send(first, 0, first.length);
	}

	/**
	 * Handle data to read
	 * 
	 * @param the key of the channel on which the incoming data waits to be received
	 */
	private void handleRead(SelectionKey key) throws IOException {
		assert (this.skey == key);
		assert (sc == key.channel());

		// Let's read the message
		inBuffer = ByteBuffer.allocate(INBUF_SZ);
		int n = sc.read(inBuffer);
		if (n == -1) {
			key.cancel();  // communication with server is broken
			sc.close(); 
			return;
		}

		byte[] data = new byte[inBuffer.position()];
		inBuffer.rewind();
		inBuffer.get(data);

		// Let's make sure we read the message we sent to the server
		byte[] md5 = md5(data);
		if (!md5check(digest, md5)) {
			System.out.println("Checksum Error!");
			return;
		}

		// Let's print the received message, assuming it is a UTF-8 string
		// since it is the format of the first message sent to the server.
		String msg = new String(data, Charset.forName("UTF-8"));
		System.out.println("NioClient received msg["+nloops+"]: " + msg.length());

		byte[] tmp = new byte[2*data.length];
		System.arraycopy(data, 0, tmp, 0, data.length);
		System.arraycopy(data, 0, tmp, data.length, data.length);
		digest = md5(tmp);
		send(tmp, 0, tmp.length);

		// send back the received message
		
		if (nloops++ > 500)
			System.exit(0);
	}

	/**
	 * Handle data to write
	 * assume the data to write is in outBuffer
	 * @param the key of the channel on which data can be sent
	 */
	private void handleWrite(SelectionKey key) throws IOException {
		assert (this.skey == key);
		assert (sc == key.channel());
		// write the output buffer to the socket channel
		sc.write(outBuffer);
		// remove the write interest & set READ interest
		key.interestOps(SelectionKey.OP_READ);
	}

	/**
	 * Request to send data
	 * 
	 * @param data: the byte array that should be sent
	 */
	public void send(byte[] data, int offset, int count) {
		// this is not optimized, we should try to reuse the same ByteBuffer
		outBuffer = ByteBuffer.wrap(data, offset, count);

		// register a WRITE interest
		skey.interestOps(SelectionKey.OP_WRITE);
	}



	public static void main(String args[]) throws IOException {
		int serverPort = NioServer.DEFAULT_SERVER_PORT;
		String serverAddress = "localhost";
		String msg = "Hello There...";
		String arg;

		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			if (arg.equals("-m")) {
				msg = args[++i];
			} else if (arg.equals("-p")) {
				serverPort = new Integer(args[++i]).intValue();
			} else if (arg.equals("-a")) {
				serverAddress = args[++i];
			}
		}
		byte[] bytes = msg.getBytes(Charset.forName("UTF-8"));
		NioClient nc = new NioClient(serverAddress, serverPort, bytes);
		nc.loop();
	}

	/*
	 * Wikipedia: The MD5 message-digest algorithm is a widely used hash function
	 * producing a 128-bit hash value. Although MD5 was initially designed to be
	 * used as a cryptographic hash function, it has been found to suffer from
	 * extensive vulnerabilities. It can still be used as a checksum to verify data
	 * integrity, but only against unintentional corruption. It remains suitable for
	 * other non-cryptographic purposes, for example for determining the partition
	 * for a particular key in a partitioned database.
	 */
	public static byte[] md5(byte[] bytes) throws IOException {
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes, 0, bytes.length);
			digest = md.digest();
		} catch (Exception ex) {
			throw new IOException(ex);
		}
		return digest;
	}

	public static boolean md5check(byte[] d1, byte[] d2) {
		if (d1.length != d2.length)
			return false;
		for (int i = 0; i < d1.length; i++)
			if (d1[i] != d2[i])
				return false;
		return true;
	}

	public static void echo(PrintStream ps, byte[] digest) {
		for (int i = 0; i < digest.length; i++)
			ps.print(digest[i] + ", ");
		ps.println();
	}

}
