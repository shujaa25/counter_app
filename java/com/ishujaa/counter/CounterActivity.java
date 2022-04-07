package com.ishujaa.counter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class CounterActivity extends AppCompatActivity {

    private Button buttonSetCount, buttonCountUp, buttonReset;
    private EditText editTextSetCount;
    private TextView textViewCount;
    private int countValue, targetValue;
    private boolean counterEnabled = false;

    private void setCounterValue(){
        textViewCount.setText(String.valueOf(countValue));
    }

    private void resetCounter(){
        countValue = 0;
        buttonSetCount.setEnabled(true);
        editTextSetCount.setEnabled(true);
        editTextSetCount.setText("");
        setCounterValue();

    }

    private void countUp(){
        if(counterEnabled){
            if(++countValue == targetValue){
                buttonCountUp.setEnabled(false);
                counterEnabled = false;
                vibrate(1000);
                try{
                    wait(1500);
                }catch (Exception e){
                    e.printStackTrace();
                }
                sendNotification("Count Reached.", String.valueOf(countValue));
            }
            setCounterValue();
            vibrate(100);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            if(action == KeyEvent.ACTION_DOWN){
                countUp();
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private void vibrate(int millis){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(millis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        editTextSetCount = findViewById(R.id.count_target_value);
        buttonSetCount = findViewById(R.id.btn_set_count);
        buttonSetCount.setOnClickListener(view -> {
            countValue = 0;
            String value = editTextSetCount.getText().toString();
            if(value.isEmpty()){
                Toast.makeText(this, "Target value cannot be empty.", Toast.LENGTH_LONG).show();
            }else{
                targetValue = Integer.parseInt(value);
                buttonReset.setEnabled(true);
                buttonCountUp.setEnabled(true);
                buttonSetCount.setEnabled(false);
                counterEnabled = true;
                editTextSetCount.setEnabled(false);
            }
        });

        buttonReset = findViewById(R.id.btn_reset);
        buttonReset.setOnClickListener(view -> {
            resetCounter();
        });

        textViewCount = findViewById(R.id.text_view_count);
        buttonCountUp = findViewById(R.id.btn_count);
        buttonCountUp.setOnClickListener(view -> {
            countUp();
        });
    }

    String CHANNEL_ID = "com.ishujaa.Counter";
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void sendNotification(String title, String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        builder.setAutoCancel(true);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

}