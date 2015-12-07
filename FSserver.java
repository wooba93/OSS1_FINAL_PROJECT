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
			String s = System.getProperty("user.dir").replace("\\", "/") + "/Images/FOODSHARE" + Integer.toString(integer) + ".jpg";
			ImageIO.write(img, "jpg", new File(s));
			
			DataLinks.getLast().savingImage(DataLinks.size() - 1);
		}
		public void saveWriteData(byte[] buf) throws IOException
		{
			BoardData temp = new BoardData();
			//Date now = new Date();
			//byte[] buf = new byte[1024];
			//in.read(buf);
			String[] dataStr;
			String data = new String(buf, "UTF-8");
			dataStr = data.split("/");
			temp.setAllDatas(dataStr);
			//temp.setData(0, now.toString());
			
			DataLinks.add(temp);
			System.out.println("Success Save new Board Datas: " + DataLinks.size());
			//out.write("SAVED".getBytes());
			//out.flush();
		}
		public void searchData(String data) throws IOException
		{
			byte[] buf = new byte[1024];
			Arrays.fill(buf, (byte) 0);
			String[] splitData = data.split("/"); // SEARCH/FOODSTYLE/전체/REGION/전체
			Iterator<BoardData> iterator = DataLinks.iterator();
			//DataLinks.getFirst();
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
				}
				else if(splitData[2].equals("전체"))
				{
					if(splitData[4].equals(tempStr2))
					{
						dataResult = temp.getAll();
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
					}
				}
				else if(splitData[4].equals("전체"))
				{
					if(splitData[2].equals(tempStr1))
					{
						dataResult = temp.getAll();
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
					}
				}
				else
				{
					if(splitData[2].equals(tempStr1) && splitData[4].equals(tempStr2))
					{
						dataResult = temp.getAll();
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
					}
				}
			}
			//out.write("NODATA".getBytes());
			System.out.println("SEARCH COMPLETE");
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
			if(tempStr1.equals("전체") && tempStr2.equals("전체"))
			{
				return this.size();
			}
			else if(tempStr1.equals("전체"))
			{
				if(tempStr2.equals(data2))
					count ++;
			}
			else if(tempStr2.equals("전체"))
			{
				if(tempStr1.equals(data1))
					count ++;
			}
			else
			{
				if(tempStr1.equals(data1) && tempStr2.equals(data2))
					count ++;
			}
		}
		
		return count;
	}
}
class BoardData {
	String[] data = new String[8];
	String imageName;
	
	public void savingImage(int i)
	{
		imageName = System.getProperty("user.dir").replace("\\", "/") + "/Images/FOODSHARE";
		imageName += (i + ".jpg");
	}
	public String getData(int index)
	{
		return data[index];
	}
	public String getAll()
	{
		String dataStr = data[0] + "/" + data[1] + "/" + data[2] + "/" + data[3] + "/" + data[4] + "/" + data[5] + "/" + data[6] + "/" + data[7] + "//";
		dataStr += "/////";
		return dataStr;
	}
	public void setData(int index, String str)
	{
		data[index] = str;
	}
	public void setAllDatas(String[] str)
	{
		for(int i = 0; i < 8; i ++)
		{
			data[i] = str[i + 1];
		}
	}
}
