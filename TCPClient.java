import java.net.*;
import java.io.*;
public class TCPClient {
	public static void main (String args[]) {
	// arguments supply message and hostname of destination
	Socket s = null;
		try{
			int serverPort = 50000;
			s = new Socket("127.0.0.1", serverPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			DataOutputStream out = new DataOutputStream( s.getOutputStream());
			out.write(("HELO\n").getBytes()); 
			out.flush();
			String data = in.readLine();
			System.out.println("Received: "+ data);
			
			out.write(("QUIT\n").getBytes());
			out.flush();
		}catch (UnknownHostException e){
			System.out.println("Sock:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("IO:"+e.getMessage());}
	finally {if(s!=null) try {s.close();}catch (IOException e)
	{System.out.println("close:"+e.getMessage());}}
	}
}
/*import java.net.*;
import java.io.*;
public class TCPClient {
public static void main (String args[]) {
// arguments supply message and hostname of destination
Socket s = null;
try{
int serverPort = 7896;
s = new Socket(args[1], serverPort);
BufferedReader in = new BufferedRead(new InputStreamReader( s.getInputStream()));
DataOutputStream out =
new DataOutputStream( s.getOutputStream());
out.write((args[0]+"\n").getBytes()); // UTF is a string encoding see Sn 4.3
out.flush();
String data = in.readLine();
System.out.println("Received: "+ data) ;
}catch (UnknownHostException e){
System.out.println("Sock:"+e.getMessage());
}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
}catch (IOException e){System.out.println("IO:"+e.getMessage());}
}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
}*/

