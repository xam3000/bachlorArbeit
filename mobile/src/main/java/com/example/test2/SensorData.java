package com.example.test2;

import android.hardware.SensorEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorData implements  Serializable{


    private long timestamp;
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

    public String[] toStringArray(long firsttimestamp) {
        List<String> strings = new ArrayList<>();
        timestamp -= firsttimestamp;
        strings.add(String.valueOf(timestamp));
        for (float f : values) {
            strings.add(String.valueOf(f));
        }

        String[] strings1 = new String[strings.size()];
        strings1 = strings.toArray(strings1);
        return strings1;
    }
}
