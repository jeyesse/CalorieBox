package com.iot.caloriebox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepCounter_M extends Activity
{

    Button buttonStep;
    Button buttonStop;

    Intent intentMyService;

    BroadcastReceiver receiver;

    boolean flag = true;

    Toast toast;

    TextView textViewStep;
    TextView textViewConsumeCalories;

    String serviceData;
    String consumeCalories;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter__m);

        intentMyService = new Intent(this, StepCounterService_M.class);

        receiver = new MyMainLocalRecever();

        textViewStep = (TextView)findViewById(R.id.textViewStep);
        textViewConsumeCalories = (TextView)findViewById(R.id.textViewConsumeCalories);

        buttonStep = (Button)findViewById(R.id.buttonStep);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        buttonStep.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(flag){
//                    btnStopService.setText("Stop !!");

                    try{
                        IntentFilter mainFilter = new IntentFilter("com.androday.test.step");

                        registerReceiver(receiver, mainFilter);

                        startService(intentMyService);

//                        Toast.makeText(getApplicationContext(), "Stop", 1).show();
                    } catch (Exception e){
//                        Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
                    }
                } else {
//                    btnStopService.setText("GO!!");

                    try{
                        unregisterReceiver(receiver);

                        stopService(intentMyService);

//                        Toast.makeText(getApplicationContext(), "GO", 1).show();
                    } catch (Exception e){
//                        Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
                    }
                }
                flag = !flag;
            }
        });
    }

    class MyMainLocalRecever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            double temp;
            String temp2;

            // 1보당 0.033 kcal
            serviceData = intent.getStringExtra("serviceData");
            temp = Float.parseFloat(serviceData)*0.033;
            temp2 = String.format("%.2f",temp);
//            consumeCalories = intent.getStringExtra(temp2);

            textViewStep.setText(serviceData);
            textViewConsumeCalories.setText(temp2);

//            Toast.makeText(getApplicationContext(), "Walking . . .", 1).show();

        }
    }
}
