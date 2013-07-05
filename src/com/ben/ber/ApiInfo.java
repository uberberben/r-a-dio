package com.ben.ber;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class ApiInfo implements Runnable {
    private String np, dj;
    private long start, end, cur;
    private int now = -1;
    Timer progressTimer;

    // constructor starting the api reader thread
    public ApiInfo() {
        Thread thread = new Thread(this);
        thread.start();

        progressTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.setProgress(now, (int) (end - start));
                now++;
            }
        });
        progressTimer.start();
    }

    // api data reader
    private void getDataFromApi() {
        try {
            URL data = new URL("http://www.r-a-d.io/api.php");
            data.openConnection();
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(data.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            parseJson(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // api data parsing
    private void parseJson(String JSON) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(JSON);
            JSONObject jsonObject = (JSONObject) obj;
            np = (String) jsonObject.get("np");
            System.out.println(np);
            start = (Long) jsonObject.get("start");
            end = (Long) jsonObject.get("end");
            cur = (Long) jsonObject.get("cur");
            dj = (String) jsonObject.get("dj");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // api data refreshing after each song
    @Override
    public void run() {
        while (true) {
            try {
                this.getDataFromApi();
                String data[] = this.getData();
                GUI.setNowPlaying(data[0]);
                now = (int) (cur - start);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    // getter for now playing and dj info
    public String[] getData() {
        return new String[]{np, dj};
    }
}
