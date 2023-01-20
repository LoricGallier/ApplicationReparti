import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;


class Worker extends Thread {

    DataInputStream dis;
    DataOutputStream dos;
    Socket soc;
    ProdConsBuffer clientBuffer;
    
    Worker (ProdConsBuffer clientBuffer) {
        this.clientBuffer = clientBuffer;
    }

    public void run(){

        while(true){
            try {
                soc = clientBuffer.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Server listening ...");
            // communicate with the client
            try {
                this.dis = new DataInputStream(soc.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } 
            try {
                this.dos = new DataOutputStream(soc.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            String filename = null;
            try {
                filename = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            File f = new File(filename);
            FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				System.out.println("No file found ...");
                try {
                    dos.writeInt(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                e.printStackTrace();
			}
            
            System.out.print("Sending file ... ");
            try {
                dos.writeInt((int) f.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] donnees = new byte[512];
            int nbChunk = (int) ((f.length()-(f.length()%512))/512);
            
            for(int i=0; i<=f.length(); i+=512){
                try {
					fis.read(donnees, 0, 512);
                    for(int j=0; j<donnees.length; j++){
                        dos.write(donnees[j]);
                    }
				} catch (IOException e) {
					e.printStackTrace();
				}
            }

            try {
                donnees = new byte[(int) f.length()%512];
                fis.read(donnees, 0, (int) f.length()%512);
                for(int j=0; j<donnees.length; j++){
                    dos.write(donnees[j]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            System.out.println("OK");
            
            System.out.println("End listening");

            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
                

            try {
                soc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
    }
}