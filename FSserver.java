import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class FSserver {

	static int port = 8080;
	ServerSocket serverSocket = null;
	Socket socket = null;
	public void init() {
		try{
		serverSocket = new ServerSocket(port);
		System.out.println("Server is running...");
		while(true) {
			socket = serverSocket.accept();
			System.out.println(socket.getInetAddress() + ":" + socket.getPort());
			Thread msr = new ThreadFunction(socket);
			msr.start();
		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	class ThreadFunction extends Thread {
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		
		public ThreadFunction(Socket socket)
		{
			this.socket = socket;
			try{
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			}
			catch (Exception e){
				System.out.println("ThreadFunction initializing failed.: " + e);
			}
		}
		@Override
		public void run()
		{
			String name = "";
			while(true)
			{
			try{
				name = in.readUTF();
				if(name.equals("out"))
				{
					System.out.println("connection End");
					break;
				}
				out.writeUTF(name + "OK");
				System.out.println(socket + ": " + name);
			}
			catch(Exception e){
				System.out.println("Send & Receive Error: " + e);
			}
			}
			
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FSserver ms = new FSserver();
		ms.init();
	}

}
