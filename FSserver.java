import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;

// DATE, USERNAME, BIGFOOD, SMALLFOOD, REGION, PRICE, EVALUE
// SEARCH//

public class FSserver {
	static LinkedList<BoardData> DataLinks = new LinkedList<BoardData>();
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
		byte[] buffer;
		
		public ThreadFunction(Socket socket)
		{
			this.socket = socket;

		}
		@Override
		public void run()
		{			
			while(true)
			{
			try{
				in = new DataInputStream(this.socket.getInputStream());
				out = new DataOutputStream(this.socket.getOutputStream());
				byte[] buffer = new byte[1000];
				Arrays.fill(buffer, (byte) 0);
				in.read(buffer);
				String bufStr = new String(buffer, "UTF-8");
				
				if(buffer.equals("out"))
				{
					System.out.println("connection End");
					break;
				}
				out.write("OK".getBytes(), 0, "OK".getBytes().length);
				
				System.out.println(socket + ": " + bufStr);
				if(bufStr.subSequence(0, 5).equals("WRITE"))
					saveWriteData(buffer);
				else if(bufStr.subSequence(0, 5).equals("IMAGE"))
					savingImage(bufStr);
				else if(bufStr.subSequence(0, 6).equals("SEARCH"))
					searchData(bufStr);
				
			}
			catch(Exception e){
				System.out.println("Send & Receive Error: " + e);
				break;
			}
			}
			try{
				socket.close();
			}
			catch(Exception e)
			{
				System.out.println("Socket closing Error: " + e);
			}
			
		}
		public void savingImage(/*String*/String data) throws IOException
		{
			int imgH = in.readInt();
			System.out.println("imgH: " + imgH);
			out.write("ImageH OK".getBytes());
			out.flush();
			int imgW = in.readInt();
			System.out.println("imgW: " + imgW);
			out.write("ImageW OK".getBytes());
			out.flush();
			int length = in.readInt();
			System.out.println("length: " + length);
			out.write("Byte Length OK".getBytes());
			out.flush();
			byte[] baseTemp = new byte[4500];
			byte[] base64String = new byte[length];
			int count = 0;
			for(int i = 0; i < length; i += 4500)
			{
				int dataLength = in.read(baseTemp);
				System.out.println("ImageByte: " + dataLength);
				System.arraycopy(baseTemp, 0, base64String, i, dataLength);
				out.write("IMAGE READ OK".getBytes());
				out.flush();
				count ++;
			}
			
			System.out.println("image read ok: " + count);
			
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(base64String));
			ImageIO.write(img, "jpg", new File("C:/Users/ChoiYeojin/Desktop/OSS/DesertTest.jpg"));
		}
		public void saveWriteData(byte[] buf) throws IOException
		{
			BoardData temp = new BoardData();
			Date now = new Date();
			//byte[] buf = new byte[1024];
			//in.read(buf);
			String data = new String(buf, "UTF-8");
			temp.setMiddle5Datas(data.split("/"));
			temp.setData(0, now.toString());
			
			DataLinks.add(temp);
			System.out.println("Success Save new Board Datas: " + DataLinks.size());
			//out.write("SAVED".getBytes());
			//out.flush();
		}
		public void searchData(String data) throws IOException
		{
			byte[] buf = new byte[1024];
			Arrays.fill(buf, (byte) 0);
			int length = in.read(buf);
			Iterator<BoardData> iterator = DataLinks.iterator();
			BoardData temp = DataLinks.getFirst();
			String dataResult;
			String bufStr = new String(buf, 0, length);
			while(iterator.hasNext())
			{
				String tempStr = temp.getData(2);
				if(bufStr.equals(tempStr))
				{
					dataResult = temp.getAll();
					out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
					return;
				}
				else
				{
					temp = iterator.next();
				}
			}
			out.write("NODATA".getBytes());
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FSserver ms = new FSserver();
		ms.init();

	}

}

class BoardData {
	String[] data = new String[7];
	
	public String getData(int index)
	{
		return data[index];
	}
	public String getAll()
	{
		return (data[0] + "/" + data[1] + "/" + data[2] + "/" + data[3] + "/" + data[4] + "/" + data[5]);
	}
	public void setData(int index, String str)
	{
		data[index] = str;
	}
	public void setMiddle5Datas(String[] str)
	{
		for(int i = 1; i < 6; i ++)
		{
			data[i] = str[i];
		}
	}
}
