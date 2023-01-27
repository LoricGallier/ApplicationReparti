package ricm.fileserver.threaded;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ricm.channels.IBroker;
import ricm.channels.IBrokerListener;
import ricm.channels.IChannel;
import ricm.channels.IChannelListener;

/*
 * Basic FileServer implementation 
 * 	The file is entirely read in memory before sending it to client
 */

public class FileServerApplication implements IBrokerListener, IChannelListener {

	IBroker engine;
	String folder;
	int port;
	public static final int NWORKERS = 5;
	public static final int MAX_REQUEST = 100;
	BlockingQueue<Request> requests;
	Thread workers[];

	public FileServerApplication(IBroker engine, String folder, int port) throws Exception {
		this.port = port;
		this.engine = engine;
		this.folder = folder;
		if (!folder.endsWith(File.separator))
			this.folder = folder + File.separator;

		requests = new ArrayBlockingQueue<Request>(MAX_REQUEST);

		workers = new Thread[NWORKERS];
		for (int i = 0; i < NWORKERS; i++) {
			workers[i] = new Worker(i,folder, requests);
			workers[i].setDaemon(true);
			workers[i].start();
		}

		this.engine.setListener(this);
		if (!this.engine.accept(port)) {
			System.err.println("Refused accept on "+port);
			System.exit(-1);
		}
	}

	/**
	 * Callback invoked when a message has been received. The message is whole, all
	 * bytes have been accumulated.
	 * 
	 * Returns an error code if the request failed: 
	 * 	-1: could not parse the request
	 * 	-2: file does not exist
	 * 	-3: unexpected error
	 * 
	 * @param channel
	 * @param bytes
	 */
	public void received(IChannel channel, byte[] bytes) {
		// System.out.println("Server: received a request ("+bytes.length+" bytes)");

		Request req = new Request(channel, bytes);
		while (true) {
			try {
				requests.put(req);
				break;
			} catch (InterruptedException ex) {}
	    }
	}

	@Override
	public void connected(IChannel c) {
		System.out.println("Unexpected connected");
		System.exit(-1);
	}

	@Override
	public void accepted(IChannel c) {
		System.out.println("Accepted");
		c.setListener(this);
	}

	@Override
	public void refused(String host, int port) {
		System.out.println("Refused " + host + ":" + port);
		System.exit(-1);
	}

	@Override
	public void closed(IChannel c, Exception e) {
		// System.out.println("Client closed channel");
	}
}
