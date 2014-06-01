package ftp27.apps.helmet.server;

import android.content.Context;
import android.util.Log;
import ftp27.apps.helmet.managers.*;
import ftp27.apps.helmet.tools.logger;
import ftp27.apps.helmet.tools.templater;
import sun.misc.Cleaner;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

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
        super(port, rootDir);
        this.startServer();

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
    public Response serve(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        Log.d(LOG_TAG, method + " '" + uri + "' ");
        try {
            uri = new String(uri.getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String ClientIP = header.getProperty("client-ip");



        if ((method.equals("POST")) && (parms.containsKey("password"))) {
            Log.d(LOG_TAG, method + " '" + parms.getProperty("password") + "' ");
            AccessManager.takeAccess(ClientIP, parms.getProperty("password"));
        }

        int AccessLevel = AccessManager.getAccessLevel(ClientIP);

        String msg = "";
        String[] uris = uri.split("/");


        if ((AccessLevel < 0) &&
                   ((uris.length < 1) ||
                    (!uris[1].toLowerCase().equals("res")))
        ) {
                return new NanoHTTPD.Response(
                        NanoHTTPD.HTTP_OK,
                        NanoHTTPD.MIME_HTML,
                        new templater().getTemplate("auth"));
        }

        if (uris.length > 0) {
            String action = uris[1].toLowerCase();
            if (!action.equals("res")) {
                Logger.statusMessage(ClientIP+" ["+method+"]: "+uri);
            }

            if (action.equals("file")) {
                return FileManager.request(uri, method, header, parms, files);
            } else if (action.equals("info")) {
                return PhoneManager.request(uri, method, header, parms, files);
            } else if (action.equals("res")) {
                return ResManager.request(uri, method, header, parms, files);
            } else if (action.equals("site")) {
                return SiteManager.request(uri, method, header, parms, files);
            }
        } else {
            return SiteManager.request(uri, method, header, parms, files);
        }
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg);
    }



}
