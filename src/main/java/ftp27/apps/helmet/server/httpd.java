package ftp27.apps.helmet.server;

import android.content.Context;
import android.util.Log;
import ftp27.apps.helmet.managers.*;
import ftp27.apps.helmet.tools.logger;
import ftp27.apps.helmet.tools.templater;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by ftp27 on 04.05.14.
 */
public class httpd extends NanoHTTPD {
    private static String LOG_TAG = "Class [httpd]";

    private phone PhoneManager;
    private site SiteManager;
    private file FileManager;
    private res ResManager;
    private auth AccessManager;
    private logger Logger;

    private Context context;


    public httpd(int port, File rootDir, auth AccessManager) throws IOException {
        super(port);
        this.start();

        this.AccessManager = AccessManager;
        this.context = AccessManager.getContext();
        this.Logger = AccessManager.getLogger();

        PhoneManager = new phone(context);
        SiteManager = new site();
        ResManager = new res();
        FileManager = new file();

        Log.d(LOG_TAG, "startServer");
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms,
                          Map<String, String> files) {
        Log.d(LOG_TAG, method + " '" + uri + "' ");



        try {
            uri = new String(uri.getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ClientIP = headers.get("client-ip");



        if ((method == Method.POST) && (parms.containsKey("password"))) {
            Log.d(LOG_TAG, method + " '" + parms.get("password") + "' ");
            AccessManager.takeAccess(ClientIP, parms.get("password"));
        }

        int AccessLevel = AccessManager.getAccessLevel(ClientIP);

        String msg = "";
        String[] uris = uri.split("/");


        if ((AccessLevel < 0) &&
                   ((uris.length < 1) ||
                    (!uris[1].toLowerCase().equals("res")))
        ) {
                return new Response(new templater().getTemplate("auth"));
        }

        if (uris.length > 0) {
            String action = uris[1].toLowerCase();
            if (!action.equals("res")) {
                Logger.statusMessage(ClientIP+" ["+method+"]: "+uri);
            }

            if (action.equals("file")) {
                return FileManager.request(uri, method, headers, parms, files);
            } else if (action.equals("info")) {
                return PhoneManager.request(uri, method, headers, parms, files);
            } else if (action.equals("res")) {
                return ResManager.request(uri, method, headers, parms, files);
            } else if (action.equals("site")) {
                return SiteManager.request(uri, method, headers, parms, files);
            } else if (action.equals("download")) {
                return ResManager.download(uri, method, headers, parms, files);
            } else if (action.equals("upload")) {


                return FileManager.upload(uri, method, headers, parms, files);
            }
        } else {
            return SiteManager.request(uri, method, headers, parms, files);
        }
        return new NanoHTTPD.Response(msg);
    }

    public String outputProperty(Map<String, String> prop) {
        for (String key: prop.keySet()) {
            Log.d(LOG_TAG,key);
        }
        return "";
    }

}
