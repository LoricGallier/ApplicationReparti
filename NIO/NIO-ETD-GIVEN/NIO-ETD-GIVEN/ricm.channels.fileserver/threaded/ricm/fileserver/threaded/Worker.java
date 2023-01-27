package ricm.fileserver.threaded;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import ricm.channels.IChannel;

/*
 * Basic Worker for FileServer
 * Reads the entire file in memory before sending it to the client
 */
public class Worker extends Thread {
	private String m_folder;
	private BlockingQueue<Request> m_requests;
	private int no;
	
	public Worker(int no,String folder, BlockingQueue<Request> requests){
		this.no= no;
		m_requests = requests;
		m_folder = folder;
		if (!m_folder.endsWith(File.separator)) 
			m_folder = m_folder + File.separator;
	}

	public void run(){
		Request req;
		while(true){
			System.out.println("Worker-"+no+": Zzz...");
			try {
				req = m_requests.take();
			} catch (InterruptedException e) {
				System.out.println("Worker: interruptedException raised, retry serving the request");
				continue;   
			}
			process(req);
		}
	}
	
	void process(Request request) {
		// System.out.println("Worker-"+no+": processing a request...");
		IChannel channel = request.channel;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			try {
				InputStream is = new ByteArrayInputStream(request.bytes);
				DataInputStream dis = new DataInputStream(is);
				String filename;
				try {
					filename = dis.readUTF();
					System.out.println("Worker-"+no+" request for downloading: " + filename);
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
			ServerMain.panic("Worker-"+no+" unexpected exception", ex);
		}
	}

	byte[] readFile(String filename) {
		File f = new File(m_folder + filename);
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
}

