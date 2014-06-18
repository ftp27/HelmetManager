package ftp27.apps.helmet;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import ftp27.apps.helmet.managers.auth;
import ftp27.apps.helmet.server.httpd;
import ftp27.apps.helmet.tools.dataBase;
import ftp27.apps.helmet.tools.logger;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ftp27 on 04.05.14.
 */
public class serverService  extends Service {
    private static final String LOG_TAG = "Class [server]";

    public static final String PARAM_PORT =  "port";
    public static final String PARAM_IP = "IP";
    public static final String PARAM_PASSKEY =  "authkey";
    public static final String PARAM_NETWORK =  "network";
    public static final String PARAM_STATUS =  "status";
    public static final String PARAM_ACTION =  "action";
    public static final String PARAM_PINTENT = "pending";
    public static final String NETWORK_NONE = "none";
    public static final int ACTION_START    = 100;
    public static final int ACTION_STOP     = 101;
    public static final int TASK_GETDATA    = 200;
    public static final int STATUS_OK       = 300;
    public static final int STATUS_ERROR    = 301;
    public static final int SERVER_START    = 500;
    public static final int SERVER_STOP    = 505;


    public static enum StatusCode {CODE_STARTED, CODE_STOPPED};

    private ConnectivityManager ConnectMng;
    private WifiManager wifiManager;
    private httpd HTTPd;
    private logger Logger;
    private auth AccessManager;

    private int port;
    private StatusCode status = StatusCode.CODE_STOPPED;

    public void onCreate() {
        super.onCreate();

        port = 8080;

        Logger = new logger();
        AccessManager =  new auth(Logger, new dataBase(this));

        ConnectMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        Integer port = intent.getIntExtra(PARAM_PORT, 0);
        if (port != 0) {
            this.port = port;
        }

        Integer action = intent.getIntExtra(PARAM_ACTION, 0);
        if (action != 0) {
            if (action.equals(ACTION_START)) {
                start();
            } else if (action.equals(ACTION_STOP)) {
                stop();
            }
        }

        PendingIntent pi = intent.getParcelableExtra(PARAM_PINTENT);
        if (pi != null) {
            Intent toActivity = new Intent();
            int resultCode = STATUS_OK;
            try {
                toActivity.putExtra(PARAM_IP, getIP());
                toActivity.putExtra(PARAM_PORT, getPort().toString());
                toActivity.putExtra(PARAM_NETWORK, getUsedNetwork());
                toActivity.putExtra(PARAM_PASSKEY, AccessManager.getAuthKey());
                toActivity.putExtra(PARAM_STATUS, getStatus());
            } catch (Exception e) {
                resultCode = STATUS_ERROR;
            }

            try {
                pi.send(serverService.this, resultCode, toActivity);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public StatusCode start() {
        String UsedNetwork = getUsedNetwork();
        if (UsedNetwork.equals("")) {
            status = StatusCode.CODE_STOPPED;
            Logger.errorMessage("Error with starting server");
        } else {
            if (this.status() == StatusCode.CODE_STARTED) {
                this.stop();
            }

            try {
                HTTPd = new httpd(port, new File("/sdcard"), AccessManager);
                status = StatusCode.CODE_STARTED;

                Logger.serverMessage("Server started");
                Logger.serverMessage("Used network: " + getUsedNetwork());
                Logger.serverMessage("IP:Port > " + getIP() + ":" + Integer.toString(getPort()));
                AccessManager.genAuthkey();

            } catch (Exception e) {
                e.printStackTrace();
                status = StatusCode.CODE_STOPPED;
                Logger.errorMessage("Error with starting server");
            }
        }

        return status;
    }

    public StatusCode stop() {
        if (!HTTPd.equals(null)) {
            try {
                HTTPd.stop();
                Logger.serverMessage("Server stepped");
            } catch (Exception e) {
                Logger.errorMessage("Error with stopping server");
            }
        }
        status = StatusCode.CODE_STOPPED;
        return status;
    }

    public StatusCode status() {
        return status;
    }

    public String getIP() {
        if (getUsedNetwork().equals("WiFi")) {
            int ip = wifiManager.getConnectionInfo().getIpAddress();
            String ipString = String.format(
                    "%d.%d.%d.%d",
                    (ip & 0xff),
                    (ip >> 8 & 0xff),
                    (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));
            return ipString;
        } else {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            Log.d(LOG_TAG, "getIP - "+inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                // Log.e(Constants.LOG_TAG, e.getMessage(), e);
            }
        }
        return "----";
    }


    public String getUsedNetwork() {
        NetworkInfo nf = ConnectMng.getActiveNetworkInfo();
        String TypeNetwork = "";
            switch (nf.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    TypeNetwork = "Mobile";
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    TypeNetwork = "WiFi";
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    TypeNetwork = "Ethernet";
                    break;
                default:
                    TypeNetwork = NETWORK_NONE;
                    break;
            }
        Log.d(LOG_TAG, "getUsedNetwork - "+TypeNetwork);
        return TypeNetwork;
    }

    public Integer getPort() {
        return port;
    }

    public int getStatus() {
        if (status() == StatusCode.CODE_STARTED) {
            return SERVER_START;
        } else {
            return SERVER_STOP;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
