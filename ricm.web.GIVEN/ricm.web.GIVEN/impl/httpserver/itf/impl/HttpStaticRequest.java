package httpserver.itf.impl;

import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";

	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}

	public void process(HttpResponse resp) throws Exception {
		Path path = Paths.get(this.m_hs.getFolder() + this.m_ressname);
		try {
			long size = Files.size(path);
			resp.setReplyOk();
			resp.setContentLength((int) size);
			resp.setContentType(HttpRequest.getContentType(this.m_ressname));
			PrintStream ps = resp.beginBody();
			ps.writeBytes(Files.readAllBytes(path));
		} catch (IOException e) {
			resp.setReplyError(404, "file not found");
		}
	}

}
