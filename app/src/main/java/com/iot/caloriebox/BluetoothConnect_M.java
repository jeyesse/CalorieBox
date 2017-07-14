package com.iot.caloriebox;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    // 블루투스 알림창 확인/취소
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_ENABLE_BT :
                // When the request to enable Bluetooth returns
                if(resultCode == Activity.RESULT_OK){
                    // 확인 눌렀을 때
                    // Next
                }else{
                    // 취소 눌렀을 때
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }

    @Override
    public void onClick(View v){
        if(bluetoothService.getDeviceState()){
            // 블루투스가 지원 가능한 기기일 때
            bluetoothService.enableBluetooth();
        }else {
            finish();
        }
    }
}
