import java.net.Socket;

/**
 * Cette classe permet de gérer de manière synchronisée le dépôt et le retrait de messages.<br />
 * Pour ce faire, un buffer stocke les messages et les délivre sur demande et dans l'ordre dans lequel ils ont été déposés.<br />
 * Le buffer implémenté suit la structure d'une file (FIFO).
 */
public class ProdConsBuffer implements IProdConsBuffer {
	
	private final Socket[] buffer;
	private volatile int head, tail,
						 socketProduced;
	
	
	/**
	 * Constructeur par défaut d'un {@link ProdConsBuffer}.
	 * @param bufferSize La taille du buffer utilisé
	 */
	public ProdConsBuffer(int bufferSize) {
		this.buffer = new Socket[bufferSize];
		
		this.head = -1;
		this.tail = 0;
		
		this.socketProduced = 0;
	}
	
	
	
	@Override
	public synchronized void put(Socket soc) throws InterruptedException {
		// Tant que le buffer est plein, les threads producteurs tentant d'y déposer
		// un message sont mis en attente
		while (this.isFull())
			this.wait();
		
		
		// Le message est déposé dans le buffer (tableau de messages), et la structure
		// de file est mise à jour
		
		this.buffer[this.tail] = soc;
		
		if (this.isEmpty())
			this.head = this.tail;
		
		this.tail = (this.tail + 1) % this.getSize();
		
		
		// Incrémentation de la variable indiquant le nombre total de messages déposés
		this.socketProduced++;
		
		System.out.println(Thread.currentThread().toString() + "requette started" );
		//System.out.println("P" + m.getContent());
		
		// On avertit les threads (consommateurs visés) qu'ils peuvent se réveiller
		this.notifyAll();
	}
	
	@Override
	public synchronized Socket get() throws InterruptedException {
		// Tant que le buffer est vide, les threads consommateurs tentant d'y retirer
		// un message sont mis en attente.
		while (this.isEmpty())
			this.wait();
		
		
		// Un message est retiré du buffer (tableau de messages), et la structure
		// de file est mise à jour
		
		final Socket soc = this.buffer[this.head];
		
		if (this.nSocket() == 1)
			this.head = -1;
		else
			this.head = (this.head + 1) % this.getSize();
		
		
		System.out.println(Thread.currentThread().toString() + " Consumed ");
		//System.out.println("C" + message.getContent());
		
		// On avertit les threads (producteurs visés) qu'ils peuvent se réveiller
		this.notifyAll();
		
		// Le message retiré est retourné
		return soc;
	}
	
	
	
	@Override
	public int nSocket() {
		// Si le buffer est vide ou plein (vérifié par des conditions évidentes sur la file), on retourne directement le résultat
		if (this.isEmpty())
			return 0;
		if (this.isFull())
			return this.getSize();
		
		// Cas où la queue de la file est située après la tête dans le tableau
		if (this.tail > this.head)
			return this.tail - this.head;
		// Cas où la queue de la file est située avant la tête dans le tableau
		else
			return this.getSize() + this.tail - this.head; //this.getSize() - (this.head - this.tail)
	}
	
	@Override
	public int totmsg() {
		return this.socketProduced;
	}
	
	
	
	/**
	 * Méthode permettant de connaître la taille du buffer, donnée lors de la création de cet objet {@link ProdConsBuffer}.
	 * @return La taille allouée au buffer
	 */
	public int getSize() {
		return this.buffer.length;
	}
	
	/**
	 * Indique si le buffer est vide ou non.
	 * @return {@code true} si le buffer est vide, {@code false} sinon
	 */
	public boolean isEmpty() {
		return this.head == -1;
	}
	
	/**
	 * Indique si le buffer est plein ou non.
	 * @return {@code true} si le buffer est plein, {@code false} sinon
	 */
	public boolean isFull() {
		return this.head == this.tail;
	}
	
}
