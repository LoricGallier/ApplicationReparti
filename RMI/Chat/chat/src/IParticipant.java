import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IParticipant extends Remote {
    String name() throws RemoteException;
    void broadcast(String name, String msg) throws RemoteException;
}