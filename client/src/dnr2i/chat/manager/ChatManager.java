package dnr2i.chat.manager;

import dnr2i.chat.gui.socket.Connection;
import dnr2i.chat.user.User;
import dnr2i.util.event.ListenableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

/**
 * Class which manages sending & retrieving message
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class ChatManager extends ListenableModel 
{

	private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    public Thread t1;
    
    private Message message;
    private HashMap<String, User> userList;
    
    //Used by modelChanged
    private String eventDirective;
    private String changeUser;
  
    private User currentUser;
    private User sendedMessageUser;

    /**
     * constructor
     *
     */
    public ChatManager() 
    {
        this.userList = new HashMap<String, User>();
        this.message = new Message();
        
        System.out.println("Connection in progress...");
        Connection connection = new Connection();
        this.socket = connection.getSocket();
        
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream());
            System.out.println("Connection to server established");
        } catch(IOException e) {
        	System.out.println("Connection to server failed: " + e.getMessage());
        }
            

        System.out.println("Starting thread for listening server directives...");
        this.t1 = new Thread(new ClientIncomingDirective(this, this.input));
        this.t1.start();
        
    }
    
    /**
     * Method which is called on login, send login and position to server
     *
     * @param loginName
     * @param x
     * @param y
     */
    public void login(String loginName, int x, int y) 
    {
        this.currentUser = new User(loginName, x, y);
        this.userList.put(loginName, this.currentUser);

        //send login to server
        System.out.println("Sending LOGIN directive to the server...");
        String login = loginName + "," + x + "," + y;
        output.println("LOGIN");
        output.println(login);
        output.flush();
        System.out.println("LOGIN directive send to the server: " + login);
        
        this.eventDirective = "LOGIN";
        fireChanged();
    }
    
    /**
     * method wich update the coordinates of the current user
     */
    public void sendCoordinate() 
    {
    	System.out.println("Sending SET_COORDINATE directive to the server...");
    	String coordinate = currentUser.getxPosition() + "," + currentUser.getyPosition();
        output.println("SET_COORDINATE");
        output.println(coordinate);
        output.flush();
        System.out.println("New coordinate sent to the server: " + coordinate);
    }

    /**
     * send message method
     *
     * @param ocMessage
     */
    public void sendMessage(String ocMessage)
    { //TODO check if ocMessage have the attended shape
    	//GET_MSG
    	System.out.println("Sending SET_MSG directive to the server...");
        this.message.setMessageOutComing(ocMessage);
        this.message.setInComingMessage(null);
        output.println("SET_MSG");
        output.println(ocMessage);
        output.flush();
        System.out.println("New message sent to the server : " + ocMessage);
        
        this.eventDirective = "NEW_OUT_MESSAGE";
        fireChanged();
    }

    /**
     * retrieve message method
     *
     * @param out
     */
    public void retrieveMessage(String incomingMessage) 
    {
        System.out.println("Handling the server response for directive GET_MSG: " + incomingMessage);

        String[] splitMessage = incomingMessage.split("<END/>");
        this.sendedMessageUser = this.userList.get(splitMessage[0]); //TODO check if sendedMessageUser is usefull
        this.message.setInComingMessage(splitMessage[1]);
        this.message.setMessageOutComing(null);
        
        this.eventDirective = "NEW_IN_MESSAGE";
        fireChanged();
    }
    
    public void addUserToList(String response)
    {
    	//called by SET_NEW_USER directive
    	String[] userSplit = response.split(",");
    	if(!this.userExist(userSplit[0])) {
    		User newUser = new User(userSplit[0], Integer.parseInt(userSplit[1]), Integer.parseInt(userSplit[2]));
        	this.userList.put(userSplit[0], newUser);
        	
        	this.changeUser = userSplit[0];
        	if (this.eventDirective != "ALREADY_CONNECTED") {
        		this.eventDirective = "WELCOME";
        		fireChanged();
        	} else {
        		fireChanged();
        		this.eventDirective = "WELCOME";
        	}            
    	}
    }
    
    public void deleteUserFromList(String oldUser)
    {
    	//called by SET_OLD_USER directive
    	if(this.userExist(oldUser)) {
    		this.userList.remove(oldUser);
    		
    		this.changeUser = oldUser;
    		this.eventDirective = "BYE";
            fireChanged();
    	}
    	//TODO disconect user
    }
    
    public boolean userExist(String username)
    {
    	return this.userList.containsKey(username);
    }

    /**
     * Method which retrieves the list of the Tchatters
     * Just called after the user connection
     * @return ArrayList
     */
    public void retrieveUsersList(String userList)
    {
    	//called by SET_USERS_LIST directive
        String[] userSplit = userList.split(";");
        for (int i = 0; i < userSplit.length; i++) {
        	this.eventDirective = "ALREADY_CONNECTED";
        	this.addUserToList(userSplit[i]);
        }
    }
    
    public void changeUserCoordinate(String response)
    {
    	//called by SET_NEW_COORDINATE directive
    	String[] responseSplit = response.split(",");
    	User user = this.userList.get(responseSplit[0]);
    	user.setxPosition(Integer.parseInt(responseSplit[1]));
    	user.setyPosition(Integer.parseInt(responseSplit[2]));
    	fireChanged();
    }
    
    /**
     * return the current user
     *
     * @return
     */
    public User getCurrentUser() {
        return currentUser;

    }

    /**
     * getter message instance
     *
     * @return
     */
    public Message getMessage() {
        return message;
    }

    /**
     * getter user list
     *
     * @return
     */
    public HashMap<String, User> getUserList() {
        return this.userList;
    }

    /**
     * getter give the user
     *
     * @return
     */
    public User getSendedMessageUser() {
        return sendedMessageUser;
    }
    
    public String getEventDirective()
    {
    	return this.eventDirective;
    }
    
    public String getChangeUser()
    {
    	return this.changeUser;
    }

}
