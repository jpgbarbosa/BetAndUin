/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package bets;

import java.util.ArrayList;
import java.util.List;

import common.Constants;


import server.GlobalDataBase;

public class BetManager implements IBetManager {
    private ArrayList<IMatch> matches;
    private BetGenerator gen;
    private GlobalDataBase database;
    
    @SuppressWarnings("unchecked")
	public BetManager(GlobalDataBase clientsStorage) {
        database = clientsStorage;
        /* The next game will start one number after the last game recorded. */
        gen = new BetGenerator(database.getNextGameNumber());
        

        matches = (ArrayList<IMatch>) database.readObjectFromFile("matches.bin");
        
        if(matches==null){
        	/* There was an error reading matches from file. matches will be declared and refreshed*/
        	matches = new ArrayList<IMatch>();
        	refreshMatches();
        }
    }
    
    public List<IMatch> getMatches() {
        return matches;
    }
    
    public Result getResult(IMatch m) {
        // Dont't tell LSilva about this, ok?
        if (m.getHomeTeam().equals("Sporting")) return Result.AWAY;
        if (m.getAwayTeam().equals("Sporting")) return Result.HOME;
        
        return gen.getRandomResult();
    }
    
    public void refreshMatches() {
        matches.clear();
        database.saveObjectToFile("matches.bin", matches);
        
        for(int i=0; i < Constants.NO_GAMES; i++) {
            matches.add(gen.getRandomMatch());
        }
        
        database.saveIntToFile("nextGameNumber.bin", gen.getCounter());
        database.saveObjectToFile("matches.bin", matches);
    }
    
}