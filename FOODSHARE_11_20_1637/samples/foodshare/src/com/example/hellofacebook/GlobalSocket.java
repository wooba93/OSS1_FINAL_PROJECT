package com.example.hellofacebook;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.provider.Telephony;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by JSR on 2015-11-13.
 */
public class GlobalSocket extends Application{

    private ServerSocket globalSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    static Thread msr;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        msr = new ThreadFunction();
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        if(globalSocket != null) {
            try {
                inputStream.close();
                outputStream.close();
                globalSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.gc();
        }
        super.onTerminate();
    }

    public ServerSocket getGlobalSocket() {
        return globalSocket;
    }

    /*public void setGlobalSocket(ServerSocket socket) {
        this.globalSocket = socket;
        setOutputStream(socket);
        setInputStream(socket);
    }

    public OutputStream getOutputStream() {
        setOutputStream(this.globalSocket);
        return outputStream;
    }

    public InputStream getInputStream() {
        setInputStream(this.globalSocket);
        return inputStream;
    }*/

    /*private void setOutputStream(ServerSocket socket) {
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setInputStream(ServerSocket socket) {
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/

    // 서버 정보
    static int port = 8080;
    static String ip = "192.168.22.11";//"192.168.219.130";
    static Socket socket = null;

    class ThreadFunction extends Thread{ // 서버 thread
        @Override
        public void run() {
            try {
                //해당 아이피와 포트로 소켓 연결
                socket = new Socket(ip, port);
            }
            catch(Exception e)
            {
            }
        }
    }
}

