import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Client {
    String serverHost = "localhost";
    int serverPort = 4320;
    File fichier;
    String nomFichier = "Loric.txt";
    String nomFichierCopie = "Loric3.txt";


    public Client(String ServerHost, int serverPort) throws UnknownHostException, IOException{
        this.serverHost = ServerHost;
        this.serverPort = serverPort;
    }
    public Client(){

    }

    public void run() throws UnknownHostException, IOException{
        Socket soc = new Socket(serverHost, serverPort);

        // envoie msg
        OutputStream os = soc.getOutputStream();
        DataOutputStream dos = new DataOutputStream (os);
        dos.writeUTF(nomFichier);

        //reception msg
        InputStream is = soc.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        int length = dis.readInt();
        if(length == 0){
            System.out.println("fichier inexistant");
        }
        else{
            byte[] b = new byte[length];
            fichier = new File( nomFichierCopie);
            fichier.createNewFile();
            FileWriter myWriter = new FileWriter(nomFichierCopie);
            System.out.println("debut ecriture fichier");
            dis.readFully(b);
            myWriter.write(new String(b));

            
            myWriter.close();

            System.out.println("fichier creerdtuetyduj"); 
        }
        


        //* Baby step
        // DataInputStream dis = new DataInputStream(is);
        // String date = dis.readUTF();
        // System.out.println("Reponse :" + date);

        soc.close();
    }
    
    public static void main(String[] args) throws Exception {
        int nbrClient = 10;
        ClienThread[] clienThread = new ClienThread[nbrClient];
        for(int i=0; i<nbrClient; i++)
            clienThread[i] = new ClienThread();
        for(int i = 0; i< nbrClient; i++){
            clienThread[i].start();
        }
        

    }
}
