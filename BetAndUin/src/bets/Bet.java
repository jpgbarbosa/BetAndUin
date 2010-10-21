package bets;

import java.io.Serializable;

public class Bet implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int credits;
	private int gameNumber;
	private String bet;
	private String user;
		
	public Bet( String user, int gameNumber, String bet, int credits){
		this.credits=credits;
		this.gameNumber=gameNumber;
		this.bet=bet;
		this.user=user;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public int getGameNumber() {
		return gameNumber;
	}

	public void setGameNumber(int gameNumber) {
		this.gameNumber = gameNumber;
	}

	public String getBet() {
		return bet;
	}

	public void setBet(String bet) {
		this.bet = bet;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
