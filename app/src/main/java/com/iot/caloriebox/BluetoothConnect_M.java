package com.iot.caloriebox;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BluetoothConnect_M extends AppCompatActivity {

    private BluetoothService_M bluetoothService = null;

    private final Handler _handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect__m);

        // BluetoothService 클래스 생성
        if(bluetoothService == null){
            bluetoothService = new BluetoothService_M(this, _handler);
        }
    }
}
