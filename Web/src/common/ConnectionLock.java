/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package common;
/* The lock to check whether the connection is up or down. */
public class ConnectionLock{
	Boolean connectionDown;
	
	public ConnectionLock(){
		connectionDown = true;
	}
	
	public Boolean isConnectionDown(){
		return connectionDown;
	}
	
	public void setConnectionDown(Boolean value){
		connectionDown = value;
	}
}
