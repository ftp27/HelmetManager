package ftp27.apps.helmet.server;

import android.util.Log;
import ftp27.apps.helmet.managers.file;
import ftp27.apps.helmet.managers.phone;
import ftp27.apps.helmet.managers.res;
import ftp27.apps.helmet.managers.site;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by ftp27 on 04.05.14.
 */
public class httpd extends NanoHTTPD {
    private static String LOG_TAG = "Class [httpd]";

    private phone Phone;
    private site Site;
    private res Res;


    public httpd(int port, File rootDir) throws IOException {
        super(port, rootDir);
        this.startServer();

        Phone = new phone();
        Site = new site();
        Res = new res();

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

        String msg = "";
        String[] uris = uri.split("/");
        if (uris.length > 0) {
            String action = uris[1].toLowerCase();
            if (action.equals("file")) {
                return file.request(uri, method, header, parms, files);
            } else if (action.equals("info")) {
                return Phone.request(uri, method, header, parms, files);
            } else if (action.equals("res")) {
                return Res.request(uri, method, header, parms, files);
            } else if (action.equals("site")) {
                return Site.request(uri, method, header, parms, files);
            }
        } else {
            return Site.request(uri, method, header, parms, files);
        }
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg);
    }



}
