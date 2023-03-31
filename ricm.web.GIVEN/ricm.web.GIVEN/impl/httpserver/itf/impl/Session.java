package httpserver.itf.impl;

import java.util.HashMap;
import java.util.Set;

import httpserver.itf.HttpSession;

class Session implements HttpSession {

    private String id;
    private HttpServer hs;
    private HashMap<String, Object> cookies = new HashMap<>();

    public Session(HttpServer hs, String id) {
        this.id = id;
        this.hs = hs;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getValue(String key) {
        return this.cookies.get(key);
    }

    @Override
    public void setValue(String key, Object value) {
        this.cookies.put(key, value);
    }

    public Set<String> getCookiesName() {
        return this.cookies.keySet();
    }

}