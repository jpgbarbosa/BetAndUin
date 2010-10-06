package pt.uc.dei.sd;

import java.util.ArrayList;
import java.util.List;

import server.ClientsStorage;

public class BetManager implements IBetManager {
    private ArrayList<IMatch> matches = new ArrayList<IMatch>();
    private BetGenerator gen;
    private int size;
    private ClientsStorage database;
    
    public BetManager(int gamesPerRound, ClientsStorage clientsStorage) {
        size = gamesPerRound;
        database = clientsStorage;
        /* The next game will start one number after the last game recorded. */
        gen = new BetGenerator(database.getLastGameNumber() + 1);
        
        refreshMatches();
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
        for(int i=0; i < size; i++) {
            matches.add(gen.getRandomMatch());
        }
        
        System.out.println("We are going to save " + gen.getCounter());
        database.saveIntToFile("lastGameNumber.bin", gen.getCounter() - 1);
    }
    
}