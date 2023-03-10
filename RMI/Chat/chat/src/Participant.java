import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Participant extends UnicastRemoteObject implements IParticipant {

    String name;

    public Participant(String name) throws RemoteException {
        super();
        this.name = name;
    }

    @Override
    public String name() throws RemoteException {
        return this.name;
    }

    @Override
    public void broadcast(String name, String msg) throws RemoteException {
        System.out.println("from :" + name + "\n msg :" + msg);
        
    }
    
}