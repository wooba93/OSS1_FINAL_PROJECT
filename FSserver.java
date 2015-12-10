import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;

// DATE, USERNAME, FOODNAME, FOODSTYLE, REGION, PRICE, LOCATION, EVALUE

public class FSserver {
	static userLinkedList DataLinks = new userLinkedList();
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
		String imagePath = System.getProperty("user.dir").replace("\\", "/") + "/Images/FOODSHARE";
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
					else if(bufStr.subSequence(0, 9).equals("RECOMMEND"))
						recommendFood(bufStr);
				
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
		public void savingImage(String data) throws IOException
		{
			int length = in.readInt();
			System.out.println("length: " + length);
			out.write("Byte Length OK".getBytes());
			out.flush();
			byte[] baseTemp = new byte[4500];
			byte[] base64String = new byte[length];
			int count = 0;
			int dataLength = 0;
			for(int i = 0; i < length; i += dataLength)
			{
				dataLength = in.read(baseTemp);
				System.out.println("ImageByte: " + dataLength);
				System.arraycopy(baseTemp, 0, base64String, i, dataLength);
				out.write("IMAGE READ OK".getBytes());
				out.flush();
				count ++;
			}
			
			System.out.println("image read ok: " + count);
			
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(base64String));
			int integer = DataLinks.size() - 1;
			String s = imagePath + Integer.toString(integer) + ".jpg";
			ImageIO.write(img, "jpg", new File(s));
			
			DataLinks.getLast().savingImage(s);
		}
		public void saveWriteData(byte[] buf) throws IOException
		{
			BoardData temp = new BoardData();
			String[] dataStr;
			String data = new String(buf, "UTF-8");
			dataStr = data.split("/");
			temp.setAllDatas(dataStr);
			
			DataLinks.add(temp);
			System.out.println("Success Save new Board Datas: " + DataLinks.size());
		}
		public void sendingImage(String name) throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
			BufferedImage img = ImageIO.read(new File(name));
			ImageIO.write(img,  "jpg", baos);
			byte[] base64String = baos.toByteArray();
			byte[] buffer = new byte[1024];
			
			out.write(Integer.toString(base64String.length).getBytes());
			in.read(buffer);
			Arrays.fill(buffer, (byte) 0);
			int count = 0;
			
			for(int i = 0; i < base64String.length;)
			{
				if(base64String.length - i >= 4500)
				{
					out.write(base64String, i, 4500);
					i += 4500;
				}
				else
				{
					out.write(base64String, i, base64String.length - i);
					i += (base64String.length - i);
				}
				out.flush();
				
				count ++;
			}
			in.read(buffer);
			System.out.println(buffer + "/ " + count);
			Arrays.fill(buffer, (byte) 0);
		}
		public void searchData(String data) throws IOException
		{
			byte[] buf = new byte[1024];
			Arrays.fill(buf, (byte) 0);
			String[] splitData = data.split("/"); // SEARCH/FOODSTYLE/전체/REGION/전체
			Iterator<BoardData> iterator = DataLinks.iterator();
			String dataResult = "";
			
			out.write(Integer.toString(DataLinks.sizeOfDatas(splitData[2], splitData[4])).getBytes());
			in.read(buf);
			Arrays.fill(buf, (byte) 0);
			while(iterator.hasNext())
			{
				BoardData temp = iterator.next();
				String tempStr1 = temp.getData(3);
				String tempStr2 = temp.getData(4);
				if(splitData[2].equals("전체") && splitData[4].equals("전체"))
				{
					dataResult = temp.getAll();
					out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
					in.read(buf);
					Arrays.fill(buf, (byte) 0);
 					sendingImage(temp.getImageName());
				}
				else if(splitData[2].equals("전체"))
				{
					if(splitData[4].equals(tempStr2))
					{
						dataResult = temp.getAll();
						dataResult += "/";
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
						sendingImage(temp.getImageName());
					}
				}
				else if(splitData[4].equals("전체"))
				{
					if(splitData[2].equals(tempStr1))
					{
						dataResult = temp.getAll();
						dataResult += "/";
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
						sendingImage(temp.getImageName());
					}
				}
				else
				{
					if(splitData[2].equals(tempStr1) && splitData[4].equals(tempStr2))
					{
						dataResult = temp.getAll();
						dataResult += "/";
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
						sendingImage(temp.getImageName());
					}
				}
			}
			System.out.println("SEARCH COMPLETE");
		}
		public void recommendFood(String data) throws IOException
		{
			byte[] buf = new byte[1024];
			Arrays.fill(buf, (byte) 0);
			String[] splitData = data.split("/");
			
			Iterator<BoardData> iterator = DataLinks.iterator();
			String dataResult = "";
			int[] count = new int[4]; // 한식 중식 양식 일식
			Arrays.fill(count, 0);
			while(iterator.hasNext())
			{
				BoardData temp = iterator.next();
				String tempStr = temp.getData(1);
				if(splitData[1].equals(tempStr))
				{
					if(temp.getData(3).equals("한식"))
						count[0] ++;
					else if(temp.getData(3).equals("일식"))
						count[1] ++;
					else if(temp.getData(3).equals("양식"))
						count[2] ++;
					else
						count[3] ++;
				}
			}
			Arrays.sort(count);
			
			System.out.println("RECOMMEND COMPLETE");
		}
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FSserver ms = new FSserver();
		ms.init();
	}

}
class userLinkedList extends LinkedList<BoardData>
{
	public int sizeOfDatas(String data1, String data2)
	{
		int count = 0;
		
		Iterator<BoardData> iterator = this.iterator();
		
		while(iterator.hasNext())
		{
			BoardData temp = iterator.next();
			String tempStr1 = temp.getData(3);
			String tempStr2 = temp.getData(4);
			if(data1.equals("전체") && data2.equals("전체"))
			{
				return this.size();
			}
			else if(data1.equals("전체"))
			{
				if(data2.equals(tempStr2))
					count ++;
			}
			else if(data2.equals("전체"))
			{
				if(data1.equals(tempStr1))
					count ++;
			}
			else
			{
				if(data1.equals(tempStr1) && data2.equals(tempStr2))
					count ++;
			}
		}
		
		return count;
	}
}

class BoardData {
	String[] data = new String[8];
	String imageName;
	
	public void savingImage(String name)
	{
		imageName = name;
	}
	public String getData(int index)
	{
		return data[index];
	}
	public String getAll()
	{
		String dataStr = data[0] + "/" + data[1] + "/" + data[2] + "/" + data[3] + "/" + data[4] + "/" + data[5] + "/" + data[6] + "/" + data[7];
		dataStr = dataStr + "/";
		return dataStr;
	}
	public void setAllDatas(String[] str)
	{
		for(int i = 0; i < 8; i ++)
		{
			data[i] = str[i + 1];
		}
	}
	public String getImageName()
	{
		return imageName;
	}
}
