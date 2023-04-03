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
			
			//Initialise variables
			String data;
			boolean flag = true;
			String LS = null;
			int LSCount = 0;
			int LSCore = 0;
			int SID = 0;
			int JOBID = 0;
			String[] REDY = null;
			
			// sending the 'HELO' message to server and waiting for reply
			out.write(("HELO\n").getBytes()); 
			out.flush();
			// reading the reply from the server and printing to the command line
			data = in.readLine();
			//System.out.println("HELO Received: "+ data);
			
			// sending the 'AUTH' with username to server to login
			out.write(("AUTH vboxuser\n").getBytes());
			out.flush();
			data = in.readLine();
			//System.out.println("AUTH Received: "+ data);
			
			
			// implementing while loop for job dispacher
			while(data.equals("NONE") == false){
				// send the 'REDY' message to recieve the information about the next job
				out.write(("REDY\n").getBytes());
				out.flush();
				data = in.readLine();
				
				System.out.println("Received: "+ data);
				
				REDY = data.split(" ");
				if(flag){
				// qeries the server state information and stores it in data
				out.write(("GETS All\n").getBytes());
				out.flush();
				data = in.readLine();
				//System.out.println("Received: "+ data);
				
				
				// sends the OK command
				out.write(("OK\n").getBytes());
				out.flush();
				
				//split the variable data so find the server Info 
				String[] serverI = data.split(" "); //split the messages by the gaps in the message
				int nRecs = Integer.parseInt(serverI[1]); //save in nRecs what's after the first gap
				//System.out.println(nRecs);
				
				
				//start the for loop for the larget server type
				for(int i = 0; i < nRecs; i++){
					data = in.readLine();
					String[] serverState = data.split(" ");
					
					if(Integer.parseInt(serverState[4]) > LSCore){
						LS = serverState[0];
						LSCount = 1;
						LSCore = Integer.parseInt(serverState[4]);
					}
					
					if(Integer.parseInt(serverState[4]) == LSCore && serverState[0].equals(LS)){
						LSCount++;
					}
			
				}
				//System.out.println(LS);
				
				// sends the OK command
				out.write(("OK\n").getBytes());
				out.flush();
				data = in.readLine();
				//System.out.println("Received: "+ data);
				flag = false;
				
				}//end of singular run
				
				
				
				
				// start scheduling jobs
				if(REDY[0].equals("JOBN")){
					
					JOBID = Integer.parseInt(REDY[2]);;
					
					out.write(("SCHD "+JOBID+" "+LS+" "+SID+"\n").getBytes());
					//System.out.println(JOBID);
					out.flush();
					
					SID++;
					if(SID >= LSCount-1){
						SID = 0;
					}
					while(data == in.readLine()){
						continue;
					}
					
				}
			}
			
			
			// sending the 'QUIT' message to the server
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
