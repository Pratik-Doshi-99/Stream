import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Runnable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.SocketException;

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
        Server server = new Server();

    }
}

class Server extends Thread{
    private ServerSocket SS;
    private boolean state;
    static final PrivateCodeManager CODE_MANAGER = new PrivateCodeManager();
    Server() {
        super("Server_Thread");
        this.state = true;
        this.start();
    }

    @Override
    public void run(){
        this.startServer(8095);
    }

    public void startServer(int port){
        try{
            this.SS = new ServerSocket(port);
            //this.state = true; unnecessary as it is already defined in constructor
            while(this.state){
                try{
                    Thread t = new Thread(new ClientManager(this.SS.accept()));
                    t.start();
                } catch(SocketException e){
                    System.out.println("Socket exception occured: " + e.getMessage());
                } catch(IOException e){
                    System.out.println("IOException Occured: " + e.getMessage());
                } catch(Exception e){
                    System.out.println("An unknown exception Occured: " + e.getMessage());
                }
            }
            System.out.println("The server was stopped");
        }catch(IOException e){
            System.out.println("An unknown exception Occured: " + e.getMessage());
        }
    }

    public void stopServer(){
        this.state = false;
        try{
            this.SS.close();
        } catch(Exception e){
            System.out.println("An Exception Occured: " + e.getMessage());
        }
    }

}

class ClientManager implements Runnable{
    private Socket SOC;
    private DataInputStream INPUT;
    private DataOutputStream OUTPUT;
    ClientManager(Socket soc){
        this.SOC = soc;
    }

    @Override
    public void run(){
        try{
            this.INPUT = new DataInputStream(this.SOC.getInputStream());
            this.OUTPUT = new DataOutputStream(this.SOC.getOutputStream());
            this.OUTPUT.writeUTF("Send your private code");
            String code = this.INPUT.readUTF();
            this.OUTPUT.writeUTF("Send your role");
            int role = this.INPUT.readInt();
            if(role != 0 && role != 1 ){
                this.OUTPUT.writeUTF("Invalid Role. Breaking Connection.");
                this.INPUT.close();
                this.OUTPUT.close();
                this.SOC.close();
                return;
            }
            //the rest of the validation will be managed by the Connection object

            boolean code_exists = Server.CODE_MANAGER.exists(code);
            if(code_exists){
                Client client = Server.CODE_MANAGER.extract(code,false);
                if(role == client.getRole()){
                    this.OUTPUT.writeUTF("Role Request Rejected. Breaking Connection.");
                    this.INPUT.close();
                    this.OUTPUT.close();
                    this.SOC.close();
                    return;
                }



            }

            /*

                IMPORTANT: START FROM HERE
                Should the PRivateCodeManager be put inside the connection object?
                    -How should work be divided between the current function and the functions of Connection object?


            */

            //This socket is relating to a client who will be sending data.
            //This client will first send a string that will describe the data he will be sending (in terms of the subscription)
        

            //This socket is relating to a client who will be receiving data.
            //This client will first send a string that will describe his subscription


        }
        catch (Exception e){
            try{
                if (this.INPUT != null)
                this.INPUT.close();
                if (this.OUTPUT != null)
                this.OUTPUT.close();
                this.SOC.close();
            } catch(Exception ex){
                System.out.println("An unknown exception Occured: " + ex.getMessage());
            }
        }


    }
}