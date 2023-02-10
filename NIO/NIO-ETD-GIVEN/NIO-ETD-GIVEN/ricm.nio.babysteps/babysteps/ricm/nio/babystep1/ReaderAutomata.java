package ricm.nio.babystep1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ReaderAutomata {
    enum State {READING_LENGTH, READING_MSG} ;
    State state = State.READING_LENGTH ;
    ByteBuffer buffer = ByteBuffer.allocate(4); 
    ByteBuffer lengthbuf = ByteBuffer.allocate(4); 
    byte[] msg;



    public void process_msg(){
        System.out.println("NioClient received msg : "+msg.length);
        System.out.println("msg recu : "+new String(this.msg, StandardCharsets.UTF_8));

        
    }

    public void handleRead(SocketChannel sc) throws IOException{
        int length;
        byte[] data = new byte[buffer.limit()];

        //<read the length>
        //<read the message knowing that it is composed of length bytes>
        if (state == State.READING_LENGTH) {   

            sc.read(lengthbuf); 

            if (lengthbuf.remaining() == 0){
                lengthbuf.rewind();
                length = lengthbuf.getInt(); 
                lengthbuf.rewind();
                buffer = ByteBuffer.allocate(length);
                //if(length!=0) System.out.println("taille msg recu:" + length);
                data = new byte[length];
                state = State.READING_MSG;
            }                     
            //<continue reading the length>
            //<if the four bytes composing the length have been read,
            //allocate a buffer to read the msg and go to READING-MSG state>
            
        } else if (state == State.READING_MSG) {

            sc.read(buffer);

            if (buffer.remaining() == 0){
                data = new byte[buffer.limit()];
                buffer.rewind();
                buffer.get(data);
                buffer = ByteBuffer.allocate(4);
                state = State.READING_LENGTH;
                this.msg = data;
                process_msg();
            }
            //<continue reading the msg>
            //<if all bytes composing the msg have been read,
            //go back to READING-LENGTH state
            //and save or return the msg read>
        }
    }
}
