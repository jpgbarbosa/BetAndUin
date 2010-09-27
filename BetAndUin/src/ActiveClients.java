import java.util.Hashtable;
import java.util.List;

/* This class holds all the active clients in the current session.
 * For efficiency, we use two structures for keeping the list:
 *     -> HASHTABLE: When we want to access a specific client (e.g. when
 *             we want to terminate a client thread and we have to remove
 *             it from the linked list with all the clients. The second
 *             situation is when we want to send a message to a single
 *             client.
 *             
 *     ->LINKED LIST: The whole list of the active clients. This structure is
 *             used when we want to send a message to the whole list of clients
 *             (chat option). When we want to perform this action, an hashtable
 *             is useless.
 */
public class ActiveClients {
	Hashtable<String, ClientInfo> hashTable = new Hashtable<String, ClientInfo>();
	List <String> messageList;
	
}
