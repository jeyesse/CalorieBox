package com.iot.caloriebox;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

/**
 * Created by 서울기술교육센터 on 2017-07-14.
 */

public class BluetoothService_M {
    //Debugging
    private static final String TAG = "BluetoothService";

    private BluetoothAdapter bluetoothAdapter;

    private Activity _activity;
    private Handler _handler;

    //Constructors
    public BluetoothService_M(Activity activity, Handler handler){
        _activity = activity;
        _handler = handler;

        // BluetoothAdapter 얻기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
}
