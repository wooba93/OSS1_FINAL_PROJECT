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
				//해당 아이피와 포트로 소켓 연결
			socket = new Socket(ip, port);
			System.out.println("Connecting success.");
			
			//해당 소켓에서 들어오는 버퍼
			in = new DataInputStream(socket.getInputStream());
			//해당 소켓에 출력하는 버퍼
			out = new DataOutputStream(socket.getOutputStream());
			
			while(true){			
			String message = "";
			//사용자가 입력한 문자열을 받음
			message = s.nextLine();
			//받은 문자열을 서버에 전송
			out.writeUTF(message);
			//받은 문자열이 out이면 클라이언트(연결) 종료
			if(message.equals("out"))
				break;
			//서버로부터 메세지 받음
			message = in.readUTF();
			//받은 메세지 출력
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