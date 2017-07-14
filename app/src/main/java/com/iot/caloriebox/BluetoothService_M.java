package com.iot.caloriebox;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by 서울기술교육센터 on 2017-07-14.
 */

public class BluetoothService_M {
    // Debugging
    private static final String TAG = "BluetoothService";

    private BluetoothAdapter bluetoothAdapter;

    private Activity _activity;
    private Handler _handler;

    // Constructors
    public BluetoothService_M(Activity activity, Handler handler){
        _activity = activity;
        _handler = handler;

        // BluetoothAdapter 얻기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // 기기의 블루투스 지원여부 확인
    public boolean getDeviceState(){
        Log.d(TAG, "Check the Bluetooth support");

        if(bluetoothAdapter == null){
            Log.d(TAG, "Bluetooth is not available");

            return false;
        }else{
            Log.d(TAG, "Bluetooth is available");

            return true;
        }
    }

    // getDeviceState()가 true를 반환할 경우 블루투스 활성화
    public void enableBluetooth(){
        Log.i(TAG, "Check the enabled Bluetooth");

        if(bluetoothAdapter.isEnabled()){
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");

            // Next Step
        }else{
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");

            // 블루투스 활성화 요청하는 알림창
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            _activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }
}
