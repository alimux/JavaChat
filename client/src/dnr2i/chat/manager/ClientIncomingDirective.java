package dnr2i.chat.manager;

import java.io.BufferedReader;
import java.io.IOException;


public class ClientIncomingDirective implements Runnable {

	private ChatManager cm;
    private BufferedReader input;
		
	/**
	 * @param socket
	 * @param cs
	 */
	public ClientIncomingDirective(ChatManager cm, BufferedReader input)
	{
		this.cm = cm;
		this.input = input;
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
				System.out.println("Waiting for server directive ..."); 
				String clientDirective = this.input.readLine();
				System.out.println("New directive " + clientDirective + " received...");
				
				String input;
				switch(clientDirective) {
					case "GET_MSG":
						input = this.input.readLine();
						System.out.println("Directive GET_MSG received, handling the request...");
						this.cm.retrieveMessage(input);
//						this.server.handleClientMessage(this.username, input);
						
						break;
					case "SET_USERS_LIST":
						System.out.println("Directive GET_USERS_LIST received, handling the request...");
						input = this.input.readLine();


						break;
					default:
						System.out.println("Unknown directive received: " + clientDirective);
				}

			}
		} catch (IOException e) {
			System.out.println("Error handling server connection: " + e.getMessage());
		}
	}
	
}

