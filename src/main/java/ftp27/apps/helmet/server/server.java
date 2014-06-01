package ftp27.apps.helmet.server;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import ftp27.apps.helmet.managers.auth;
import ftp27.apps.helmet.tools.logger;

import java.io.File;

/**
 * Created by ftp27 on 04.05.14.
 */
public class server {
    public static final int
            CODE_OK=1,
            CODE_STARTED=2,
            CODE_STOPPED=3;

    private WifiInfo wifiInfo;
    private httpd HTTPd;
    private logger Logger;
    private Context context;
    private auth AccessManager;

    private int port;
    private int status = CODE_STOPPED;

    public server(int port, WifiManager wifiMng, auth AccessManager) {
        this.port = port;
        this.AccessManager = AccessManager;
        this.Logger = AccessManager.getLogger();
        this.context = AccessManager.getContext();

        wifiInfo = wifiMng.getConnectionInfo();
    }

    public int start() {
        if (this.status() == CODE_STARTED) {
            this.stop();
        }

        try {
            HTTPd = new httpd(port, new File("/sdcard"), AccessManager);
            status = CODE_STARTED;

            Logger.serverMessage("Server started");
            Logger.serverMessage("IP:Port > "+getIP()+":"+Integer.toString(getPort()));
            AccessManager.genAuthkey();

        } catch (Exception e) {
            e.printStackTrace();
            status = CODE_STOPPED;
            Logger.errorMessage("Error with starting server");
        }

        return status;
    }

    public int stop() {
        if (!HTTPd.equals(null)) {
            try {
                HTTPd.stop();
                Logger.serverMessage("Server stepped");
            } catch (Exception e) {
                Logger.errorMessage("Error with stopping server");
            }
        }
        status = CODE_STOPPED;
        return status;
    }

    public int status() {
        return status;
    }

    public String getIP() {
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        return ipString;
    }

    public int getPort() {
        return port;
    }
}
