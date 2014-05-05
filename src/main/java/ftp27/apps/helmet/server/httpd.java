package ftp27.apps.helmet.server;

import android.util.Log;
import ftp27.apps.helmet.managers.file;
import ftp27.apps.helmet.managers.phone;
import ftp27.apps.helmet.managers.res;
import ftp27.apps.helmet.managers.site;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by ftp27 on 04.05.14.
 */
public class httpd extends NanoHTTPD {
    private phone Phone;
    private site Site;
    private res Res;


    public httpd(int port, File rootDir) throws IOException {
        super(port, rootDir);
        this.startServer();

        Phone = new phone();
        Site = new site();
        Res = new res();

        Log.d("httpd", "startServer");
    }

    @Override
    public Response serve(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        Log.d("httpd", method + " '" + uri + "' ");

        String msg = "";
        String[] uris = uri.split("/");
        if (uris.length > 0) {
            if (uris[1].toLowerCase().equals("file")) {
                return file.request(uri, method, header, parms, files);
            } else if (uris[1].toLowerCase().equals("info")) {
                return Phone.request(uri, method, header, parms, files);
            } else if (uris[1].toLowerCase().equals("res")) {
                return Res.request(uri, method, header, parms, files);
            } else {
                return Site.request(uri, method, header, parms, files);
            }
        } else {
            return Site.request(uri, method, header, parms, files);
        }
    }



}
