package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpSession;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpRicmlet;

/*
 * This class allows to build an object representing an HTTP dynamic request
 */
public class HttpRicmletRequestImpl extends HttpRicmletRequest {
    private HashMap<String, String> arguments = new HashMap<String, String>();
    public HashMap<String, String> cookies = new HashMap<>();

    public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
        super(hs, method, ressname, br);
        String[] argsLine = ressname.split("\\?");
        if (argsLine.length > 1) {
            String[] args = argsLine[1].split("\\&");
            for (String arg : args) {
                String[] spl = arg.split("\\=");
                arguments.put(spl[0], spl[1]);
            }
        }

        String line = br.readLine();
        while (line.length() != 0) {
            if (line.startsWith("Cookie: ")) {
                String[] split = line.replaceAll("Cookie: ", "").split("=");
                cookies.put(split[0], split[1]);
            }
            line = br.readLine();
        }
    }

    public HttpSession getSession() {
        String session_id = (String) this.getCookie("session-id");
        if (session_id == null || m_hs.sessions.get(session_id) == null) {
            Session s = new Session(m_hs, new Integer(Session.newId()).toString());
            m_hs.sessions.put(s.getId(), s);
            this.cookies.put("session-id", s.getId());
            return s;
        }

        return m_hs.sessions.get(session_id);
    }

    public String getArg(String name) {
        return arguments.get(name);
    }

    public Object getCookie(String name) {
        return this.cookies.get(name);
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
