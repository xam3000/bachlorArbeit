package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.opencsv.CSVWriter;

import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

                file.mkdir();

                sensorData.forEach((s, sensorData1) -> {
                    try {
                        String[] header ={"timestamp", "x", "y", "z"};

                        CSVWriter writer = new CSVWriter(new FileWriter(new File(file, s + ".csv")));
                        writer.writeNext(header);
                        for (SensorData sensorData2 : sensorData1) {
                            writer.writeNext(sensorData2.toStringArray());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                ZipUtil.pack(file, new File(file.getPath() + ".zip"));

                deleteDirectory(file);

                textView.setText(file.getPath());

                ois.close();
                bis.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }

        }
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }


}