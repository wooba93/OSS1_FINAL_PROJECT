import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class clientTest {

	static int port = 8080;
	static String ip = "127.0.0.1";
	static Socket socket = null;
	
	public static void main(String[] args) {
		
		DataInputStream in;
		DataOutputStream out;
		Scanner s = new Scanner(System.in);
		

			try {
				//�ش� �����ǿ� ��Ʈ�� ���� ����
			socket = new Socket(ip, port);
			System.out.println("Connecting success.");
			
			//�ش� ���Ͽ��� ������ ����
			in = new DataInputStream(socket.getInputStream());
			//�ش� ���Ͽ� ����ϴ� ����
			out = new DataOutputStream(socket.getOutputStream());
			
			while(true){			
			String message = "";
			//����ڰ� �Է��� ���ڿ��� ����
			message = s.nextLine();
			//���� ���ڿ��� ������ ����
			out.writeUTF(message);
			//���� ���ڿ��� out�̸� Ŭ���̾�Ʈ(����) ����
			if(message.equals("out"))
				break;
			//�����κ��� �޼��� ����
			message = in.readUTF();
			//���� �޼��� ���
			System.out.println(message);
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