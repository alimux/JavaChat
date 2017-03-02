package dnr2i.chatServer;

/**
 * A main to launch the chat server
 * @author plabadille, Alexandre DUCREUX
 * @since February, 2017
 */
public class StartServer
{
	public final static int PORT = 3636;

	public static void main(String[] args) 
	{		
		new ChatServer(PORT);
	}
	
}
