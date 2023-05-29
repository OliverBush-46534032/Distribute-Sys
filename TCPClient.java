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
			boolean avail = true;
			String ST = null;
			int SID = 0;
			int JOBID = 0;
			int wait = 0;
			int LW = 0;
			int  nRecs = 0;
			String[] REDY = null;
			String[] serverI;
			String[] serverState = null;
			
			
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
			while(true){
				// send the 'REDY' message to recieve the information about the next job
				out.write(("REDY\n").getBytes());
				out.flush();
				data = in.readLine();
				
				//System.out.println("Received!: "+ data);
				
				REDY = data.split(" ");
				if(REDY[0].equals("JOBN")) {
					// qeries the server state information and stores it in data
					out.write(("GETS Avail " + REDY[4] + " " + REDY[5] + " " + REDY[6] + "\n").getBytes());
					//System.out.println("GETS Avail " + REDY[4] + " " + REDY[5] + " " + REDY[6] + "");
					out.flush();
					data = in.readLine();
					//System.out.println("Gets Ret: "+ data);
					//split the variable data so find the server Info 
					serverI = data.split(" "); //split the messages by the gaps in the message
					nRecs = Integer.parseInt(serverI[1]); //save in nRecs what's after the first gap
					//System.out.println(nRecs);
					if(nRecs == 0){
						avail = false;
						// sends the OK command
						out.write(("OK\n").getBytes());
						out.flush();
						data = in.readLine();
						// qeries the server state information and stores it in data
						out.write(("GETS Capable " + REDY[4] + " " + REDY[5] + " " + REDY[6] + "\n").getBytes());
						//System.out.println("GETS Capable " + REDY[4] + " " + REDY[5] + " " + REDY[6] + "");
						out.flush();
						data = in.readLine();
						//System.out.println("Gets Ret: "+ data);
						//split the variable data so find the server Info 
						serverI = data.split(" "); //split the messages by the gaps in the message
						nRecs = Integer.parseInt(serverI[1]); //save in nRecs what's after the first gap
						//System.out.println(nRecs);
					
					
						// sends the OK command
						out.write(("OK\n").getBytes());
						out.flush();
					
						
						for(int i = 0; i < nRecs; i++) {
							//split the return from gets
							data = in.readLine();
							//System.out.println("data: " + data);
							serverState = data.split(" ");
							//System.out.println("Server: " + serverState[0] + " " + i + " " + Integer.parseInt(serverState[1]));
							if(!serverState[2].equals("active")){
								SID = Integer.parseInt(serverState[1]);
								ST = serverState[0];
								flag = false;
								avail = false;
							}
							wait = Integer.parseInt(serverState[7]); //store the number of waiting jobs on a server
							if(wait <= LW && avail) {
								LW = wait;
								SID = Integer.parseInt(serverState[1]);
								ST = serverState[0];
								flag = false;
							}
						}
						if(flag) { 
							SID = Integer.parseInt(serverState[1]);
							ST = serverState[0];
						}
						flag = true;
						avail = false;
					}
					if(avail) {
						// sends the OK command
						out.write(("OK\n").getBytes());
						out.flush();
						
						for(int i = 0; i < nRecs; i++) {
							data = in.readLine();
							//System.out.println("data: " + data);
							serverState = data.split(" ");
							if(i == 0) {
								SID = Integer.parseInt(serverState[1]);
								ST = serverState[0];
							}
						}
					}
					avail = true;
						
					// sends the OK command
					out.write(("OK\n").getBytes());
					out.flush();
					data = in.readLine();
					//System.out.println("OKOK: " + data);
					
					JOBID = Integer.parseInt(REDY[2]);
					
					out.write(("SCHD "+JOBID+" "+ST+" "+SID+"\n").getBytes());
					//System.out.println("SCHD "+JOBID+" "+ST+" "+SID+"");
					out.flush();
					data = in.readLine();
					
					//System.out.println("ReceivedB: "+ data);
				}
				else if(REDY[0].equals("NONE")) {
					break;
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
