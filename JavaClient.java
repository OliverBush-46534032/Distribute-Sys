import java.net.*;
import java.io.*;

public class JavaClient {

public static void main(String args[]) {

	Socket socket = null;

	try {

	int serverPort = 50000;
	socket = new Socket("127.0.0.1", serverPort);

	BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	DataOutputStream out = new DataOutputStream(socket.getOutputStream());

	String data;
	boolean isAvailable = true;
	String serverType = null;
	int serverID = 0;
	int jobID = 0;
	int waitTime = 0;
	int lowestWaitTime = 0;
	int numberOfRecords = 0;
	String[] readyInfo = null;
	String[] serverData = null;
	String[] serverInfo = null;

	// Send HELO message to the server
	out.write(("HELO\n").getBytes());
	out.flush();
	data = input.readLine();

	// Send AUTH message with username to login
	out.write(("AUTH vboxuser\n").getBytes());
	out.flush();
	data = input.readLine();

	while (true) {
		// Send REDY message to receive information about the next job
		out.write(("REDY\n").getBytes());
		out.flush();
		data = input.readLine();

		readyInfo = data.split(" ");


		if (readyInfo[0].equals("JOBN")) {
			System.out.println(data);
			// Query the server state information for available servers
			out.write(("GETS Avail " + readyInfo[4] + " " + readyInfo[5] + " " + readyInfo[6] + "\n").getBytes());
			out.flush();
			data = input.readLine();

			serverData = data.split(" ");
			numberOfRecords = Integer.parseInt(serverData[1]);

			// Send OK command to acknowledge the request
			out.write(("OK\n").getBytes());
			out.flush();

			if (numberOfRecords == 0) {
				data = input.readLine();

				// Query the server state information for capable servers
				out.write(("GETS Capable " + readyInfo[4] + " " + readyInfo[5] + " " + readyInfo[6] + "\n").getBytes());
				out.flush();
				data = input.readLine();

				serverData = data.split(" ");
				numberOfRecords = Integer.parseInt(serverData[1]);


				// Send OK command to acknowledge the request
				out.write(("OK\n").getBytes());
				out.flush();

				for (int i = 0; i < numberOfRecords; i++) {
					data = input.readLine();
					serverInfo = data.split(" ");

					if (!serverInfo[2].equals("active")) {
						serverID = Integer.parseInt(serverInfo[1]);
						serverType = serverInfo[0];
						isAvailable = false;
					}

					waitTime = Integer.parseInt(serverInfo[7]);

					if (waitTime <= lowestWaitTime && isAvailable) {
						lowestWaitTime = waitTime;
						serverID = Integer.parseInt(serverInfo[1]);
						serverType = serverInfo[0];
						isAvailable = false;
					}
				}

				if (isAvailable) {
					serverID = Integer.parseInt(serverInfo[1]);
					serverType = serverInfo[0];
					isAvailable = false;
				}
			}
			
			if (isAvailable) {
				for (int i = 0; i < numberOfRecords; i++) {
					data = input.readLine();
					serverInfo = data.split(" ");

					if (i == 0) {
						serverID = Integer.parseInt(serverInfo[1]);
						serverType = serverInfo[0];
					}
				}
			}

			isAvailable = true;

			// Send OK command to acknowledge the request
			out.write(("OK\n").getBytes());
			out.flush();
			data = input.readLine();

			jobID = Integer.parseInt(readyInfo[2]);

			// Schedule the job on the selected server
			out.write(("SCHD " + jobID + " " + serverType + " " + serverID + "\n").getBytes());
			out.flush();
			data = input.readLine();
			
			}
			
			else if (readyInfo[0].equals("NONE")) {
			// No more jobs to process, exit the loop
			break;
		}
	}

	// Send QUIT message to the server
	out.write(("QUIT\n").getBytes());
	out.flush();

	}
	catch (UnknownHostException e) {
	System.out.println("Sock:" + e.getMessage());
	}
	catch (EOFException e) {
	System.out.println("EOF:" + e.getMessage());
	}
	catch (IOException e) {
	System.out.println("IO:" + e.getMessage());
	}
	finally {
		if (socket != null) {
			try {
				socket.close();
			}
			catch (IOException e) {
			System.out.println("close:" + e.getMessage());
			}
		}
	}
}
}

