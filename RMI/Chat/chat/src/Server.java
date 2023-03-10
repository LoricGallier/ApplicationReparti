import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server{
    public static void main(String argv[]) throws RemoteException, NotBoundException, AlreadyBoundException {
        Registry registry = LocateRegistry.createRegistry(9999);
        
        IChatRoom chatRoom1 = new ChatRoom("ChatRoom_1");
        registry.bind("ChatRoom_1", chatRoom1);

        IChatRoom chatRoom2 = new ChatRoom("ChatRoom_2");
        registry.bind("ChatRoom_2", chatRoom2);
    }
}