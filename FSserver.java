import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

import javax.imageio.*;
import javax.imageio.stream.*;

import com.sun.org.apache.xerces.internal.impl.dv.util.*;
// DATE, USERNAME, BIGFOOD, SMALLFOOD, REGION, PRICE, EVALUE

public class FSserver {
	LinkedList<BoardData> DataLinks = new LinkedList<BoardData>();
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
		//String name;
		String name;
		
		public ThreadFunction(Socket socket)
		{
			this.socket = socket;

		}
		@Override
		public void run()
		{
			//String name = "";
			
			while(true)
			{
			try{
				name = "";
				
				in = new DataInputStream(this.socket.getInputStream());
				out = new DataOutputStream(this.socket.getOutputStream());
				
				name = in.readUTF();
				
				//name = in.read();
				if(name.equals("out"))
				{
					System.out.println("connection End");
					break;
				}
				
				out.writeUTF("OK");
				System.out.println(socket + ": " + name);
				if(name.subSequence(0, 5).equals("WRITE"))
					saveWriteData(name);
				if(name.subSequence(0, 5).equals("IMAGE"))
					savingImage(name);
				
				
			}
			catch(Exception e){
				System.out.println("Send & Receive Error: " + e);
			}
			}
			
		}
		public void savingImage(/*String*/String data) throws IOException
		{
			int imgH = in.readInt();
			System.out.println("imgH: " + imgH);
			out.writeUTF("ImageH OK");
			int imgW = in.readInt();
			System.out.println("imgW: " + imgW);
			out.writeUTF("ImageW OK");
			
			int length = in.readInt();
			System.out.println("length: " + length);
			out.writeUTF("Byte Length OK");
			
			byte[] base64String = new byte[length];
			in.read(base64String, 0, length);
			out.writeUTF("IMAGE READ OK");
			System.out.println("image read ok");
			
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(base64String));
			ImageIO.write(img, "jpg", new File("C:/Users/ChoiYeojin/Desktop/DesertTest.jpg"));
			
		}
		public void saveImage(String data)
		{
			
			try{
			
			//String imgb = "";
			int imgH = in.readInt();
			out.writeUTF("ImageH OK");
			out.flush();
			int imgW = in.readInt();
			out.writeUTF("ImageW OK");
			out.flush();
			int byteLength = in.readInt();
			out.writeUTF("bLength OK");
			out.flush();
			
			byte[] imageByte = new byte[byteLength];
			
			in.read(imageByte, 0, imgH * imgW);
			
			System.out.println("[Image]: " + imageByte.length);
			out.writeUTF("IMAGE READ OK");
			out.flush();
			//imageByte = imgb.getBytes();
			//BufferedImage image = createRGBImage(imageByte);
//			FileOutputStream stream = new FileOutputStream("C:/Users/ChoiYeojin/Desktop/DesertTest.jpg");
			//ImageIO.write(image, "jpg", new File("C:/Users/ChoiYeojin/Desktop/DesertTest.jpg"));
			
			ByteArrayInputStream bais = new ByteArrayInputStream(imageByte);
			Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) readers.next();
			Object source = bais;
			ImageInputStream iis = ImageIO.createImageInputStream(source);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Image image = reader.read(0, param);
			BufferedImage bufferedImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bufferedImage.createGraphics();
			g2d.drawImage(image, null, null);
			File imageFile = new File("C:/Users/ChoiYeojin/Desktop/DesertTest.jpg");
			ImageIO.write(bufferedImage, "jpg", imageFile);
			System.out.println("IMAGE SAVE");
			} catch(Exception e) {
				System.out.println("reading img Failed.");
			}
			
			
		}
		public void saveWriteData(String data)
		{
			Date now = new Date();
			BoardData temp = new BoardData();
			
			temp.setMiddle5Datas(data.split("/"));
			temp.setData(0, now.toString());
			/*try {
				temp.setData(6, in.readUTF());
				out.writeUTF("OK");
				System.out.println(socket + ": evalue");
			}
			catch(Exception e)
			{
				System.out.println("saveWriteData Error: " + e);
			}*/
			DataLinks.add(temp);
			System.out.println("Success Save new Board Datas: " + DataLinks.size());
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FSserver ms = new FSserver();
		ms.init();

	}
	private static BufferedImage createRGBImage(byte[] bytes)
	{
		DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
		ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8,8,8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, Raster.createInterleavedRaster(buffer, 1024, 768, 1024 * 3, 3, new int[]{0, 1, 2}, null), false, null);
	}

}

class BoardData {
	String[] data = new String[7];
	
	public String getData(int index)
	{
		return data[index];
	}
	public void setData(int index, String str)
	{
		data[index] = str;
	}
	public void setMiddle5Datas(String[] str)
	{
		for(int i = 2/*1*/; i < 5/*6*/; i ++)
		{
			data[i] = str[i + 1];
		}
	}
}
