import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ChatRoom extends UnicastRemoteObject implements IChatRoom {

    String name;
    ArrayList<IParticipant> participantList = new ArrayList<>();

    public ChatRoom(String name) throws RemoteException{
        super();
        this.name = name;
    }

	@Override
	public String name() throws RemoteException {
		return this.name;
	}

	@Override
	public void connect(IParticipant p) throws RemoteException {
		this.participantList.add(p);
	}

	@Override
	public void leave(IParticipant p) throws RemoteException {
		this.participantList.remove(p);
	}

	@Override
	public String[] who() throws RemoteException {
		String[] participants = new String[this.participantList.size()];
        for(int i=0; i<this.participantList.size(); i++)
            participants[i] = this.participantList.get(i).name();
        return participants;
	}

	@Override
	public void send(IParticipant p, String msg) throws RemoteException {
		for(IParticipant part : this.participantList){
            part.broadcast(p.name(), msg);
        }
	}

}