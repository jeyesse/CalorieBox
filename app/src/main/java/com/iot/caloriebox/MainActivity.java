package com.iot.caloriebox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iot.caloriebox.bluetooth.BluetoothConnect_M;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, PaceCounters_M.class);
        Intent intent = new Intent(this, BluetoothConnect_M.class);
        startActivity(intent);
        this.finish();
    }
}
