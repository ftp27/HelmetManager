package ftp27.apps.helmet.managers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class phone {
    private Context context;

    public phone(Context context) {
        this.context = context;
    }

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        String[] uris = uri.split("/");
        String action = "";
        String message = "";

        if (uris.length>2) {
            action = uris[2];
        }

        if (action.equals("header")) {
            message = getHeaderInfo();
        }

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, message);
    }

    public String getDeviceName() {
        return Build.DEVICE;
    }

    public String getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = 100*level / (float)scale;
        return Float.toString(batteryPct)+"%";
    }

    public String getHeaderInfo() {
        String message = "{";
            message += "\"phoneName\":\""+getDeviceName()+"\",";
            message += "\"batteryLevel\":\""+getBatteryLevel()+"\"";
        message += "}";
        return message;
    }
}
