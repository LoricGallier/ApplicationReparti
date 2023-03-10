import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client_ChatRoom1 {
    public static void main(String argv[]) throws RemoteException, AlreadyBoundException, NotBoundException {
        IParticipant participantA = new Participant("P_A");
        IParticipant participantB = new Participant("P_B");
        IParticipant participantC = new Participant("P_C");

        Registry registry2 = LocateRegistry.getRegistry("localhost", 9999);
        IChatRoom chatroom = (IChatRoom) registry2.lookup("ChatRoom_1");

        chatroom.connect(participantA);
        chatroom.connect(participantB);
        chatroom.connect(participantC);

        chatroom.send(participantC, "message de C");
    }
}