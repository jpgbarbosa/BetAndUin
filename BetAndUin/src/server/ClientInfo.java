package server;

import java.io.Serializable;

/* The class that saves all the information related to the client,
 * including username, password, e-mail address and the number of
 * credits.
 */
public class ClientInfo implements Serializable{
	String username, password, email;
	int credits;
	
	public ClientInfo(String user, String pass, String mail, int noCredits){
		username = user;
		password = pass;
		email = mail;
		credits = noCredits;
	}

	/* The getters for all the client information, 
	 * as well as a setter for the number of credits.
	 */
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}
	
	public void increaseCredits(int credits){
		this.credits += credits;
	}
	
}
