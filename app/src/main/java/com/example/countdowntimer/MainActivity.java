package com.example.countdowntimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    long time_ms;
    EditText editText;
    Button set;
    TextView textView;
    Button start;
    Button reset;
    CountDownTimer ctimer;
    boolean state;
    long timeleft=time_ms;
    long endtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);
        start=findViewById(R.id.start);
        editText=findViewById(R.id.editText);
        set=findViewById(R.id.set);
        reset=findViewById(R.id.reset);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input =editText.getText().toString();
                if(input.length()==0){
                    Toast.makeText(MainActivity.this,"invalid",Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisinput=Long.parseLong(input)*60000;
                if(millisinput==0){
                    Toast.makeText(MainActivity.this,"invalid number",Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisinput);
                editText.setText("");

            }
        });



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state){
                    pauseTimer();
                }
                else{
                    startTimer();
                }
            }

        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }


        });
        //updateText();

    }

    private void setTime(long millisinput) {
        time_ms=millisinput;
        resetTimer();
    }

    public void startTimer(){
        endtime=System.currentTimeMillis() + timeleft;
        ctimer= new CountDownTimer(timeleft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleft=millisUntilFinished;
                updateText();
                
            }

            @Override
            public void onFinish() {
                state=false;
                start.setText("Start");
                updateButtons();

            }
        }.start();
        state=true;
        updateButtons();
    }

    private void updateText() {
        int hours=(int)(timeleft/1000)/3600;
        int minutes=(int) ((timeleft/1000)%3600)/60;
        int seconds=(int) (timeleft/1000)%60;
        String samay;
        if(hours>0){
            samay=String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,seconds);
        }else{
            samay=String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        }


        textView.setText(samay);
    }

    public void pauseTimer(){

        ctimer.cancel();
        state=false;
        updateButtons();



    }

    private void resetTimer() {
        timeleft=time_ms;
        updateText();
        updateButtons();
        
    }

    private void updateButtons() {
        if (state) {
            editText.setVisibility(View.INVISIBLE);
            set.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
            start.setText("Pause");
        } else {
            editText.setVisibility(View.VISIBLE);
            set.setVisibility(View.VISIBLE);
            start.setText("Start");
            if (timeleft < 1000) {
                start.setVisibility(View.INVISIBLE);
            } else {
                start.setVisibility(View.VISIBLE);
            }
            if (timeleft < time_ms) {
                reset.setVisibility(View.VISIBLE);
            } else {
                reset.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putLong("StartTime",time_ms);
        editor.putLong("Millisleft",timeleft);
        editor.putBoolean("TimerRunning",state);
        editor.putLong("endTime", endtime);
        editor.apply();
        if(ctimer!=null){
            ctimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref=getSharedPreferences("pref",MODE_PRIVATE);
        timeleft=pref.getLong("Millisleft",time_ms);
        time_ms=pref.getLong("StartTime",0);
        state=pref.getBoolean("TimerRunning",false);
        updateText();
        updateButtons();
        if(state){
            endtime=pref.getLong("endtime",0);
            timeleft=endtime-System.currentTimeMillis();
            if(timeleft<0){
                timeleft=0;
                state=false;
                updateText();
                updateButtons();
            }else{
                startTimer();
            }
        }


    }
}