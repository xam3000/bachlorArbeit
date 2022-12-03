package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    private Map<String, List<SensorData>> sensorData;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(VOICE_TRANSCRIPTION_MESSAGE_PATH)) {
            byte[] data = messageEvent.getData();
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis);
                sensorData = (Map<String, List<SensorData>>) ois.readObject();

                File file = new File(this.getFilesDir(), LocalDateTime.now().toString());

                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(sensorData);


                textView.setText(file.getPath());

                ois.close();
                bis.close();
                oos.close();
                fos.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }

        }
    }


}