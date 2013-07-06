package com.ben.ber;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class SearchApiParser implements Runnable {
    private static ArrayList<Track> searchedTracks;
    private static boolean isRequestAvailable;
    private static Long pages;
    StringBuilder sb;
    private static Object lock = new Object();
    private static String query;

    public SearchApiParser() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        synchronized (lock) {
            parseIsRequestPossible(getDataFromApi("",1));
            while (true) {
                try {
                    lock.wait();
                    if (query != null){
                        searchPages(getDataFromApi(query, 1));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void doSearch(String search){
        synchronized (lock){
            query = search;
            lock.notifyAll();
        }
    }

    private String getDataFromApi(String query, int page) {
        try {
            String urlEncoderQuery = URLEncoder.encode(query, "UTF-8");
            URL data = new URL("http://www.r-a-d.io/search/api.php" + "?query=" +urlEncoderQuery + "&page=" + Integer.toString(page));
            data.openConnection();
            sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(data.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    private void searchPages(String JSON){
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(JSON);
            searchedTracks = new ArrayList<Track>();
            pages = (Long) jsonObject.get("pages");
            if (pages > 0){
                for (int i = 1; i <= pages; i++){
                    parseJson(getDataFromApi(query, i));
                }
            }
        }  catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String JSON) {
        JSONParser parser = new JSONParser();
        try {
            ArrayList<JSONArray> pagesArray = new ArrayList<JSONArray>();
            Object obj = parser.parse(JSON);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tracks = (JSONArray) jsonObject.get("result");
            Iterator<JSONArray> iterator = tracks.iterator();
            //list of a parsed tracks
            isRequestAvailable = (Boolean) jsonObject.get("status");
            while (iterator.hasNext()) {
                getTrackDetails(iterator.next());
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    SearchFrame.addListElements(searchedTracks);
                    SearchFrame.setIsRequestPossible(isRequestAvailable);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseIsRequestPossible(String JSON) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(JSON);
            JSONObject jsonObject = (JSONObject) obj;
            isRequestAvailable = (Boolean) jsonObject.get("status");
            SearchFrame.setIsRequestPossible(isRequestAvailable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTrackDetails(JSONArray details) {
        searchedTracks.add(new Track(details));
    }
}

class Track {
    public String artistName;
    public String trackName;
    public long lastPlayed;
    public long lastRequested;
    public int trackId;
    public boolean isRequestable;

    public Track(JSONArray list) {
        artistName = (String) list.get(0);
        trackName = (String) list.get(1);
        lastPlayed = (Long) list.get(2);
        if (!(list.get(3) instanceof String)){
            lastRequested = (Long) list.get(3);
        }

        trackId = Integer.parseInt((String) list.get(4));
        isRequestable = (Boolean) list.get(5);

    }
}
