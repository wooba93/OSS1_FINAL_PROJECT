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
		//File imgPath = new File("C:/Users/ChoiYeojin/Desktop/Desert.jpg");
		//BufferedImage bimg = ImageIO.read(imgPath);
		/*WritableRaster raster = bimg.getRaster();
		DataBufferByte data = (DataBufferByte)raster.getDataBuffer();
		byte[][] dataByte = data.getBankData();*/
		//FileInputStream fis = new FileInputStream(imgPath);
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		
		//System.out.println("Height: " + bimg.getHeight() + " / Width: " + bimg.getWidth());
			try {
				//해당 아이피와 포트로 소켓 연결
			socket = new Socket(ip, port);
			System.out.println("Connecting success.");
			
			//해당 소켓에서 들어오는 버퍼

			//해당 소켓에 출력하는 버퍼

			
			while(true){			
			String message = "";
			byte[] buffer = new byte[1000];
			//사용자가 입력한 문자열을 받음
			message = s.nextLine();
			//받은 문자열을 서버에 전송
			//out.writeUTF(message);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			out.write(message.getBytes());
			out.flush();
			in.read(buffer);
			System.out.println(new String(buffer));
			Arrays.fill(buffer, (byte) 0);
			
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
			//baos.flush();
			
			byte[] base64String = baos.toByteArray();//Base64.encode(baos.toByteArray());
			
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
			
			
			/*out.writeInt(dataByte[0].length);
			System.out.println(in.readUTF());

			for(int i = 0; i < dataByte.length; i ++)
			{
				out.write(dataByte[i], 0, dataByte[i].length);
				out.flush();
				System.out.println(in.readUTF());
			}*/
			/*int knum = 0;
			for(int readNum; (readNum = fis.read(buf)) != -1;)
			{
				baos.write(buf,  0, readNum);
				System.out.println("read " + readNum + "bytes,");
				knum ++;
			}
			System.out.println("line: " + knum);
			byte[] dataByte = baos.toByteArray();
			out.writeInt(dataByte.length);
			System.out.println(in.readUTF());
			out.write(dataByte, 0, dataByte.length);
			out.flush();
			System.out.println(in.readUTF());*/
			//System.out.println(in.readUTF());
		
			//받은 문자열이 out이면 클라이언트(연결) 종료
			if(message.equals("out"))
				break;
			//서버로부터 메세지 받음
			//message = in.read();
			//in.readUTF();
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

}
