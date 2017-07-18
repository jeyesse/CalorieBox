//package com.iot.caloriebox;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
///**
// * Created by 서울기술교육센터 on 2017-07-17.
// */
//
//public class PaceCountersService_M extends Service {
//
//    PaceCounters_M paceCounters;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        // Service 객체와 (화면단 Activity 사이에서)
//        // 통신(데이터를 주고받을) 때 사용하는 메서드
//        // 데이터를 전달할 필요가 없으면 return null;
//        return null;
//    }
//
//    @Override
//    public void onCreate(){
//        super.onCreate();
//        Log.d("test", "서비스의 onCreate");
//
//        paceCounters = PaceCounters_M.create(this, R.raw.chacha);
//        paceCounters.setLooping(false); // 반복재생
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId){
//        // 서비스가 호출될 때마다 실행
//        Log.d("test", "서비스의 onStartCommand");
//
//        paceCounters.start(); //시작
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        // 서비스가 종료될 때 실행
//
//        paceCounters.stop(); //종료
//
//        Log.d("test", "서비스의 onDestroy");
//    }
//}
