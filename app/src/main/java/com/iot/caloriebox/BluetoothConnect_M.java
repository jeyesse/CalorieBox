package com.iot.caloriebox;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothConnect_M extends Activity implements View.OnClickListener {

    // Debugging
    private static final String TAG = "BluetoothConnect";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout
    private Button button_Connect;
    private TextView textView_Result;

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
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_bluetooth_connect__m);

        button_Connect = (Button) findViewById(R.id.button_connect);
        textView_Result = (TextView) findViewById(R.id.textView_result);

        button_Connect.setOnClickListener(this);

        // BluetoothService 클래스 생성
        if(bluetoothService == null){
            bluetoothService = new BluetoothService_M(this, _handler);
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

    // 블루투스 알림창 확인/취소
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult " + resultCode);

        switch(requestCode){
            case REQUEST_CONNECT_DEVICE :
                if(resultCode == Activity.RESULT_OK){
                    bluetoothService.getDeviceInfo(data);
                }
                break;


            case REQUEST_ENABLE_BT :
                // When the request to enable Bluetooth returns
                if(resultCode == Activity.RESULT_OK){
                    // 확인 눌렀을 때
                    // Next
                    bluetoothService.scanDevice();
                }else{
                    // 취소 눌렀을 때
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }
}
