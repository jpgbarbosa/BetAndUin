/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package intraServerCommunication;

import java.util.LinkedList;
import java.util.List;

public class MessagesRepository {
	List <String> messageList;
	
	public MessagesRepository() {
		messageList = new LinkedList<String>();
	}
	
	public void addMsg(String msg){
		messageList.add(msg);
	}
	
	public String getMsg(){
		/* Returns the messages in the head. */
		return messageList.remove(0);	
	}
	
	public int listSize(){
		/* Returns the size of the list. */
		return messageList.size();
	}
}