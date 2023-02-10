package ricm.nio.babystep1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WriterAutomata {
    enum State{WRITING_LENGTH, WRITING_MSG, WRITING_IDLE, WRITING_INIT_LENGTH};
    State state = State.WRITING_IDLE ;
    public ByteBuffer bb;
    byte[] msg;
    ArrayList<byte[]> pendingMsgs = new ArrayList<>();
    byte[] msg_to_send = {};

    public void sendMsg(byte[] msg){
        pendingMsgs.add(msg);
    }

    public void handleWrite(SocketChannel sc) throws IOException{

        if(state == State.WRITING_INIT_LENGTH){

            msg_to_send= pendingMsgs.get(0);
            pendingMsgs.remove(0);
            bb.position(0);
            this.bb.putInt(msg_to_send.length);
            bb.rewind();
            state = State.WRITING_LENGTH;
        }
        
        if(state == State.WRITING_LENGTH){
            
            
            sc.write(bb);

            if (bb.remaining()==0){
                bb.rewind();
                state = State.WRITING_MSG;
                if(msg_to_send.length !=0) System.out.println("on envoie un message de taille "+msg_to_send.length);
                bb = ByteBuffer.wrap(msg_to_send);
                System.out.println("msg envoyé : "+new String(msg_to_send, StandardCharsets.UTF_8));

            }
            
        } 
        if(state == State.WRITING_MSG){
            
            sc.write(bb);

            if(bb.remaining()==0){
                state = State.WRITING_IDLE;
            }

        }
        if(state==State.WRITING_IDLE && pendingMsgs.size()>0){
            state = State.WRITING_INIT_LENGTH;
            bb = ByteBuffer.allocate(4);
        }
        
    }
    
}
