package ftp27.apps.helmet.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ftp27 on 30.05.14.
 */
public class dataBase extends SQLiteOpenHelper {
    private static String LOG_TAG = "Class [dataBase]";
    private static String DataBaseName = "HelmetManagerDB";

    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public dataBase(Context context) {
        super(context, DataBaseName, null, 1);

        this.context = context;
    }

    public long newAccess(String IP, Date deathtime, int access){
        ContentValues values = new ContentValues();
            values.put("ip", IP);
            values.put("access", access);
            values.put("deathtime", dateFormat.format(deathtime));

        long id = this.getWritableDatabase().insert("tokens",null,values);

        return id;
    }

    public int takeAccess(String IP){
        Cursor c = this.getReadableDatabase().query(
               "tokens",
               new String[] {
                       "access"
               },
               "ip = ?",
               new String[] {
                       IP
               },
               null,
               null,
               null
       );
       int access = -1;
       if (c != null) {
           if (c.moveToFirst()) {
               String str;
               access = c.getInt(c.getColumnIndex("access"));
           }
           c.close();
       } else {
           Log.d(LOG_TAG, "Cursor is null");
       }
       return access;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tokens (" +
                "id INTEGER primary key autoincrement," +
                "ip TEXT," +
                "access INTEGER," +
                "deathtime DATETIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Context getContext() {
        return context;
    }
}
