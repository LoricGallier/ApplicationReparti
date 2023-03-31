package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpSession;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpRicmlet;

import examples.HelloRicmlet;

/*
 * This class allows to build an object representing an HTTP dynamic request
 */
public class HttpRicmletRequestImpl extends HttpRicmletRequest {

    protected HttpSession session;
    private HashMap<String, String> arguments = new HashMap<String, String>();

    public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
        super(hs, method, ressname, br);
        this.session = null;
        String[] argsLine = ressname.split("\\?");
        if (argsLine.length > 1) {
            String[] args = argsLine[1].split("\\&");
            for (String arg : args) {
                String[] spl = arg.split("\\=");
                arguments.put(spl[0], spl[1]);
            }
        }

    }

    public HttpSession getSession() {
        if (session == null) {
            session = new Session(this.m_hs, "0");
        }
        return session;
    }

    public String getArg(String name) {
        return arguments.get(name);
    }

    public Object getCookie(String name) {
        if (getSession() != null) {
            return getSession().getValue(name);
        }
        return null;
    }

    @Override
    public void process(HttpResponse resp) throws Exception {
        String[] withoutArgs = this.m_ressname.split("\\?");
        String[] parsed = withoutArgs[0].split("/", 3);
        String pathName = parsed[2].replace('/', '.');

        try {
            HttpRicmlet ricmlet = this.m_hs.getInstance(pathName);
            // Path path = Paths.get(this.m_hs.getFolder() + this.m_ressname);

            // HttpRicmeltResponseImpl response = new HttpRicmeltResponseImpl(m_hs, this,
            // resp.ps);

            ricmlet.doGet(this, (HttpRicmletResponse) resp);

            // ps.writeBytes(Files.readAllBytes(path));
        } catch (ClassNotFoundException e) {
            resp.setReplyError(404, "Ricmlet class not found");
        } catch (NoSuchMethodException | SecurityException e) {
            resp.setReplyError(404, "Ricmlet class constructor not found");
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            resp.setReplyError(404, "Ricmlet class instance not found");
        } catch (IOException e) {
            resp.setReplyError(404, "Ricmlet not found");
        }

    }

}
