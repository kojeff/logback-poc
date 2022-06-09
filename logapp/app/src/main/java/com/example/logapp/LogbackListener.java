package com.example.logapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackListener {

    private AsyncTask<Void, Void, Void> async;
    private boolean active = true;
    int port = 4000;


    public void runUdpServer()
    {
        async = new AsyncTask<Void, Void, Void>()
        {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... params)
            {
                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                DatagramSocket ds = null;

                try
                {
                    ds = new DatagramSocket(port);
                    Logger log = LoggerFactory.getLogger(MainActivity.class);
                    log.info("starting UDP Listener");

                    while(active)
                    {
                        ds.receive(packet);
                        String s = new String(buffer, 0, packet.getLength());
                        JSONObject jObject = new JSONObject(s);
                        try {
                            String msg = jObject.getString("message");

                            JSONObject jObject2 = new JSONObject(msg);
                            String source = jObject2.getString("source");
                            if (source.equals("AppB") == false) {
                                log.info("got message: " + jObject2.getString("message"));
                            }
                        } catch(Exception e) {

                        }
                        Intent i = new Intent();

                        //i.setAction(Main.MESSAGE_RECEIVED);
                        //i.putExtra(Main.MESSAGE_STRING, new String(lMsg, 0, dp.getLength()));
                        //Main.MainContext.getApplicationContext().sendBroadcast(i);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }

                return null;
            }
        };

        async.execute();
    }

    public void stop_UDP_Server()
    {
        active = false;
    }
}
