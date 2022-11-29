package com.example.test2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.test2.databinding.ActivityMainBinding;
import com.example.test2.databinding.ActivitySensorBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SensorActivity extends Activity implements SensorEventListener {

    private Button sensorButton;
    private Button sendButton;
    private TextView textView;

    private ActivitySensorBinding binding;

    private SensorManager sensorManager;
    private Sensor sensor;

    private boolean collecting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySensorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorButton = (Button) findViewById(R.id.button_sensor);
        sendButton = (Button) findViewById(R.id.button_send_sensor);
        textView = (TextView) findViewById(R.id.textView_sensor);

        sensorButton.setOnClickListener((View view) -> {
            collectSensorData();
        });

        sendButton.setOnClickListener((View view) ->{
            sendData();
        });

    }

    private void sendData() {
        new ConnectThread().start();
    }

    private void collectSensorData() {

        if (collecting) {
            sensorButton.setText("Start collecting");
            sensorManager.unregisterListener(this);
        } else {
            sensorEvents = new ArrayList<>();
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

            sensorButton.setText("Stop collecting");
        }
        collecting = !collecting;
    }

    private List<SensorData> sensorEvents;
    private ReadWriteLock sensorEventLock = new ReentrantReadWriteLock();

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensorEventLock.writeLock().lock();
        sensorEvents.add(new SensorData(sensorEvent));
        sensorEventLock.writeLock().unlock();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private final String DATA_CAPABILITY = "gyro_data";

    private String transcriptionNodeId = null;


    public static final String VOICE_TRANSCRIPTION_MESSAGE_PATH = "/gyro_data";

    private void sendData(byte[] data) {
        if (transcriptionNodeId != null) {
            Task<Integer> sendTask =

                    Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            transcriptionNodeId, VOICE_TRANSCRIPTION_MESSAGE_PATH, data);
            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            sendTask.addOnSuccessListener(runnable -> {
                textView.setText("success");
            });
            sendTask.addOnFailureListener(runnable -> {
                textView.setText("fail");
            });
        } else {
            textView.setText("no node");
        }
    }

    private void connectToPhone() {
        CapabilityInfo capabilityInfo = null;
        try {
            capabilityInfo = Tasks.await(
                    Wearable.getCapabilityClient(getApplicationContext()).getCapability(
                            DATA_CAPABILITY, CapabilityClient.FILTER_REACHABLE));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // capabilityInfo has the reachable nodes with the transcription capability
        updateTranscriptionCapability(capabilityInfo);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                textView.setText(node.getId());
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        transcriptionNodeId = pickBestNodeId(connectedNodes);
    }

    private class ConnectThread extends Thread {
        public void run() {
            connectToPhone();
            byte[] data = null;
            sensorEventLock.readLock().lock();
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                 oos.writeObject(sensorEvents);
                 data = bos.toByteArray();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                sensorEventLock.readLock().unlock();

            }
            if (data != null) {
                sendData(data);
            }
        }

    }

}
