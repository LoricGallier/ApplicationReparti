package ricm.fileserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

	void panic(String msg, Exception ex) {
		ex.printStackTrace(System.err);
		System.err.println("PANIC: "+msg);
		System.exit(-1);
	}

	public FileServerApplication(IBroker engine, String folder, int port) throws Exception {
		this.port = port;
		this.engine = engine;
		this.folder = folder;
		if (!folder.endsWith(File.separator))
			this.folder = folder + File.separator;
		this.engine.setListener(this);
		if (!this.engine.accept(port)) {
			System.err.println("Refused accept on "+port);
			System.exit(-1);
		}
	}

	byte[] readFile(String filename) {
		File f = new File(folder + filename);
		if (!f.exists() || !f.isFile())
			return null;
		byte[] bytes;
		int nbytes = (int) f.length();
		try {
			FileInputStream fis;
			fis = new FileInputStream(f);
			try {
				bytes = new byte[nbytes];
				for (int nread = 0; nread < f.length();) {
					int r;
					try {
						r = fis.read(bytes, nread, nbytes - nread);
						nread += r;
					} catch (IOException e) {
						return null;
					}
				}
			} finally {
				fis.close();
			}
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return bytes;
	}

	/**
	 * Callback invoked when a message has been received. The message is whole, all
	 * bytes have been accumulated.
	 * 
	 * Returns an error code if the request failed: -1: could not parse the request
	 * -2: file does not exist -3: unexpected error
	 * 
	 * @param channel
	 * @param bytes
	 */
	public void received(IChannel channel, byte[] request) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			try {
				InputStream is = new ByteArrayInputStream(request);
				DataInputStream dis = new DataInputStream(is);
				String filename;
				try {
					filename = dis.readUTF();
					System.out.println("FileServer - Receive request for downloading: " + filename);
				} catch (Exception ex) {
					dos.writeInt(-1); // could not parse the request
					return;
				}
				byte[] bytes;
				bytes = readFile(filename);
				if (bytes == null) {
					dos.writeInt(-2); // requested file does not exist
				} else {
					dos.writeInt(bytes.length);
					dos.write(bytes);
				}
			} catch (IOException ex) {
				ex.printStackTrace(System.err);
				dos.writeInt(-3);
			} finally {
				dos.close();
				byte[] bytes = os.toByteArray();
				channel.send(bytes);
			}
		} catch (Exception ex) {
			panic("unexpected exception", ex);
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
		System.out.println("Client closed channel");
	}
}
