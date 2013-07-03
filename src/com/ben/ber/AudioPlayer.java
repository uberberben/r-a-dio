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

    // Singleton getter

    public static AudioPlayer getPlayerObject() {
        if (player == null) {
            player = new AudioPlayer();
        }
        return player;

    }

   // Pause player
    public void setPause() {
        paused = true;
        stopped = false;
    }

    // Start playing with thread initialising when not available
    public void setPlay() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        // Lock waking
        synchronized (lock) {
            stopped = false;
            paused = false;
            lock.notifyAll();
        }

    }

    // Stop playback by deleting the thread
    public void setStop() {
        thread = null;
        // Lock waking
        synchronized (lock) {
            stopped = true;
            paused = false;
            lock.notifyAll();
        }
    }


    @Override
    public void run() {

        // Current thread needed later to stop playing
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

                    /*
                     *  Do not continue playing when thread is deleted (stop button clicked)
                     */
                    while ((nBytesRead = din.read(data, 0, data.length)) != -1 && (thread == thisThread)) {

                        while (paused) {
                            if (line.isRunning()) {
                                line.stop();
                            }
                            try {
                                // Pause thread and wait for wake by Stop or Play
                                lock.wait();
                            } catch (InterruptedException e) {

                            }
                        }

                        // Start playing when waking up from pause
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


