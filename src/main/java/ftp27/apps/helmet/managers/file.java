package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.io.*;
import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class file {
    private static String LOG_TAG = "Class [file]";

    public static NanoHTTPD.Response request(String uri, String method, Properties header,
                          Properties parms, Properties files) {

        String[] uris = uri.split("/");
        String Address = "";//File.pathSeparator;
        String Parent = "";

            if (uris.length>2) {
                for (int i=2; i<uris.length; i++) {
                        Address += uris[i]+"/";
                    if (i == uris.length-2) {
                        Parent = new String(Address);
                    }
                }
            }

        Log.d(LOG_TAG, Address);

        String message = "{ \"fileName\": \""+uris[uris.length-1]+"\",";//Address+"<br>";

        if (Address.length() == 0) {
            Address = "/";
        }
        File file = new File(Address);
        //message += "[]: "+toLink(uri,"..")+"<br>";

        if (file.exists()) {
            message += "\"fileAddress\":\""+Address+"\",";
            if (file.isDirectory()) {
                message += "\"fileType\":\"directory\",";
                message += "\"files\" : [";

                File[] dir_files = file.listFiles();

                if (Address.length() > 1) {
                    message += "{ \"fileName\": \"..\",";
                    message += "\"fileAddress\":\"" + Parent + "\",";
                    message += "\"fileType\":\"directory\"";
                    message += "}";
                    if (dir_files.length > 0) {
                        message += ",";
                    }
                }

                if (dir_files.length > 0) {
                    for (int i=0; i<dir_files.length; i++) {
                        message += "{ \"fileName\": \""+dir_files[i].getName()+"\",";
                        message += "\"fileAddress\":\""+Address+dir_files[i].getName()+"/\",";
                        if (dir_files[i].isDirectory()) {
                            message += "\"fileType\":\"directory\"";
                        } else {
                            message += "\"fileType\":\"file\"";
                        }
                        message += "}";
                        if (i != dir_files.length-1) {
                            message += ",";
                        }
                        //message += "["+i+"]: "+toLink(uri,dir_files[i])+"<br>";
                    }
                }
                message += "]";
            } else {
                message += "\"fileType\":\"file\"";
            }
        } else {
            message += "\"fileType\":\"none\"";
        }

        message += "}";

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_JSON, message);
    }
}
