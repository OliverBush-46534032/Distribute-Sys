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
			
			// sending the 'HELO' message to server and waiting for reply
			out.write(("HELO\n").getBytes()); 
			out.flush();
			// reading the reply from the server and printing to the command line
			String data = in.readLine();
			System.out.println("HELO Received: "+ data);
			
			// sending the 'AUTH' with username to server to login
			out.write(("AUTH Oliver\n").getBytes());
			out.flush();
			data = in.readLine();
			System.out.println("AUTH Received: "+ data);
			
			// implementing while loop for job dispacher
			while(data != "NONE"){
				// send the 'REDY' message to recieve the information about the next job
				out.write(("REDY\n").getBytes());
				out.flush();
				data = in.readLine();
				System.out.println("REDY Received: "+ data);
				
				String[] JOBREDY = data.split(" ");
				String JOB = JOBREDY[0];
				
				// checking if there are any jobs left to run
				//if(JOB == "JOBN"){
					// qeries the server state information and stores it in data
					out.write(("GETS All\n").getBytes());
					out.flush();
					data = in.readLine();
					System.out.println("GETS Received: "+ data);
					
					//split the variable data so find the server Info 
					String[] serverI = data.split(" "); //split the messages by the gaps in the message
					int nRecs = Integer.parseInt(serverI[1]); //save in nRecs what's after the first gap
					System.out.println(nRecs);
					
					// sends the OK command
					out.write(("OK\n").getBytes());
					out.flush();
					data = in.readLine();
					System.out.println("OK Received: "+ data);
					
					String LS = null;
					int LSCount = 0;
					int LSCore = 0;
					
					//start the for loop for the larget server type
					for(int i = 0; i < nRecs; i++){
						data = in.readLine();
						String serverState[] = data.split(" ");
						
						if(Integer.parseInt(serverState[4]) > LSCore){
							LS = serverState[0];
							LSCount = 1;
							LSCore = Integer.parseInt(serverState[4]);
						}
						
						if(Integer.parseInt(serverState[4]) == LSCore && serverState[0].equals(LS)){
							LSCount++;
						}
					
					}
					
					// sends the OK command
					out.write(("OK\n").getBytes());
					out.flush();
					data = in.readLine();
					System.out.println("OK Received: "+ data);
					
					
					
				//}
			}
			
			
			out.write(("HELO\n").getBytes()); 
			out.flush();
			// reading the reply from the server and printing to the command line
			data = in.readLine();
			System.out.println("HELO Received: "+ data);
			
			
			// sending the 'QUIT' message to the server
			out.write(("QUIT\n").getBytes());
			out.flush();
			data = in.readLine();
			System.out.println("QUIT Received: "+ data);
			
		}catch (UnknownHostException e){
			System.out.println("Sock:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("IO:"+e.getMessage());}
	finally {if(s!=null) try {s.close();}catch (IOException e)
	{System.out.println("close:"+e.getMessage());}}
	}
}
