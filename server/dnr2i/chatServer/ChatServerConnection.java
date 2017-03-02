package dnr2i.chatServer;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A class for establishing connection with the server
 * @author plabadille, Alexandre DUCREUX
 * @since February, 2017
 *
 */
public class ChatServerConnection implements Runnable 
{
	private ServerSocket ss;
	private ChatServer cs;
	public Thread t1;
	
	/**
	 * @param ss
	 * @param cs
	 */
	public ChatServerConnection(ServerSocket ss, ChatServer cs)
	{
		this.ss = ss;
		this.cs = cs;
	}
	
	/**
	 * Wait for user connection then create a new client thread
	 * @see java.lang.Runnable#run()
	 * @throws IOException
	 */
	@Override
	public void run()
	{
		try {
			while(true) {
				System.out.println("Waiting for client to connect ..."); 
				this.t1 = new Thread(new ChatServerDirectiveManager(this.cs, ss.accept()));
				this.t1.start();
			}
		} catch (IOException e) {
			System.out.println("Error handling client connection: " + e.getMessage());
		}
	}
	
}
