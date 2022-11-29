package com.example.test2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test2.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {


    private ActivityMainBinding binding;

    private final String DATA_CAPABILITY = "gyro_data";
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        text = (TextView) findViewById(R.id.textView);

        Button buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener((View view) -> {
            text.setText("connecting...");
            ConnectThread connectThread = new ConnectThread();
            connectThread.start();

            //connectToPhone();
        });

        Button buttonSend = (Button) findViewById(R.id.button_send);
        buttonSend.setOnClickListener((View view) -> {
            String dataString = "test";
            byte[] data = dataString.getBytes();
            sendData(data);
        });

        Button buttonSensors = (Button) findViewById(R.id.button_sensors);
        buttonSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, SensorActivity.class);
        startActivity(switchActivityIntent);
    }


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
                text.setText("success");
            });
            sendTask.addOnFailureListener(runnable -> {
                text.setText("fail");
            });
        } else {
            text.setText("no node");
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
                text.setText(node.getId());
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
        }
    }
}


