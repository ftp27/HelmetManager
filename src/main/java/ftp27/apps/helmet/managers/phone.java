package ftp27.apps.helmet.managers;

import ftp27.apps.helmet.server.NanoHTTPD;

import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class phone {
    public NanoHTTPD.Response request(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        String message = "";
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, message);
    }
}
