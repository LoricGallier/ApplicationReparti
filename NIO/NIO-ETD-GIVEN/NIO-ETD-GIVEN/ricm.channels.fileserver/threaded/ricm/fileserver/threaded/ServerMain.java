package ricm.fileserver.threaded;

import ricm.channels.nio.Broker;

public class ServerMain {

	public static final int DEFAULT_SERVER_PORT = 8888;
	public static final String DEFAULT_FOLDER = ".";

	public static void panic(String msg, Exception ex) {
		ex.printStackTrace(System.err);
		System.err.println("PANIC: "+msg);
		System.exit(-1);
	}

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
