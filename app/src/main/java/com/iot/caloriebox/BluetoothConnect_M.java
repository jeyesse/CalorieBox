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
import android.widget.Toast;

public class BluetoothConnect_M extends Activity implements View.OnClickListener {

    // 위치 태그
    private static final String TAG = "BluetoothConnect";

    // Intent 요청 코드
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout
    private Button button_Connect;
    private Button btn1;
    private Button btn2;
    private TextView textWeight;

    private BluetoothService_M bluetoothService = null;

    int cal;


    private static final boolean D = true;

    // 보내기 모드
    public static final int MODE_REQUEST = 1;

    // 버튼 상태
    private int _selectedButton;

    // synchronized flags
    private static final int STATE_SENDING = 1;
    private static final int STATE_NO_SENDING = 2;
    private int _sendingState;

    // 메세지
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_READ = 3;

    private StringBuffer _outStringBuffer;

    //    private final Handler _handler = new Handler(){
//        @Override
//        public void handleMessage(Message message){
//            super.handleMessage(message);
//
//            // 메세지에 따라 각각의 경우를 분기한다.
//            switch (message.what){
//                case MESSAGE_STATE_CHANGE :
//                    if(D)
//                        Log.i(TAG, "MESSAGE_STATE_CHANGE : " + message.arg1); // 메세지 가공공
//
//                    switch (message.arg1){
//                        // 블루투스 연결이 되었을 경우
//                        case BluetoothService_M.STATE_CONNECTED :
//                            Toast.makeText(getApplicationContext(), "불루투스 연결에 성공하였습니다." , Toast.LENGTH_SHORT).show();
//                            break;
//
//                        // 블루투스 연결이 실패했을 경우
//                        case BluetoothService_M.STATE_FAIL :
//                            Toast.makeText(getApplicationContext(), "불루투스 연결에 실패하였습니다." , Toast.LENGTH_SHORT).show();
//                            break;
//
//                    }//message_switch
//                case MESSAGE_WRITE :
//                    String writeMessage = null;
//
//                    if(_selectedButton == 1){
//                        writeMessage = btn1.getText().toString();
//                        _selectedButton = -1;
//                    } else if (_selectedButton == 2){
//                        writeMessage = btn2.getText().toString();
//                        _selectedButton = -1;
//                    } else {                            // _selectedButton = -1 : 선택되지 않음
//                        byte[] writeBuffer = (byte[])message.obj;
//
//                        // 버퍼로부터 문자열을 구성한다.
//                        writeMessage = new String(writeBuffer);
//                    }
//                    break;
//            }
//        }
//    };
    private final Handler _handler = new Handler() {
        //핸들러의 기능을 수행할 클래스(handleMessage)
        public void handleMessage(Message msg) {
            //BluetoothService로부터 메시지(msg)를 받는다.
            super.handleMessage(msg);

            switch (msg.what) {

                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE : " + msg.arg1);

                    switch (msg.arg1) {

                        case BluetoothService_M.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "블루투스 연결에 성공하였습니다!", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothService_M.STATE_FAIL:
                            Toast.makeText(getApplicationContext(), "블루투스 연결에 실패하였습니다!", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;

                case MESSAGE_READ:
                    String readMessage = null;

                    Toast.makeText(getApplicationContext(), "음식의 무게 정보를 받았습니다.", Toast.LENGTH_SHORT).show();

                    byte[] readBuf = (byte[])msg.obj;

                    readMessage = new String(readBuf);

                    textWeight.setText(readMessage);

                    cal = Integer.parseInt(readMessage);


                    break;

                case MESSAGE_WRITE:
                    String writeMessage = null;

                    if (_selectedButton == 1) {

                        // writeMessage = mbtn_response.getText().toString() ;
                        _selectedButton = -1;
                    } /*else if ( mSelectedBtn == 2 ) {
                        writeMessage = mbtn2.getText().toString() ;
                        mSelectedBtn = -1 ;
                    } */ else { // mSelectedBtn = -1 : not selected

                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        writeMessage = new String(writeBuf);
                    }

                    break;
            }
        }
    };

//    private final Handler _handler = new Handler(){
//        @Override
//        public void handleMessage(Message message){
//            super.handleMessage(message);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_bluetooth_connect__m);

        // button state : 상태를 -1로 초기화
        _selectedButton = -1;

        button_Connect = (Button) findViewById(R.id.button_connect);
        button_Connect.setOnClickListener(_clickListener);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(_clickListener);
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(_clickListener);
        textWeight = (TextView) findViewById(R.id.textWeight);

        // BluetoothService 클래스 생성
        if (bluetoothService == null) {
            bluetoothService = new BluetoothService_M(this, _handler);
            _outStringBuffer = new StringBuffer("");
        }
    }

    // 블루투스 알림창 확인/취소
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothService.getDeviceInfo(data);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // 확인 눌렀을 때
                    bluetoothService.scanDevice();
                } else {
                    // 취소 눌렀을 때
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }

    // 확인되었을때 블루투스 활성화
    @Override
    public void onClick(View v) {
        if (bluetoothService.getDeviceState()) {
            // 블루투스가 지원 가능한 기기일 때
            bluetoothService.enableBluetooth();
        } else {
            finish();
        }
    }

    // 블루투스 연결하기 버튼을 눌렀을 때 블루투스 활성화를 위한 리스너
    private View.OnClickListener _clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String value;

            //분기.
            switch (v.getId()) {

                case R.id.button_connect:  //모든 블루투스의 활성화는 블루투스 서비스 객체를 통해 접근한다.

                    if (bluetoothService.getDeviceState()) { // 블루투스 기기의 지원여부가 true 일때
                        bluetoothService.enableBluetooth();  //블루투스 활성화 시작.
                    } else {
                        finish();
                    }
                    break;

                case R.id.btn1:

                    value = String.valueOf(cal*109/100);

                    if (bluetoothService.getState() == BluetoothService_M.STATE_CONNECTED) { //연결된 상태에서만 값을 보낸다.
                        sendMessage(value, MODE_REQUEST);
                        _selectedButton = 1;
                    } else {
                        Toast.makeText(getApplicationContext(), "블루투스 연결을 먼저 해 주세요!! ", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.btn2:

                    value = String.valueOf(cal*11/100);

                    if (bluetoothService.getState() == BluetoothService_M.STATE_CONNECTED) {
                        sendMessage(value, MODE_REQUEST);
                        _selectedButton = 2;
                    } else {
                        Toast.makeText(getApplicationContext(), "블루투스 연결을 먼저 해 주세요!! ", Toast.LENGTH_SHORT).show();
                    }

                    break;

                default:
                    break;
            }//switch
        }
    };


    /*메시지를 보낼 메소드 정의*/
    private synchronized void sendMessage(String message, int mode) {

        if (_sendingState == STATE_SENDING) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        _sendingState = STATE_SENDING;

        // Check that we're actually connected before trying anything
        if (bluetoothService.getState() != BluetoothService_M.STATE_CONNECTED) {
            _sendingState = STATE_NO_SENDING;
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothService.write(send, mode);

            // Reset out string buffer to zero and clear the edit text field
            _outStringBuffer.setLength(0);

        }

        _sendingState = STATE_NO_SENDING;
        notify();
    }
}
