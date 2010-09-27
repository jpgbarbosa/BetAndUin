package messages;
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

/*

//Create the list
List list = new LinkedList();    // Doubly-linked list
list = new ArrayList();          // List implemented as growable array

// Append an element to the list
list.add("a");

// Insert an element at the head of the list
list.add(0, "b");

// Get the number of elements in the list
int size = list.size();          // 2

// Retrieving the element at the end of the list
Object element = list.get(list.size()-1);   // a

// Retrieving the element at the head of the list
element = list.get(0);                      // b

// Remove the first occurrence of an element
boolean b = list.remove("b");      // true
b = list.remove("b");              // false

// Remove the element at a particular index
element = list.remove(0);          // a

*/