package dnr2i.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {
	
	public final static int PORT = 3636;
	public final static String HOST = "localhost";

	public static void main(String[] args) 
	{		
		/* server setup */
		new ChatServer(PORT);
		/* client socket */
//		Socket socket;
//		BufferedReader in;
//		PrintWriter out;
//		try {
//			socket = new Socket(HOST, PORT);
//			System.out.println("Successfully created socket: " + socket);
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			out = new PrintWriter(socket.getOutputStream());
//			
//			connection(socket, in, out);
////			getUserList(socket, in, out);
//		} catch(IOException e) {
//			System.out.println(" Error getting buffer for server: " + e.getMessage());
//		}	
		
	}
//	
//	public static void connection(Socket socket, BufferedReader in, PrintWriter out)
//	{
//		System.out.println("Send user connexion request");
//		/* first user connexion */
//		out.println("LOGIN");
//        out.println("pierre,3,5");
//        out.flush();
//        
//        sendMessage(socket, in, out);
//	}
//	
//	public static void sendMessage(Socket socket, BufferedReader in, PrintWriter out)
//	{
//		out.println("SET_MSG");
//		out.println("Bonjour!");
//		out.flush();
//	}
	
//	public static void getUserList(Socket socket, BufferedReader in, PrintWriter out)
//	{
//		out.println("GET_USERS_LIST");
//		out.flush();
//		String usersList;
//		try {
//			usersList = in.readLine();
//			System.out.println("Connected users: " + usersList);
//        } catch (IOException e) {
//        	System.out.println(" Error getting userList from server: " + e.getMessage());
//        }
//	}

}
