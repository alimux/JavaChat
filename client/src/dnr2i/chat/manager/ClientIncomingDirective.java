package dnr2i.chat.manager;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * A class used in a thread by the manager to wait for incoming server directive
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class ClientIncomingDirective implements Runnable 
{
	private ChatManager cm;
    private BufferedReader input;
		
	/**
	 * @param cm
	 * @param input
	 */
	public ClientIncomingDirective(ChatManager cm, BufferedReader input)
	{
		this.cm = cm;
		this.input = input;
	}
	
	/**
	 * Dispatch server directive to according manager function.
	 * Just run when this.input.readLine() get something from the server
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
				
				String input = this.input.readLine();
				switch(clientDirective) {
					case "GET_MSG": 
						System.out.println("Directive GET_MSG received, handling the request...");
						this.cm.retrieveMessage(input);
						break;
					case "SET_USERS_LIST":
						System.out.println("Directive GET_USERS_LIST received, handling the request...");
						this.cm.retrieveUsersList(input);
						break;
					case "SET_NEW_USER":
						System.out.println("Directive SET_NEW_USER received, handling the request...");
						this.cm.addUserToList(input);
						break;
					case "SET_OLD_USER":
						System.out.println("Directive SET_OLD_USER received, handling the request...");
						this.cm.deleteUserFromList(input);
						break;
					case "SET_NEW_COORDINATE":
						System.out.println("Directive SET_NEW_COORDINATE received, handling the request...");
						this.cm.changeUserCoordinate(input);
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
