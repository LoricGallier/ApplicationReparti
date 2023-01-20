import java.net.Socket;

/**
 * Cette interface permet de définir des classes qui ont pour but de gérer de manière synchronisée le dépôt et le retrait d'objets {@link Message}.<br />
 * Les messages sont déposés par des objets {@link Producer}.<br />
 * Les messages sont retirés par des objets {@link Consumer}.
 */
public interface IProdConsBuffer {
	
	/**
	 * Dépose un message dans le buffer.
	 * @param m Le message à déposer dans le buffer
	 * @throws InterruptedException
	 */
	public void put(Socket soc) throws InterruptedException;
	
	/**
	 * Récupère un message dans le buffer, suivant une politique FIFO.
	 * @return Le message le plus ancien dans le buffer
	 * @throws InterruptedException
	 */
	public Socket get() throws InterruptedException;
	
	
	/**
	 * Retourne le nombre de messages présents dans le buffer.
	 * @return Combien de messages sont dans le buffer
	 */
	public int nSocket();
	
	/**
	 * Retourne le nombre total de messages qui ont été déposés dans le buffer depuis sa création.
	 * @return Combien de messages ont été déposés dans le buffer depuis sa création.
	 */
	public int totmsg();
	
}
