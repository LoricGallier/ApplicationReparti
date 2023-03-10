import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatRoom extends Remote {
    String name()throws RemoteException;
    void connect(IParticipant p) throws RemoteException;
    void leave(IParticipant p) throws RemoteException;
    String[] who() throws RemoteException;
    void send(IParticipant p, String msg) throws RemoteException;
}