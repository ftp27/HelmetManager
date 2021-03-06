package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;
import ftp27.apps.helmet.server.NanoHTTPD.Response;
import ftp27.apps.helmet.tools.templater;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class site {
    private static String LOG_TAG = "Class [site]";
    private boolean DEBUG = false;

    public Response request(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms,
                            Map<String, String> files) {

        Map<String,String> values = new HashMap<String,String>();


        String msg = getTemplate("header");

        String[] uris = uri.split("/");

        msg += getTemplate("content");
        msg += getTemplate("footer");

        if (DEBUG) {

            String debug ="";
            debug += "<strong>URI:</strong> " + uri + "<br>";
            debug += "<strong>method:</strong> " + method + "<br>";
            debug += "<b>header:</b><br>"+genPropertyList(headers);
            debug += "<b>parms:</b><br>"+genPropertyList(parms);
            debug += "<strong>URI length:</strong> " + uris.length + "<br>";
            if (uris.length > 0) {
                for (int i = 0; i < uris.length; i++) {
                    debug += "<strong>URI[" + i + "]:</strong> " + uris[i] + "<br>";
                }
            }

            values.put("debuginfo",debug);
            msg += getTemplate("debug");
        }

        String navcontent = "";
        if (uris.length > 2) {
            String action = uris[2].toLowerCase();
            if (action.equals("file")) {
                values.put("requedjs","file");
                navcontent += getTemplate("file");
            } else if (action.equals("info")) {
                values.put("requedjs","info");
                navcontent += getTemplate("info");
            }

            values.put("navpanel", getTemplate("navpanel"));
            values.put("navcontent", navcontent);
            msg = templater.Compile(msg,values);

        } else {
            values.put("location","/site/file");
            msg = templater.Compile(getTemplate("relocation"),values);
        }
        return new Response(msg);
    }

    private String getTemplate(String templateName) {
        return new templater().getTemplate(templateName);
    }

    private String genPropertyList(Map<String, String> properties) {
        String answer = "";
        for (String key: properties.keySet()) {
            answer += "<i>"+key+":</i> "+properties.get(key)+"<br>";
        }
        return answer;
    }
}
