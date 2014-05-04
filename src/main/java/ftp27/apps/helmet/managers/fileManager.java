package ftp27.apps.helmet.managers;

import java.io.File;
import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class fileManager {

    public static String request(String uri, String method, Properties header,
                          Properties parms, Properties files) {

        String[] uris = uri.split("/");
        String Address = "/";//File.pathSeparator;
        if (uris.length>2) {
            for (int i=2; i<uris.length; i++) {
                Address += uris[i]+"/";//File.pathSeparator;
            }
        }
        String message = Address+"<br>";

        File file = new File(Address);
        message += "[]: "+toLink(uri,"..")+"<br>";

        if (file.exists()) {
            if (file.isDirectory()) {
                String[] dir_files = file.list();
                if (dir_files.length > 0) {
                    for (int i=0; i<dir_files.length; i++) {
                        message += "["+i+"]: "+toLink(uri,dir_files[i])+"<br>";
                    }
                }
            } else {
                message += "is File";
            }
        } else {
            message += "this file don't exist";
        }

        return message;
    }

    private static String toLink(String uri, String link) {
        if (link.toLowerCase().equals("..")) {
            String[] uris = uri.split("/");
            if (uris.length>2) {
                String newURI = "/";
                for (int i=1; i<uris.length-1; i++) {
                    if (!uris[i].equals("")) {
                        newURI += uris[i]+"/";
                    }
                }
                return "<a href='" + newURI + "'>" + link + "</a>";
            } else {
                return link;
            }
        } else {
            return "<a href='" + uri + "/" + link + "'>" + link + "</a>";
        }
    }
}
