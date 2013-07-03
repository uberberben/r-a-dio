package com.ben.ber;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

public class AudioPlayer implements Runnable {
    Thread thread = null;
    SourceDataLine line;
    boolean paused = false;
    boolean stopped = false;
    Object lock = new Object();

    public static AudioPlayer player;

    public static AudioPlayer getPlayerObject() {
        if (player == null) {
            player = new AudioPlayer();
        }
        return player;

    }

    //  public AudioPlayer() {
    //    thread = new Thread(this);
    //  thread.start();
    // }

    public void setPause() {
        paused = true;
        stopped = false;
    }

    public void setPlay() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        synchronized (lock) {
            stopped = false;
            paused = false;
            lock.notifyAll();
        }

    }

    public void setStop() {

        thread = null;
        synchronized (lock) {
            stopped = true;
            paused = false;
            lock.notifyAll();
        }
    }


    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        AudioInputStream din = null;
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(new URL("http://r-a-d.io/lb/load-balance.php"));
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                    baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
                    false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            if (line != null) {
                line.open(decodedFormat);
                byte[] data = new byte[4096];
                // Start
                line.start();

                int nBytesRead;
                synchronized (lock) {

                    while ((nBytesRead = din.read(data, 0, data.length)) != -1 && (thread == thisThread)) {

                        while (paused) {
                            if (line.isRunning()) {
                                line.stop();
                            }
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {

                            }
                        }


                        if (!line.isRunning() && stopped == false) {
                            line.start();
                        }


                        line.write(data, 0, nBytesRead);
                    }
                }
                // Stop
                line.drain();
                line.stop();
                line.close();
                din.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (din != null) {
                try {
                    din.close();
                } catch (IOException e) {
                }
            }
        }
    }
}


