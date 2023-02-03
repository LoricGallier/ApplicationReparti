package ricm.nio.babystep1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class WriterAutomata {
    enum State{WRITING_LENGTH, WRITING_MSG, WRITING_IDLE};
    State state = State.WRITING_IDLE ;
    ByteBuffer bb;
    byte[] msg;
    ArrayList<byte[]> pendingMsgs = new ArrayList<>();
    byte[] msg_to_send;
    final int CHUNK_SIZE = 512;

    void sendMsg(byte[] msg){
        pendingMsgs.add(msg);
    }

    void handleWrite(SocketChannel sc) throws IOException{
        
        if(state == State.WRITING_LENGTH){
            
            if (bb.remaining()==0){
                msg_to_send= pendingMsgs.get(0);
                this.bb.putInt(msg_to_send.length);
            }
            
            sc.write(bb);

            if (bb.remaining()==0){
                bb.rewind();
                state = State.WRITING_MSG;
                ByteBuffer.allocate(msg_to_send.length);
                bb.put(msg_to_send);
            }
            
        } 
        if(state == State.WRITING_MSG){
            
            sc.write(bb);

            if(bb.remaining()==0){
                state = State.WRITING_IDLE;
                bb.rewind();
            }

        }
        if(state==State.WRITING_IDLE && pendingMsgs.size()>0){
            state = State.WRITING_LENGTH;
            ByteBuffer.allocate(4);
        }
        
    }
    
}
