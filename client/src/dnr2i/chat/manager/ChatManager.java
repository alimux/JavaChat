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
 * Class which manages sending & retrieving message
 *
 * @author Alexandre DUCREUX 02/2017
 */
public class ChatManager extends ListenableModel {

    private PrintWriter output;
    private BufferedReader input;
    private Socket socket;
    private String outComingMessage;
    private Boolean justConnected = false;
    private ArrayList<User> userList;
    private User currentUser;
    private User sendedMessageUser;
    private Message message;
    private Thread t1, t2;
    private volatile boolean threadSuspended;
    private boolean listUpdated = true;

    /**
     * constructor
     *
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

    public ChatManager() {
        this.message = new Message();
        userList = new ArrayList<>();
    }

    /**
     * send message method
     *
     * @param ocMessage
     */
    public void sendMessage(String ocMessage) {
        System.out.println("Chat manager -> Envoi du message : " + message);
        outComingMessage = ocMessage;
        message.setMessageOutComing(outComingMessage);
        
        /* output.println("SET_MSG");
        output.println(outComingMessage);
        output.flush();
         */
        fireChanged();
        
    }

    /**
     * retrieve message method
     *
     * @param out
     */
    public void retrieveMessage(PrintWriter out) {

        System.out.println("reception d'un message");
        output.println("GET_MSG");
        output.flush();
        try {
            String incomingMessage = input.readLine();
            String[] splitMessage = incomingMessage.split("<END/>");
            //retrieve the user who sended message
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUserName().equals(splitMessage[0])) {
                    sendedMessageUser = userList.get(i);
                }
            }
            message.setInComingMessage(splitMessage[1]);
            fireChanged();

        } catch (IOException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Method which is called on login, send login and position to server
     *
     * @param loginName
     * @param x
     * @param y
     */
    public void login(String loginName, int x, int y) {
        if (!justConnected) {
            System.out.println(loginName + " vient de se connecter");
            
            //starting thread    
            t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) { 
                            System.out.println("Thread 1 lancé");
                            if(threadSuspended) {
                                synchronized (this) {
                                    while(threadSuspended){
                                        try {
                                            wait();
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            
                            }
                        
                        sendMessage(outComingMessage);
                        this.stop();
                    }
                }
                public synchronized void start(){
                    if(t1==null){
                        t1=new Thread(this);
                        threadSuspended = false;
                        System.out.println("thread 1 en démarre");
                        this.start();
                    }
                }
                public synchronized void stop(){
                    System.out.println("thread 1 en pause");
                    threadSuspended=true;
                }
            });

            t1.start();
            
            t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) { 
                            System.out.println("Thread 2 lancé");
                            System.out.println("Récupération des messages entrants");
                            if(threadSuspended) {
                                synchronized (this) {
                                    while(threadSuspended){
                                        try {
                                            wait();
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            
                            }
                        
                        retrieveMessage(output);
                        this.stop();
                    }
                }
                public synchronized void start(){
                    if(t2==null){
                        t2=new Thread(this);
                        threadSuspended = false;
                        System.out.println("thread 1 en démarre");
                        this.start();
                    }
                }
                public synchronized void stop(){
                    System.out.println("thread 2 en pause");
                    threadSuspended=true;
                }
               
            });
            
            t2.start();

            justConnected = true;
            currentUser = new User(loginName, x, y);
            addCurrentUser();
            fireChanged();

            justConnected = false;

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
     *
     * @return ArrayList
     */
    public ArrayList<User> retrieveUsersList() {

        System.out.println("reception de la liste des utilisateurs");
        output.println("GET_USERS_LIST");
        output.flush();
        try {
            //retrievelist
            String userListIncoming = input.readLine();
            //parsing list
            String[] userSplit = userListIncoming.split(";");
            for (int i = 0; i < userSplit.length; i++) {
                String[] user = userSplit[i].split(",");
                for (int j = 0; j < user.length; j++) {
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
     *
     * @return
     */
    public Boolean getJustConnected() {
        return justConnected;
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
     * add the current user in user list
     */
    public void addCurrentUser() {
        userList.add(this.currentUser);

    }

    /**
     * getter user list
     *
     * @return
     */
    public ArrayList<User> getUserList() {
        return userList;
    }

    /**
     * boolean properties to check if the user list connected is up to date.
     *
     * @return
     */
    public boolean isListUpdated() {
        return listUpdated;
    }

    /**
     * setter list updated
     *
     * @param listUpdated
     */
    public void setListUpdated(boolean listUpdated) {
        this.listUpdated = listUpdated;
    }

    /**
     * getter give the user
     *
     * @return
     */
    public User getSendedMessageUser() {
        return sendedMessageUser;
    }

    /**
     * getter thread1
     *
     * @return
     */
    public Thread getT1() {
        return t1;
    }

    /**
     * getter thread2
     *
     * @return
     */
    public Thread getT2() {
        return t2;
    }
    /**
     * return isThreadSuspended
     * @return 
     */
    public boolean isThreadSuspended() {
        return threadSuspended;
    }



}
