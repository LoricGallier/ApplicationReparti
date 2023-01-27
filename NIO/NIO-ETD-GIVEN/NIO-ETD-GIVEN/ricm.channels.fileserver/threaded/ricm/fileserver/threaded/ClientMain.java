package ricm.fileserver.threaded;

import ricm.channels.nio.Broker;

public class ClientMain {

	public static final int DEFAULT_SERVER_PORT = 8888;
	
	public static void main(String args[]) throws Exception {
		String filename = ".project";
		String hostname = "localhost";
		int port = DEFAULT_SERVER_PORT;
		int n = 1;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-p")) {
				port = new Integer(args[++i]).intValue();
			} else if (arg.equals("-h")) {
				hostname = args[++i];
			} else if (arg.equals("-f")) {
				filename = args[++i];
			} else if (arg.equals("-n")) {
				n = new Integer(args[++i]).intValue();
			}
		}
		
		Broker engine = new Broker();
		
		FileDownloadApplication client = new FileDownloadApplication(engine);
		
		for (int i=0;i<n;i++)
			client.download(hostname, port,filename,true);
		
		engine.run();
	}
}
