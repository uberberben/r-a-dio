package com.ben.ber;

import java.net.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ApiInfo implements Runnable {
    private String np, dj;
    private long start, end, cur;

    public ApiInfo() {
        Thread thread = new Thread(this);
        thread.start();
    }

    private void getDataFromApi() {
        try {
            URL data = new URL("http://www.r-a-d.io/api.php");
            data.openConnection();
            FileWriter out = new FileWriter("data");
            BufferedReader in = new BufferedReader(new InputStreamReader(data.openStream()));

            String line;

            while ((line = in.readLine()) != null) {
                out.write(line);
            }

            out.close();
            in.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJson() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("data"));
            JSONObject jsonObject = (JSONObject) obj;

            np = (String) jsonObject.get("np");
            start = (Long) jsonObject.get("start");
            end = (Long) jsonObject.get("end");
            cur = (Long) jsonObject.get("cur");
            dj = (String) jsonObject.get("dj");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                this.getDataFromApi();
                this.parseJson();
                long diff = end - cur;
                if (diff <= 0 ){
                    continue;
                }
                String data[] = this.getData();
                System.out.println(data[0]);
                GUI.changeNowPlaying(data[0]);



                    Thread.sleep((diff * 1000)+5000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public String[] getData() {
        return new String[]{np, dj};
    }
}
