package ricm.fileserver;

import ricm.channels.nio.Broker;

public class ServerMain {

	public static final int DEFAULT_SERVER_PORT = 8888;
	public static final String DEFAULT_FOLDER = ".";

	public static void main(String args[]) throws Exception {
		int port = DEFAULT_SERVER_PORT;
		String folder = DEFAULT_FOLDER;
		String arg;

		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			if (arg.equals("-p")) {
				port = new Integer(args[++i]).intValue();
			}
			if (arg.equals("-f")) {
				folder = args[++i];
			}
		}
		Broker engine = new Broker();
		
		new FileServerApplication(engine, folder, port);

		engine.run();
	}

}
