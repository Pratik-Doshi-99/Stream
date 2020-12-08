import java.net.Socket;
/*This intrface is meant to highlight the process that ewstablishes the connection between client sockets
    Now connections could be one sender one receiver, one sender, multiple receivers or even a multiple senders model
    For the current version, it will be a simple one sender one receiver model. Any such model needs to be represented as a class
    that will implement the connection interface.

    The interface is put here so that connection models can be modified without much impact to the main code
*/
public interface Connection{
    public void addParameterString(String parameters);//to set certain basic configurations of the connection like default 
    public void addReceiver(Socket s) throws InvalidNodeException; //
    public void addSender(Socket s) throws InvalidNodeException;;
    public void connect();
}

class InvalidNodeException extends Exception{ 
    /*
        Exception thrown when a node (sender/receiver) cannot be added or some error exists
        In case of a connection model that is one-one. This exception is thrown when a second sender/receiver is added
        In case of a connection model that does not support nodes to be added after the connection has started, this exception is
        thrown if an attempt to add a sender/receiver is made after the connection has started
    */

    private static final long serialVersionUID = 8744091732977844205L;
    String MESSAGE;
    InvalidNodeException(String message){
        this.MESSAGE = message;
    }
    @Override
    public String getMessage(){
        return this.MESSAGE;
    }

}

class O2OConnection implements Connection{ //One to One Connection
    O2OConnection(){

    }
    public void addParameterString(String parameters){

    }
    public void addReceiver(Socket s)throws InvalidNodeException{

    }
    public void addSender(Socket s)throws InvalidNodeException{

    }
    public void connect(){

    }

}