package ftp27.apps.helmet;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class mainActivity extends Activity implements View.OnClickListener {
    private static final String LOG_TAG = "Class [main]";

    private TextView ServerButton;
    private TextView textIP, textPort, textPass, textConType;
    private ImageView statusCicrle;
    private Integer ServerStatus;

    private Timer interfaceUpdater;
    private static long updateTime = 5L * 1000;
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            updateDisplays();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerButton = (TextView) findViewById(R.id.server_control);

        textIP = (TextView) findViewById(R.id.textIP);
        textPort = (TextView) findViewById(R.id.textPort);
        textPass = (TextView) findViewById(R.id.textPasskey);
        textConType = (TextView) findViewById(R.id.textConType);
        statusCicrle = (ImageView) findViewById(R.id.imageMainCircle);

        interfaceUpdater = new Timer();
        interfaceUpdater.schedule(updateTask, 0L, updateTime);

        //Set port
        //startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_PORT, 8080));
        //startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_START));
        //startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_STOP));
        ServerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.server_control:
                Log.d(LOG_TAG, "OnClick!");
                if (ServerStatus == null || ServerStatus == serverService.SERVER_STOP) {
                    Log.d(LOG_TAG, "Start Server");
                    startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_START));
                } else {
                    startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_STOP));
                }
                updateDisplays();
                break;
        }
    }

    private void updateDisplays() {
        Intent intent = new Intent(this, serverService.class);
        startService(
                intent.putExtra(
                        serverService.PARAM_PINTENT,
                        createPendingResult(serverService.TASK_GETDATA, new Intent(this, serverService.class), 0)));
    }

    private String checkString(String Res) {
        return checkString(Res, getString(R.string.none));
    }

    private String checkString(String Res, String Default) {
        if (Res == null) {
            return Default;
        }
        return Res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == serverService.STATUS_OK) {
            String port = checkString(data.getStringExtra(serverService.PARAM_PORT));
            String ip = checkString(data.getStringExtra(serverService.PARAM_IP));
            String passkey = checkString(data.getStringExtra(serverService.PARAM_PASSKEY));
            String network = checkString(data.getStringExtra(serverService.PARAM_NETWORK),serverService.NETWORK_NONE);
            ServerStatus = data.getIntExtra(serverService.PARAM_STATUS, serverService.SERVER_STOP);

            if (!network.equals(serverService.NETWORK_NONE)) {
                // Yellow || Green
                if (ServerStatus == serverService.SERVER_START) {
                    // Green
                    statusCicrle.setImageResource(R.drawable.green_button);
                    textIP.setText(ip);
                    textPass.setText(passkey);
                } else {
                    // Yellow
                    statusCicrle.setImageResource(R.drawable.yellow_button);
                    textIP.setText(R.string.press);
                    textPass.setText(R.string.none);
                }
                textConType.setText(network);
                textPort.setText(port);
            } else {
                // Red
                statusCicrle.setImageResource(R.drawable.red_button);
                textConType.setText(R.string.not_avaible);
                textIP.setText(R.string.none);
                textPort.setText(R.string.none);
                textPass.setText(R.string.none);
            }

            if ((getScreenOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) && ServerStatus == serverService.SERVER_STOP) {
                textIP.setText(R.string.none);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        interfaceUpdater.cancel();
        interfaceUpdater.purge();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

        private int getScreenOrientation() {
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int orientation;
            // if the device's natural orientation is portrait:
            if ((rotation == Surface.ROTATION_0
                    || rotation == Surface.ROTATION_180) && height > width ||
                    (rotation == Surface.ROTATION_90
                            || rotation == Surface.ROTATION_270) && width > height) {
                switch(rotation) {
                    case Surface.ROTATION_0:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                    case Surface.ROTATION_90:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                    case Surface.ROTATION_180:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        break;
                    case Surface.ROTATION_270:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown screen orientation. Defaulting to " +
                                "portrait.");
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                }
            }
            // if the device's natural orientation is landscape or if the device
            // is square:
            else {
                switch(rotation) {
                    case Surface.ROTATION_0:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                    case Surface.ROTATION_90:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                    case Surface.ROTATION_180:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        break;
                    case Surface.ROTATION_270:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown screen orientation. Defaulting to " +
                                "landscape.");
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                }
            }

            return orientation;
        }



//    protected void onStart() {}
//    protected void onRestart() {}
//    protected void onPause() {}

}
