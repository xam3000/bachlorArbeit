package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MessageClient.OnMessageReceivedListener {

    private TextView textView;

    private final String VOICE_TRANSCRIPTION_MESSAGE_PATH = "/gyro_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        Wearable.getMessageClient(this).addListener(this);
    }

    private List<SensorData> sensorData;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(VOICE_TRANSCRIPTION_MESSAGE_PATH)) {
            byte[] data = messageEvent.getData();
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis);
                sensorData = (List<SensorData>) ois.readObject();
                ois.close();
                bis.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            textView.setText(sensorData.toString());
        }
    }


}