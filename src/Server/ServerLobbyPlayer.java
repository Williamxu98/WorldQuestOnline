package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerLobbyPlayer implements Runnable {
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private boolean isLeader = false;
	private Server server;

	public static int nextTeam = 0;
	private int team = 0;
	private String IP = "";
	private String name = "Player";
	private boolean started = false;

	public ServerLobbyPlayer(Socket socket, Server server)
	{
		this.socket = socket;
		this.IP = socket.getInetAddress().toString();
		this.server = server;
		this.team = nextTeam%2+1;
		nextTeam++;

		// Set up the output
		try
		{
			output = new PrintWriter(this.socket.getOutputStream());
			sendMessage("M "+server.getMap());
		}
		catch (IOException e)
		{
			System.out.println("Error getting client's output stream");
			e.printStackTrace();
		}

		// Set up the input
		try
		{
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}
		catch (IOException e)
		{
			System.out.println("Error getting client's input stream");
			e.printStackTrace();
		}
		
	}

	/**
	 * Input thread
	 */
	public void run() {
		while (true)
		{
			try
			{
				// Read the next line the player sent in
				String command = input.readLine();

				if(command.length() >= 3 && command.charAt(0) == 'C')
				{
					String message = command.substring(2);
					String[] tokens = message.split(" ");

					server.broadcast("CH " + "E "+(getTeam() + getName()).split(" ").length
							+ " " + getTeam() + getName() + " "
							+ tokens.length + " " + message);
				}
				else if(command.equals("S") && isLeader)
				{
					started = true;
					server.start();
				}
				else if(command.length() > 2 && command.substring(0,2).equals("Na"))
				{
					try
					{
						name = command.substring(3);
					}
					catch(Exception E)
					{
						continue;
					}
					server.broadcast("JO " + getName().split(" ").length + " "
							+ getTeam() + getName());
				}
			}
			catch (IOException e)
			{
				break;
			}
			catch (NullPointerException e)
			{
				break;
			}

		}

		System.out.println("A client has disconnected");
		try
		{
			input.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			System.out.println("Error closing buffered reader");
		}
		
		if(!started)
			server.remove(this);

	}

	public void setLeader()
	{
		isLeader = true;
		sendMessage("L");
	}

	public boolean isLeader()
	{
		return isLeader;
	}

	public int getTeam()
	{
		return team;
	}

	public String getName()
	{
		return name;
	}

	public void sendMessage(String message)
	{
		output.println(message);
		output.flush();
	}

	public String getIP()
	{
		return IP;
	}

}
