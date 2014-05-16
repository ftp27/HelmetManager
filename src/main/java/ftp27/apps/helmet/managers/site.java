package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;
import ftp27.apps.helmet.tools.templater;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class site {
    private static String LOG_TAG = "Class [site]";
    private boolean DEBUG = true;

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                                 Properties parms, Properties files) {

        Map<String,String> values = new HashMap<String,String>();


        String msg = getTemplate("header");

        String[] uris = uri.split("/");

        msg += getTemplate("content");
        msg += getTemplate("footer");

        if (DEBUG) {

            String debug ="";
            debug += "<strong>URI:</strong> " + uri + "<br>";
            debug += "<strong>method:</strong> " + method + "<br>";
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
                navcontent += getTemplate("file");
            }
        } else {
            navcontent += getTemplate("file");
        }

        values.put("navpanel", getTemplate("navpanel"));
        values.put("navcontent", navcontent);
        msg = templater.Compile(msg,values);

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg);
    }

    private String getTemplate(String templateName) {
        Log.d(LOG_TAG, "Checking file " + "/templates/" + templateName + ".html");
        InputStream stream = this.getClass().getResourceAsStream("/templates/"+templateName+".html");
        BufferedReader input = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();
        String aux = "";
        String message = "";

        try {
            while ((aux = input.readLine()) != null) {
                builder.append(aux);
            }
            message = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return message;
    }
}
