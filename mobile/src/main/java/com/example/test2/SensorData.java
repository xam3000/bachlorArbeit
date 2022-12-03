package com.example.test2;

import android.hardware.SensorEvent;

import java.io.Serializable;
import java.util.Arrays;

public class SensorData implements  Serializable{


    private final long timestamp;
    private final float[] values;
    private static final long serialVersionUID = 2L;
    private final String sensorType;

    public long getTimestamp() {
        return timestamp;
    }

    public float[] getValues() {
            return values;
        }

    public String getSensorType() {
        return sensorType;
    }

    SensorData(SensorEvent sensorEvent) {
            this.timestamp = sensorEvent.timestamp;
            this.values = sensorEvent.values;
            this.sensorType = sensorEvent.sensor.getStringType();
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "timestamp=" + timestamp +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
