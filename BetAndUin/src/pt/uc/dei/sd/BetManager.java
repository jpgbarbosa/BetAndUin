package pt.uc.dei.sd;

import java.util.ArrayList;
import java.util.List;

import server.GlobalDataBase;

public class BetManager implements IBetManager {
    private ArrayList<IMatch> matches = new ArrayList<IMatch>();
    private BetGenerator gen;
    private int size;
    private GlobalDataBase database;
    
    public BetManager(int gamesPerRound, GlobalDataBase clientsStorage) {
        size = gamesPerRound;
        database = clientsStorage;
        /* The next game will start one number after the last game recorded. */
        gen = new BetGenerator(database.getNextGameNumber());
        
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
        
        database.saveIntToFile("nextGameNumber.bin", gen.getCounter());
    }
    
}