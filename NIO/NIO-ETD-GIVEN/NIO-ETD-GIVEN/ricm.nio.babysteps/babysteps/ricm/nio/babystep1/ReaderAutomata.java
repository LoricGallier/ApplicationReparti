package ricm.nio.babystep1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ReaderAutomata {
    enum State {READING_LENGTH, READING_MSG} ;
    State state = State.READING_LENGTH ;
    ByteBuffer buffer; 
    byte[] msg;

    void process_msg(){
        if (state == State.READING_LENGTH){
            if(msg != null)
                System.out.println("NioClient received msg : "+msg.length);
        }
    }

    void handleRead(SocketChannel sc) throws IOException{
        int length;
        byte[] data = new byte[buffer.limit()];

        //<read the length>
        //<read the message knowing that it is composed of length bytes>
        if (state == State.READING_LENGTH) {   

            sc.read(buffer); 

            if (buffer.position() > 3){
                buffer.rewind();
                length = buffer.getInt(); 
                data = new byte[length];
                state = State.READING_MSG;
            }                     
            //<continue reading the length>
            //<if the four bytes composing the length have been read,
            //allocate a buffer to read the msg and go to READING-MSG state>
            
        } else if (state == State.READING_MSG) {

            sc.read(buffer);

            if (buffer.remaining() == 0){
                buffer.get(data);
                state = State.READING_LENGTH;
                this.msg = data;
            }
            //<continue reading the msg>
            //<if all bytes composing the msg have been read,
            //go back to READING-LENGTH state
            //and save or return the msg read>
        }
    }
}
