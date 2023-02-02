package com.bitnemo.testng.utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class HttpUtils {
    private JSONObject createCookieFromHeader(String url, String s){
        boolean hasDomain = false;
        String[] arrKeyVal = s.split(";");
        JSONObject json = new JSONObject();
        json.put("name",arrKeyVal[0].split("=")[0].trim());
        try {
            json.put("value",arrKeyVal[0].split("=")[1].trim());
        } catch (Exception ex){
            json.put("value","");
        }

        for (int i = 1; i<arrKeyVal.length; i++){
            if (arrKeyVal[i].contains("=")) {
                String[] tmp = arrKeyVal[i].split("=");
                if (tmp[0].equalsIgnoreCase("domain")) hasDomain = true;
                json.put(tmp[0].toLowerCase().trim(), tmp[1].trim());
            }
            else
                json.put("is"+arrKeyVal[i].trim(),true);
        }
        if (!hasDomain) {
            try {
                URL urlObj = new URL(url);
                json.put("domain",urlObj.getHost());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return json;
    }

    public JSONArray parseCookieFromHeaders(String url, Header[] headers, List<String> keyCookiesToGet){
        JSONArray cookies = new JSONArray();
        for (Header header : headers)
            if (header.getName().toLowerCase().contains("set-cookie")){
            JSONObject cookie = createCookieFromHeader(url, header.getValue());
            if ((keyCookiesToGet == null || keyCookiesToGet.contains(cookie.get("name")))) {
                cookies.put(cookie);
            }
        }
        return cookies;
    }

    public JSONArray getCookiesFromHttp(String url, List<String> keyCookiesToGet){
        JSONArray cookies = new JSONArray();
        try {
            String payload = "{\"username\":\"quydoan\",\"password\":\"abc\"}";
            StringEntity entity = new StringEntity(payload,
                    ContentType.APPLICATION_FORM_URLENCODED);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);
            System.out.println(response.getStatusLine().getStatusCode());
            cookies = parseCookieFromHeaders(url, response.getAllHeaders(), keyCookiesToGet);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return cookies;
    }
}
