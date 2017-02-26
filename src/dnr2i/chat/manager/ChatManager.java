package dnr2i.chat.manager;

import dnr2i.chat.gui.GUIJavaChat;
import dnr2i.chat.user.User;
import dnr2i.util.event.ListenableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Class which manages sending & retrieving message
 * @author Alexandre DUCREUX 02/2017
 */
public class ChatManager extends ListenableModel implements Runnable {

    private PrintWriter output;
    private BufferedReader input;
    private Socket socket;
    private String incomingMessage;
    private String outComingMessage;
    private Boolean justConnected = false;
    private ArrayList<User> userList;
    private User currentUser;
    private Message message;
    private Thread t1;
    private boolean suspended = true;
    private boolean listUpdated = true;
    
    /**
     * constructor
     * @param socket 
     */
    public ChatManager(Socket socket) {
        this.socket = socket;
        userList = new ArrayList<>();
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    public ChatManager (){
        this.message = new Message();
        userList = new ArrayList<>();
    }
    /**
     * send message method
     * @param message 
     */
    public void sendMessage(String ocMessage) {
        System.out.println("Chat manager -> Envoi du message : " + message);
        outComingMessage = ocMessage;
        message.setMessageOutComing(outComingMessage);
        resume();
       /* output.println("SET_MSG");
        output.println(outComingMessage);
        output.flush();
        */
       
       fireChanged();
    }
    /**
     * retrieve message method
     * @param out 
     */
    public void retrieveMessage(PrintWriter out) {

        System.out.println("reception d'un message");
        output.println("GET_MSG");
        output.flush();
        try {
            incomingMessage = input.readLine();

        } catch (IOException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * getter incomingMessage
     * @return String
     */
    public String getIncomingMessage() {
        return incomingMessage;
    }
    /**
     * Method which is called on login, send login and position to server
     * @param loginName
     * @param x
     * @param y 
     */
    public void login(String loginName, int x, int y){
        if(!justConnected){
            System.out.println(loginName+" vient de se connecter");
                t1 = new Thread(this); 
                t1.start();
            
            justConnected=true;
            currentUser = new User(loginName, x, y);
            addCurrentUser();
            fireChanged();
            
            justConnected=false;
            
            
            //send login to server
            /*output.println("LOGIN");
            output.println(loginName);
            output.println(x);
            output.println(y);
            output.flush();
            */
            
        }
    }
    /**
     * Method which retrieves the list of the Tchatters
     * @return ArrayList
     */
    public ArrayList<User> retrieveUsersList(){
        
        System.out.println("reception de la liste des utilisateurs");
        output.println("GET_USERS_LIST");
        output.flush();
        try {
            //retrievelist
            String userListIncoming = input.readLine();
            //parsing list
            String[] userSplit = userListIncoming.split(";");
            for(int i=0;i<userSplit.length;i++){
                String[] user = userSplit[i].split(",");
                for(int j=0;j<user.length;j++){
                    userList.add(new User(user[0], Integer.valueOf(user[1]), Integer.valueOf(user[2])));
                }
            }
            return userList;
            

        } catch (IOException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    /**
     * getterJustConnected
     * @return 
     */
    public Boolean getJustConnected() {
        return justConnected;
    }

    public User getCurrentUser() {
        return currentUser;
        
    }

   
    @Override
    public void run() {
        
        while (!suspended) {
            sendMessage(outComingMessage);
            suspend();
            synchronized (this) {
                while (suspended) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GUIJavaChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

    }

    public void suspend() {
        suspended = true;
    }

    synchronized void resume() {
        suspended = false;
        notify();
    }

    public Message getMessage() {
        return message;
    }
    public void addCurrentUser(){
        userList.add(this.currentUser);
        
    }
    public ArrayList<User> getUserList() {
        return userList;
    }

    public boolean isListUpdated() {
        return listUpdated;
    }

    public void setListUpdated(boolean listUpdated) {
        this.listUpdated = listUpdated;
    }
    
    
    
    


}
