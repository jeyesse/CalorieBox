package com.iot.caloriebox;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by 서울기술교육센터 on 2017-07-14.
 */

public class BluetoothService_M {
    // Debugging
    private static final String TAG = "BluetoothService";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private int _state;

    // 상태를 나타내는 상태 변수
    private static final int STATE_NONE = 0;        // 아무것도 안함
    private static final int STATE_LISTEN = 1;      // 들어오는 연결을 기다림
    private static final int STATE_CONNECTING = 2;  // 나가는 연결을 시작함
    private static final int STATE_CONNECTED = 3;   // 원격 장치에 연결됨

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ConnectThread _ConnectThread;
    private ConnectedThread _ConnectedThread;

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
            scanDevice();
        }else{
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");

            // 블루투스 활성화 요청하는 알림창
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            _activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    // 블루투스 디바이스 검색
    public void scanDevice(){
        Log.d(TAG, "Scan Deivce");

        Intent serverIntent = new Intent(_activity, DeviceListActivity.class);
        _activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    // 검색된 기기에 접속
    public void getDeviceInfo(Intent data){
        // 디바이스 MAC 주소를 받는다
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        // BluetoothDevice 객체 받기
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        Log.d(TAG, "Get Device Info \n" + "address : " + address);

        // 연결하기 위하여 전달
        connect(device);
    }

    // Bluetooth 상태 set
    private synchronized void setState(int state){
        Log.d(TAG, "setState() " + _state + " -> " + state);
        _state = state;
    }

    // Bluetooth 상태 get
    public synchronized int getState(){
        return _state;
    }

    public synchronized void start(){
        Log.d(TAG, "start");

        // 연결을 시도하는 모든 쓰레드를 취소
        if(_ConnectThread == null){

        }else{
            _ConnectThread.cancel();
            _ConnectThread = null;
        }

        // 현재 연결중인 모든 쓰레드를 취소
        if(_ConnectedThread == null){

        }else {
            _ConnectedThread.cancel();
            _ConnectedThread = null;
        }
    }

    // ConnectThread 초기화 device의 모든 연결 제거
    public synchronized void connect(BluetoothDevice device){
        Log.d(TAG, "connect to : " + device);

        // 연결을 시도하는 모든 쓰레드를 취소
        if(_state == STATE_CONNECTING){
            if(_ConnectThread == null){

            }else {
                _ConnectThread.cancel();
                _ConnectThread = null;
            }
        }

        // 현재 연결중인 모든 쓰레드 취소
        if(_ConnectedThread == null){

        }else {
            _ConnectedThread.cancel();
            _ConnectedThread = null;
        }

        // 주어진 장치와 연결하기위해 쓰레드를 시작한다.
        _ConnectThread = new ConnectThread(device);

        _ConnectThread.start();
        setState(STATE_CONNECTING);
    }

    // ConnectedThread 초기화
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected");

        // 연결을 완료한 쓰레드를 취소
        if(_ConnectThread == null){

        } else {
            _ConnectThread.cancel();
            _ConnectThread = null;
        }

        // 현재 연결된 모든 쓰레드를 취소
        if(_ConnectedThread == null){

        } else {
            _ConnectedThread.cancel();
            _ConnectedThread = null;
        }

        // 쓰레드를 시작하여 연결을 관리하고 전송한다.
        _ConnectedThread = new ConnectedThread(socket);
        _ConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    // 모든 쓰레드 정지
    public synchronized void stop(){
        Log.d(TAG, "stop");

        if(_ConnectThread != null){
            _ConnectThread.cancel();
            _ConnectThread = null;
        }

        if(_ConnectedThread != null){
            _ConnectedThread.cancel();
            _ConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    // 값을 쓰는 부분(보내는 부분)
    public void write(byte[] out){  //임시 객체 만들기
        ConnectedThread copySynchronize;  //ConnectedThread의 복사본 동기화
        synchronized (this){
            if(_state != STATE_CONNECTED)
                return;
            copySynchronize = _ConnectedThread;
        }
        // 동기화되지 않은 쓰기를 수행한다.
        copySynchronize.write(out);
    }

    // 연결 실패했을때
    private void connectionFailed(){
        setState(STATE_LISTEN);
    }

    // 연결을 잃었을 때
    private void connectionLost(){
        setState(STATE_LISTEN);
    }


    private class ConnectThread extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device){
            bluetoothDevice = device;
            BluetoothSocket socketTemp = null;

            // 디바이스 정보를 얻어서 BluetoothSocket 생성
            try{
                socketTemp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }catch(IOException e){
                Log.e(TAG, "create() failed", e);
            }
            bluetoothSocket = socketTemp;
        }

        public void run(){
            Log.i(TAG, "BEGIN _ConnectThread");
            setName("ConnectThread");

            // 연결을 시도하기 전에는 항상 기기 검색을 중지한다.
            // 기기 검색이 계속되면 연결속도가 느려지기 때문이다.
            bluetoothAdapter.cancelDiscovery();

            // BluetoothSocket 연결 시도
            try{
                // BluetoothSocket 연결 시도에 대한 return 값은 succes 또는 exception이다.
                bluetoothSocket.connect();
                Log.d(TAG, "Connect Success");
            }catch (IOException e){
                // 연결 실패시 불러오는 메서드
                connectionFailed();
                Log.d(TAG, "Connect Fail");

                // socket을 닫는다.
                try{
                    bluetoothSocket.close();
                }catch (IOException e2){
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }

                // 연결중, 혹은 연결 대기상태인 메소드를 호출한다.
                BluetoothService_M.this.start();
                return;
            }

            // ConnectThread 클래스를 reset한다.
            synchronized(BluetoothService_M.this){
                _ConnectThread = null;
            }

            // ConnectThread를 시작한다.
            connected(bluetoothSocket, bluetoothDevice);
        }

        public void cancel(){
            try{
                bluetoothSocket.close();
            }catch (IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "create ConnectedThread");
            bluetoothSocket = socket;
            InputStream inputTemp = null;
            OutputStream outputTemp = null;

            // BluetoothSocket의 inputStream과 outputStream을 얻는다.
            try{
                inputTemp = socket.getInputStream();
                outputTemp = socket.getOutputStream();
            }catch (IOException e){
                Log.e(TAG, "temp sockets not created", e);
            }

            inputStream = inputTemp;
            outputStream = outputTemp;
        }

        public void run(){
            Log.i(TAG, "BEGIN _ConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while(true){
                try {
                    // InputStream으로부터 값을 받아 읽는 부분(값을 받는다)
                    bytes = inputStream.read(buffer);
                }catch (IOException e){
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        // 연결된 OutStream에 쓴다.
        // param buffer는 bytes로 쓴다.
        public void write(byte[] buffer){
            try{
                // 값을 쓰는 부분(값을 보낸다)
                outputStream.write(buffer);
            }catch (IOException e){
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel(){
            try{
                bluetoothSocket.close();
            }catch (IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
