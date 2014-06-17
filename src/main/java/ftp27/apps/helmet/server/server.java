package ftp27.apps.helmet.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import ftp27.apps.helmet.managers.auth;
import ftp27.apps.helmet.tools.logger;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ftp27 on 04.05.14.
 */
public class server {
    private static final String LOG_TAG = "Class [server]";
    public static enum StatusCode {CODE_STARTED, CODE_STOPPED};

    private ConnectivityManager ConnectMng;
    private WifiManager wifiManager;
    private httpd HTTPd;
    private logger Logger;
    private Context context;
    private auth AccessManager;

    private int port;
    private StatusCode status = StatusCode.CODE_STOPPED;

    public server(int port, auth AccessManager) {
        this.port = port;
        this.AccessManager = AccessManager;
        this.Logger = AccessManager.getLogger();
        this.context = AccessManager.getContext();

        ConnectMng = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        Log.d(LOG_TAG, "Connection is "+new Boolean(checkConnection()).toString());


        /*
        NetworkInfo[] networks = ConnectMng.getAllNetworkInfo();
        String TypeNetwork;
        for (NetworkInfo network: networks) {
            TypeNetwork = "";
            switch (network.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    TypeNetwork = " - TYPE_MOBILE";
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    TypeNetwork = " - TYPE_WIFI";
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    TypeNetwork = " - TYPE_ETHERNET";
                    break;
            }
            Log.d(LOG_TAG, network.getTypeName()+TypeNetwork);
        }
        */
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
        return "";
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
            }
        Log.d(LOG_TAG, "getUsedNetwork - "+TypeNetwork);
        return TypeNetwork;
    }

    public int getPort() {
        return port;
    }

    public boolean checkConnection() {
        if (    ConnectMng.getActiveNetworkInfo() != null &&
                ConnectMng.getActiveNetworkInfo().isAvailable() &&
                ConnectMng.getActiveNetworkInfo().isConnected()) {
            return true;

        } else {
            return false;
        }
    }
}
