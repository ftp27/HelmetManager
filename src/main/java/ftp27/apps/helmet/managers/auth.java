package ftp27.apps.helmet.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import ftp27.apps.helmet.tools.dataBase;
import ftp27.apps.helmet.tools.logger;
import sun.rmi.runtime.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by ftp27 on 30.05.14.
 */
public class auth {
    private static String LOG_TAG = "Class [dataBase]";
    private static int authKeyLength = 4;

    private dataBase db;
    private logger Logger;
    private Context context;

    private String authKey;

    public auth(logger Logger, dataBase db) {
        this.Logger = Logger;
        this.db = db;
        this.context = db.getContext();
    }

    public void genAuthkey() {
        authKey = "";
        for (int i=0; i<authKeyLength; i++) {
            authKey += Integer.toHexString(
                    new Random().nextInt(16)
            );
        }
        Logger.statusMessage("AuthKey: "+authKey);
    }

    public int getAccessLevel(String IP) {
        return db.takeAccess(IP);
    }

    public long takeAccess(String IP, String AuthKey) {
        if (AuthKey.equals(this.authKey)) {
            int AccessLevel = 0;
            Date deathtime = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(deathtime);
            c.add(Calendar.DATE, 1);
            deathtime = c.getTime();
            long id = db.newAccess(IP, deathtime, AccessLevel);
            if (id > 0) {
                Logger.statusMessage("Access granted ["+AccessLevel+"] to "+IP);
            }
            return id;
        }

        return -2;
    }

    // Additional functional :)

    public logger getLogger() {
        return Logger;
    }

    public Context getContext() {
        return context;
    }
}
