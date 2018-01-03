package com.ankhrom.base.networking.volley;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBuilder {

    private String url;
    private int method;
    private byte[] body;
    private String contentType;
    private Map<String, String> header;
    private Map<String, String> params;
    private Response.Listener listener;
    private Response.ErrorListener errorListener;

    private RequestBuilder() {

        contentType = "application/json; charset=utf-8";

        header = new LinkedHashMap<>();
        header.put("accept", "*/*");
    }

    public static RequestBuilder get(String url) {

        return request(Request.Method.GET, url);
    }

    public static RequestBuilder post(String url) {

        return request(Request.Method.POST, url);
    }

    public static RequestBuilder put(String url) {

        return request(Request.Method.PUT, url);
    }

    public static RequestBuilder request(int method, String url) {

        RequestBuilder builder = new RequestBuilder();

        builder.url = url;
        builder.method = method;

        return builder;
    }

    public RequestBuilder body(byte[] body) {

        this.body = body;
        return this;
    }

    public RequestBuilder body(String body) {

        this.body = body.getBytes();
        return this;
    }

    public RequestBuilder body(Object body) {

        this.body = new Gson().toJson(body).getBytes();
        return this;
    }

    public RequestBuilder contentType(String contentType) {

        this.contentType = contentType;
        return this;
    }

    public RequestBuilder header(String key, String value) {

        header.put(key, value);
        return this;
    }

    public RequestBuilder param(String key, String value) {

        if (params == null) {
            params = new LinkedHashMap<>();
        }

        params.put(key, value);

        return this;
    }

    public RequestBuilder listener(Response.Listener listener) {

        this.listener = listener;
        return this;
    }

    public RequestBuilder errorListener(Response.ErrorListener errorListener) {

        this.errorListener = errorListener;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> BaseVolleyRequest<T> asGson(@NonNull Type type) {

        return new GsonRequest<T>(type, method, url, contentType, header, params, body, listener, errorListener);
    }

    @SuppressWarnings("unchecked")
    public BaseVolleyRequest<String> asString() {

        return new StringRequest(method, url, contentType, header, params, body, listener, errorListener);
    }

    @SuppressWarnings("unchecked")
    public BaseVolleyRequest<byte[]> asBytes() {

        return new ByteRequest(method, url, contentType, header, params, body, listener, errorListener);
    }
}
