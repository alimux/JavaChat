package dnr2i.chatServer;

import java.io.IOException;
import java.net.ServerSocket;

public class ChatServerConnectionThread implements Runnable 
{
	
	private ServerSocket ss;
	private ChatServer cs;
	
	public ChatServerConnectionThread(ServerSocket ss, ChatServer cs)
	{
		this.ss = ss;
		this.cs = cs;
	}
	
	public void run()
	{
		try {
			while(true) {
				System.out.println("Waiting for client to connect ..."); 
				Thread thread = new Thread(new ChatServerThread(this.cs, ss.accept()));
				thread.run();
			}
		} catch (IOException e) {
			System.out.println("Error handling client connection: " + e.getMessage());
		}
	}
	
}
