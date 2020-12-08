import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Calendar;

/*
    In practice, this class will have an object that will be referenced by a static variable in the Server class

*/


public class PrivateCodeManager extends Thread {
    private Hashtable<String, Client> MAP;
    private long MIN_TIME_TO_EXPIRY;
    private static final int DEFAULT_TIMEOUT = 30*60*1000;
    private String logString;
    PrivateCodeManager() {
        super("Expiry_Manager");
        this.setDaemon(true);
        this.MAP = new Hashtable<String, Client>();
        this.MIN_TIME_TO_EXPIRY = DEFAULT_TIMEOUT;
        this.start();
        this.logString = "Datetime,Action,Private_Key,Notes\n";
    }

    public boolean exists(String code) {
        return this.MAP.containsKey(code);
    }
 
    public synchronized void add(String code, String message, Socket soc, int role, int timeout) { //timeout is in minutes
        if (this.MAP.containsKey(code)) {
            throw new MapKeyException("Private code:" + code + " already exists");
        }
        this.interrupt();
        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT; //default timeout is in milliseconds
        }
        else{
            timeout = timeout*60*1000;
        }
        this.MAP.put(code, new Client(message, soc, role, timeout));

        this.notify(); // notify the PrivateCodeManagerThread which is removing expired clients
        this.logString += "";
    }
 
    public synchronized Client extract(String code, boolean retain) {
        if (!this.MAP.containsKey(code)) {
            throw new MapKeyException("Private code:" + code + " does not exist");
        }
        Client client = this.MAP.get(code);
        if (!retain)
        this.MAP.remove(code);
        
        return client;
        
    }

    

    private synchronized void checkExpiredClients() {
        // Extract the collections from view method. Get iterator of the collections
        // object and delete those client entries
        // who session time has passed
        while (this.MAP.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException ex) {

            }
        }
        Iterator<Client> iter = this.MAP.values().iterator();
        while (iter.hasNext()) {
            long time_to_expiry = iter.next().timeToExpiry();
            if (time_to_expiry <= 0)
                iter.remove();
            else if (time_to_expiry < this.MIN_TIME_TO_EXPIRY) {
                this.MIN_TIME_TO_EXPIRY = time_to_expiry;
            }
        }
    }

    @Override
    public void run() {
        while(true){
            this.checkExpiredClients();
            try {
                Thread.sleep(this.MIN_TIME_TO_EXPIRY*60*1000);
            } catch (InterruptedException e) {
            }
        }
    }
}

class Client{
    private String MESSAGE;
    private Socket SOC;
    private Calendar EXPIRY;
    private int ROLE;
    /*
    Client(String message, Socket socket){
        this.MESSAGE = message;
        this.SOC = socket;
        long time = System.currentTimeMillis() + DEFAULT_TIMEOUT*60*1000;
        this.EXPIRY = Calendar.getInstance();
        this.EXPIRY.setTimeInMillis(time);
    }*/
    Client(String message, Socket socket, int role, int timeout){ //timeout is in milliseconds
        this.ROLE = role;
        this.MESSAGE = message;
        this.SOC = socket;
        long time = System.currentTimeMillis() + timeout;
        this.EXPIRY = Calendar.getInstance();
        this.EXPIRY.setTimeInMillis(time);
    }
    public Socket getSocket(){
        return this.SOC;
    }
    public String getMessage(){
        return this.MESSAGE;
    }
    public long timeToExpiry(){
        return (this.EXPIRY.getTimeInMillis() - System.currentTimeMillis());
    }
    public int getRole(){
        return this.ROLE;
    }
}

class MapKeyException extends RuntimeException{
    private String MESSAGE;
    MapKeyException(String message){
        this.MESSAGE = message;
    }
    @Override
    public String getMessage(){
        return this.MESSAGE;
    }
}
