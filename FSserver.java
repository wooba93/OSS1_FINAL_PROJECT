import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;

// DATE, USERNAME, FOODNAME, FOODSTYLE, REGION, PRICE, LOCATION, EVALUE

public class FSserver {
	//all boarded data
	static userLinkedList DataLinks = new userLinkedList();
	static int port = 8080;
	ServerSocket serverSocket = null;
	Socket socket = null;
	
	//initializing socket
	public void init() {
		try{
			//listen
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

					//read request from client
					in.read(buffer);
					String bufStr = new String(buffer, "UTF-8");
					
					System.out.println(socket + ": " + bufStr);
					//save board data - string
					if(bufStr.subSequence(0, 5).equals("WRITE"))
					{
						out.write("OK".getBytes(), 0, "OK".getBytes().length);
						saveWriteData(buffer);
					}
					//save board image - image
					else if(bufStr.subSequence(0, 5).equals("IMAGE"))
					{
						out.write("OK".getBytes(), 0, "OK".getBytes().length);
						savingImage(bufStr);
					}
					//search data
					else if(bufStr.subSequence(0, 6).equals("SEARCH"))
					{
						out.write("OK".getBytes(), 0, "OK".getBytes().length);
						searchData(bufStr);
					}
					//recommend food type
					else if(bufStr.subSequence(0, 9).equals("RECOMMEND"))
						recommendFood(bufStr);
					//end connection
					else
					{
						System.out.println("connection End");
						break;
					}
				
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
			//read image size
			int length = in.readInt();
			System.out.println("length: " + length);
			out.write("Byte Length OK".getBytes());
			out.flush();
			
			byte[] baseTemp = new byte[4500];
			byte[] base64String = new byte[length];
			int count = 0;
			int dataLength = 0;
			
			//read image by byte type
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
			
			//convert byte[] to image.jpg and save image
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(base64String));
			int integer = DataLinks.size() - 1;
			String s = imagePath + Integer.toString(integer) + ".jpg";
			ImageIO.write(img, "jpg", new File(s));
			
			//save image name
			DataLinks.getLast().setImageName(s);
		}
		public void saveWriteData(byte[] buf) throws IOException
		{
			BoardData temp = new BoardData();
			String[] dataStr;
			String data = new String(buf, "UTF-8");
			dataStr = data.split("/");
			
			//save all data
			temp.setData(dataStr);
			
			DataLinks.add(temp);
			System.out.println("Success Save new Board Datas: " + DataLinks.size());
		}
		public void sendingImage(String name) throws IOException
		{
			//send image from server to client
			//convert image.jpg to byte[]
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
			BufferedImage img = ImageIO.read(new File(name));
			ImageIO.write(img,  "jpg", baos);
			byte[] base64String = baos.toByteArray();
			byte[] buffer = new byte[1024];
			
			//send image size to client
			out.write(Integer.toString(base64String.length).getBytes());
			in.read(buffer);
			Arrays.fill(buffer, (byte) 0);
			
			int count = 0;
			//send image
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
			int sizeOfDatas = DataLinks.sizeOfDatas(splitData[2], splitData[4]);
			in.read(buf);
			System.out.println(new String(buf));
			Arrays.fill(buf, (byte) 0);
			
			//if there is no found data
			if(sizeOfDatas == 0)
			{
				out.write("SEARCHEND".getBytes());
				in.read(buf);
				System.out.println(new String(buf));
				Arrays.fill(buf, (byte) 0);
				return;
			}
			//send the number of data
			out.write(Integer.toString(sizeOfDatas).getBytes());
			in.read(buf);
			
			Arrays.fill(buf, (byte) 0);
			while(iterator.hasNext())
			{
				BoardData temp = iterator.next();
				String tempStr1 = temp.getData(3);
				String tempStr2 = temp.getData(4);
				//find data with condition
				//all & all
				if(splitData[2].equals("전체") && splitData[4].equals("전체"))
				{
					dataResult = temp.getAll();
					//send boarded data
					out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
					in.read(buf);
					Arrays.fill(buf, (byte) 0);
					//send boarded image
					sendingImage(temp.getImageName());
				}
				//all & special
				else if(splitData[2].equals("전체"))
				{
					if(splitData[4].equals(tempStr2))
					{
						dataResult = temp.getAll();
						//send boarded data
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
						//send boarded image
						sendingImage(temp.getImageName());
					}
				}
				//special & all
				else if(splitData[4].equals("전체"))
				{
					if(splitData[2].equals(tempStr1))
					{
						dataResult = temp.getAll();
						//send boarded data
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
						//send boarded image
						sendingImage(temp.getImageName());
					}
				}
				//special & special
				else
				{
					if(splitData[2].equals(tempStr1) && splitData[4].equals(tempStr2))
					{
						dataResult = temp.getAll();
						//send boarded data
						out.write(dataResult.getBytes(), 0, dataResult.getBytes().length);
						in.read(buf);
						Arrays.fill(buf, (byte) 0);
						//send boarded image
						sendingImage(temp.getImageName());
					}
				}
			}
			System.out.println("SEARCH COMPLETE");
		}
		public void recommendFood(String data) throws IOException
		{
			String[] splitData = data.split("/");
			
			Iterator<BoardData> iterator = DataLinks.iterator();
			recommendCount[] count = {new recommendCount("한식"), new recommendCount("중식"), new recommendCount("양식"), new recommendCount("일식")}; // 한식 중식 양식 일식
			
			while(iterator.hasNext())
			{
				BoardData temp = iterator.next();
				String tempStr = temp.getData(1);
				//check user's board and count
				if(splitData[1].equals(tempStr))
				{
					if(temp.getData(3).equals("한식"))
						count[0].ascCount();
					else if(temp.getData(3).equals("중식"))
						count[1].ascCount();
					else if(temp.getData(3).equals("양식"))
						count[2].ascCount();
					else
						count[3].ascCount();
				}
			}
			//find minimum food type
			//if there are a lot of food type, choose only one
			String result = findMinRandom(count);
			//send chosen food type
			out.write(result.getBytes());
			System.out.println("RECOMMEND COMPLETE");
		}
		public String findMinRandom(recommendCount[] recount)
		{
			//sort(ascending) by count
			Arrays.sort(recount, recommendCount.countComparator);
			int index = 3;
			
			//count the number of minimum food type
			while(recount[0].getCount() < recount[index].getCount())
			{
				index --;
			}
			
			//generate random number
			Random rand = new Random();
			int n = rand.nextInt(index + 1);
			
			return recount[n].getName();
		}
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FSserver ms = new FSserver();
		ms.init();
	}

}
//class for "count-food type"
class recommendCount
{
	int count;
	String name;
	recommendCount(String nameStr)
	{
		count = 0;
		name = nameStr;
	}
	public void ascCount()
	{
		count ++;
	}
	public int compareTo(recommendCount o)
	{
	    return (int)(this.count - o.count);
	}
	public int getCount()
	{
		return count;
	}
	public String getName()
	{
		return name;
	}
	public static Comparator<recommendCount> countComparator = new Comparator<recommendCount>() {
		public int compare(recommendCount rec1, recommendCount rec2)
		{
			return rec1.compareTo(rec2);
		}
	};
}
//class for boarded data, extends linked list structure
class userLinkedList extends LinkedList<BoardData>
{
	//count selected data with data1 and data2
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
//class for boarded data - saving actual data
class BoardData {
	String[] data = new String[8];
	String imageName;
	
	public void setImageName(String name)
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
	public void setData(String[] str)
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
