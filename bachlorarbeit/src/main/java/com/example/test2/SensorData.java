package com.example.test2;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SensorData implements  Serializable {


    private  long timestamp;
    private  float[] values;
    private static final long serialVersionUID = 2L;
    private  String sensorType;

    public long getTimestamp() {
        return timestamp;
    }

    public float[] getValues() {
        return values;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String[] toStringArray() {
        List<String> strings = new ArrayList<>();
        strings.add(String.valueOf(timestamp));
        for (float f : values) {
            strings.add(String.valueOf(f));
        }

        String[] strings1 = new String[strings.size()];
        strings1 = strings.toArray(strings1);
        return strings1;
    }
}