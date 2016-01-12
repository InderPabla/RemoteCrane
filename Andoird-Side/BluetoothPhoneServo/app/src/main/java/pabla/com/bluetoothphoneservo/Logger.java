package pabla.com.bluetoothphoneservo;


import android.util.Log;

public class Logger {

    String tag = "Logger";

    public Logger(String tag) {
        this.tag = tag;
    }

    void e(String message){
        Log.e(tag,message);
    }

    void v(String message){
        Log.v(tag,message);
    }

    void d(String message){
        Log.d(tag,message);
    }
}
