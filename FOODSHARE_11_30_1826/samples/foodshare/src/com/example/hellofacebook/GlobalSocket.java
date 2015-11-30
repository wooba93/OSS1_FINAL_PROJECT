package com.example.hellofacebook;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.provider.Telephony;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    static Thread readThread;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        msr = new ThreadFunction();
        readThread = new ReadClass();
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
    static String ip = "192.168.26.250";//"192.168.219.130";
    static Socket socket = null;
    static DataInputStream in;
    static DataOutputStream out;
    static byte[] byteBuffer;
    static int bBufferLength;
    class ThreadFunction extends Thread{ // 서버 thread
        @Override
        public void run() {
            try {
                //해당 아이피와 포트로 소켓 연결
                socket = new Socket(ip, port);
                in = new DataInputStream(GlobalSocket.socket.getInputStream());
                out = new DataOutputStream(GlobalSocket.socket.getOutputStream());
            }
            catch(Exception e)
            {
            }
        }
    }

    public class ReadClass extends  Thread{
        public void run()
        {
            bBufferLength = 0;
            byteBuffer = new byte[1000];
            while(true) {
                try {
                    bBufferLength = GlobalSocket.in.read(byteBuffer);
                } catch (IOException e) {
                }
            }
        }
    }
}

