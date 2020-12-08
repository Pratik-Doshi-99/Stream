import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Runnable;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/*
    The purpose of this application is to run on a port on a machine with a static ip address.
    This application will accept socket connections from clients, and connect clients to one another
    If C1 wants to connect with C2, C1 will connect to this server, send his details, a private code
    C2 will also connect to this applcation with the same private code. When C2's request comes, the server
    will process its private code, and look for the directory of private codes it maintains. If it finds the private code
    in this directory, it will connect C2 with the person who sent the private code first. Once connected, C1 and C2 can communicate
    If the application doesnt find the private code C2 sent, it will enter the private code in the directory and wait for the coutner party
    to connect with the same private code.

    At the time of connecting, each client will send: private code, and all details that its coutner party may require
    If a client's private code matches, that client will receive an intimation
    The client will define a timeout period. If no connection is obtained from the counter party in the timeout period,
    the private code will auto expire. If client does not mention this timeout period, the default timeout period shall be 5 mins
*/

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}

class Server {
    ServerSocket SS;
    boolean state;
    Server() {
        this.state = false;
    }

    public void startServer() throws IOException {
        this.SS = new ServerSocket(8095);
        this.state = true;
        while(this.state){
            Socket soc = this.SS.accept();
        }
        




    }

}

class ClientManager implements Runnable{
    private Socket SOC;
    private DataInputStream INPUT;
    private DataOutputStream OUTPUT;
    private int ROLE;
    ClientManager(Socket soc){
        this.SOC = soc;
    }

    @Override
    public void run(){
        try{
            this.INPUT = new DataInputStream(this.SOC.getInputStream());
            this.OUTPUT = new DataOutputStream(this.SOC.getOutputStream());
            this.OUTPUT.writeUTF("Enter your role");
            this.ROLE =this.INPUT.readInt();
            if(this.ROLE == 0){
                //This socket is relating to a client who will be sending data.
                //This client will first send a string that will describe the data he will be sending (in terms of the subscription)
            }
            else if(this.ROLE == 1){
                //This socket is relating to a client who will be receiving data.
                //This client will first send a string that will describe his subscription

            }
            else{
                this.OUTPUT.writeUTF("Invalid Role. Breaking Connection.");
                this.INPUT.close();
                this.OUTPUT.close();
                this.SOC.close();
            }

        }
        catch (Exception e){
            try{
                this.SOC.close();
            } catch(Exception ex){

            }
        }


    }
}