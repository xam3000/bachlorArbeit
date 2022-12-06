package com.example.test2;

import com.example.test2.SensorData;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.List;
import java.util.Map;

public class Main {

    static Map<String, List<SensorData>> sensorData;
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("D:\\Documents\\00Uni\\bachlor_arbeit\\test2\\bachlorarbeit\\jump");
            ObjectInputStream ois = new ObjectInputStream(fis);
            sensorData = (Map<String, List<SensorData>>) ois.readObject();
            ois.close();
            fis.close();

            sensorData.forEach((s, sensorData1) -> {
                try {
                    String[] header ={"timestamp", "x", "y", "z"};

                    CSVWriter writer = new CSVWriter(new FileWriter("D:\\Documents\\00Uni\\bachlor_arbeit\\test2\\bachlorarbeit\\jumpdata\\"+s + ".csv"));
                    writer.writeNext(header);
                    for (SensorData sensorData2 : sensorData1) {
                        writer.writeNext(sensorData2.toStringArray());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}