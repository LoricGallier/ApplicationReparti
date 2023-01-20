import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class ClienThread extends Thread {
    String serverHost = "localhost";
    int serverPort = 4320;
    File fichier;
    String nomFichier = "Loric.txt";
    String nomFichierCopie = "Loric";
    String extension = ".txt";

    public ClienThread(){
    }


    
    public void run() {
        Socket soc = null;
        try {
            soc = new Socket(serverHost, serverPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // envoie msg
        OutputStream os = null;
		try {
			os = soc.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        DataOutputStream dos = new DataOutputStream (os);
        try {
			dos.writeUTF(nomFichier);
		} catch (IOException e) {
			e.printStackTrace();
		}

        //reception msg
        InputStream is = null;
        try {
            is = soc.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataInputStream dis = new DataInputStream(is);
        int length = 0;
        try {
            length = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(length == 0){
            System.out.println("fichier inexistant");
        }
        else{
            byte[] b = new byte[512];
            fichier = new File( nomFichierCopie + this.getId() + extension);

            try {
                fichier.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileWriter myWriter = null;
			try {
				myWriter = new FileWriter(nomFichierCopie + this.getId() + extension);
			} catch (IOException e) {
				e.printStackTrace();
			}
            System.out.println("debut ecriture fichier");
            for(int i =0; i<length; i+=512){
                try {
                    dis.readFully(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    myWriter.write(new String(b));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("i :" + i +"\n");
            }


            

            // Création fichier

            
            try {
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("fichier creer"); 
        }
    }
}
