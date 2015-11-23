import java.net.Socket;
import java.util.Scanner;
import java.io.*;
import java.awt.image.*;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class clientTest {

	static int port = 8080;
	static String ip = "127.0.0.1";//"10.30.115.215";
	static Socket socket = null;
	
	public static void main(String[] args) throws IOException {
		
		DataInputStream in;
		DataOutputStream out;
		Scanner s = new Scanner(System.in);
		byte[] buffer = new byte[1024];
		
		try {
				//해당 아이피와 포트로 소켓 연결
			socket = new Socket(ip, port);
			System.out.println("Connecting success.");
			
			while(true){			
			String message = "";
			//사용자가 입력한 문자열을 받음
			message = s.nextLine();
			//해당 소켓에서 들어오는 버퍼
			in = new DataInputStream(socket.getInputStream());
			//해당 소켓에 출력하는 버퍼
			out = new DataOutputStream(socket.getOutputStream());
			//받은 문자열을 서버에 전송
			out.write(message.getBytes());
			out.flush();
			in.read(buffer);
			System.out.println(new String(buffer));
			Arrays.fill(buffer, (byte) 0);
			
			if(message.subSequence(0, 5).equals("IMAGE"))
				savingImage(buffer, in, out);
			else if(message.subSequence(0, 5).equals("WRITE"))
				writingData(buffer, in, out);
			else if(message.subSequence(0, 6).equals("SEARCH"))
				searchingData(buffer, in, out);			
			//받은 문자열이 out이면 클라이언트(연결) 종료
			else if(message.equals("out"))
				break;
			
			//받은 메세지 출력
			System.out.println("message: " + message);
		}}
		catch(Exception e)
		{
			System.out.println("Error: " + e);
		}
		
		try{
		s.close();
		System.out.println("Connection End.");
		}
		catch(Exception e)
		{
			System.out.println("Close Error: " + e);
		}
	}
	public static void savingImage(byte[] buffer, DataInputStream in, DataOutputStream out) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		BufferedImage img = ImageIO.read(new File("C:/Users/ChoiYeojin/Desktop/OSS/Desert.jpg"));
		
		out.writeInt(img.getHeight());
		in.read(buffer);
		System.out.println(new String(buffer));
		Arrays.fill(buffer, (byte) 0);
		
		out.writeInt(img.getWidth());
		in.read(buffer);
		System.out.println(new String(buffer));
		Arrays.fill(buffer, (byte) 0);
		
		ImageIO.write(img,  "jpg", baos);
		byte[] base64String = baos.toByteArray();
		
		out.writeInt(base64String.length);
		in.read(buffer);
		System.out.println(new String(buffer));
		Arrays.fill(buffer, (byte) 0);
		int count = 0;
		for(int i = 0; i < base64String.length; i += 4500)
		{
			if(base64String.length - i >= 4500)
				out.write(base64String, i, 4500);
			else
				out.write(base64String, i, base64String.length - i);
			out.flush();
			in.read(buffer);
			System.out.println(new String(buffer) + ": " + i);
			Arrays.fill(buffer, (byte) 0);
			count ++;
		}
		System.out.println("Count:" + count);
	}
	public static void writingData(byte[] buffer, DataInputStream in, DataOutputStream out) throws IOException
	{
		buffer = new byte[1024];
		out.write("EMPTY/A/B/C/D/E/".getBytes());
		out.flush();
		in.read(buffer);
		System.out.println(new String(buffer));
	}
	public static void searchingData(byte[] buffer, DataInputStream in, DataOutputStream out) throws IOException
	{
		buffer = new byte[1024];
		out.write("C".getBytes());
		out.flush();
		in.read(buffer);
		System.out.println(new String(buffer));
	}
}
