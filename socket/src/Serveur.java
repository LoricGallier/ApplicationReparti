import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;


public class Serveur {

    static int port = 4320;
    static int backlog = 3;
    DataInputStream dis;
    DataOutputStream dos;
    ProdConsBuffer clientBuffer;


    public Serveur(){
        
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        
        Serveur serv = new Serveur();
        System.out.println("Server running ...");
        ServerSocket listenSoc = null;
        try {
            listenSoc = new ServerSocket(port, backlog);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProdConsBuffer clientBuffer = new ProdConsBuffer(2);
        Worker worker = new Worker(clientBuffer);
        worker.start();

        while(true){
            // wait for a connection request
            Socket soc = null;
            try {
                soc = listenSoc.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientBuffer.put(soc);
        }
        

    }



    
}
