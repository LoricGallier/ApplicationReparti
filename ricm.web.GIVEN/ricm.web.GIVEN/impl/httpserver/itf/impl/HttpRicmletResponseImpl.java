package httpserver.itf.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Set;
import java.util.Map.Entry;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpRicmletResponse;

/*
 * This class allows to build an object representing an HTTP response
 */
class HttpRicmletResponseImpl implements HttpRicmletResponse {
    protected HttpServer m_hs;
    protected PrintStream m_ps;
    protected HttpRequest m_req;

    protected HttpRicmletResponseImpl(HttpServer hs, HttpRequest req, PrintStream ps) {
        m_hs = hs;
        m_req = req;
        m_ps = ps;
    }

    public void setReplyOk() {
        m_ps.println("HTTP/1.0 200 OK");
        m_ps.println("Date: " + new Date());
        
        Set<Entry<String, String>> cookies = ((HttpRicmletRequestImpl) m_req).cookies.entrySet();
        if (cookies.size() > 0) {
            m_ps.print("Set-Cookie: ");
            for (Entry<String, String> cookie : cookies) {
                m_ps.print(cookie.getKey() + "=" + cookie.getValue() + ";");
            }
            m_ps.println();
        }
        
        m_ps.println("Server: ricm-http 1.0");
    }

    public void setReplyError(int codeRet, String msg) throws IOException {
        m_ps.println("HTTP/1.0 " + codeRet + " " + msg);
        m_ps.println("Date: " + new Date());
        m_ps.println("Server: ricm-http 1.0");
        m_ps.println("Content-type: text/html");
        m_ps.println();
        m_ps.println("<HTML><HEAD><TITLE>" + msg + "</TITLE></HEAD>");
        m_ps.println("<BODY><H4>HTTP Error " + codeRet + ": " + msg + "</H4></BODY></HTML>");
        m_ps.flush();
    }

    public void setContentLength(int length) {
        m_ps.println("Content-length: " + length);
    }

    public void setContentType(String type) {
        m_ps.println("Content-type: " + type);
    }

    /*
     * Insert an empty line to the response
     * and return the printstream to send the reply
     * 
     * @see httpserver.itf.HttpResponse#beginBody()
     */
    public PrintStream beginBody() {
        m_ps.println();
        return m_ps;
    }

    @Override
    public void setCookie(String name, String value) {
        ((HttpRicmletRequestImpl) this.m_req).cookies.put(name, value);
    }

}
