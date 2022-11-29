package com.example.test2;

import android.hardware.SensorEvent;

import java.io.Serializable;

public class SensorData implements  Serializable {


    private final long timestamp;
    private final float[] values;
    private static final long serialVersionUID = 1L;

    public long getTimestamp() {
        return timestamp;
    }

    public float[] getValues() {
        return values;
    }

    SensorData(SensorEvent sensorEvent) {
        this.timestamp = sensorEvent.timestamp;
        this.values = sensorEvent.values;
    }
}