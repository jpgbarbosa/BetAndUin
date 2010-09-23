package pt.uc.dei.sd;

import java.io.Serializable;

public class Match implements IMatch, Serializable {
    private String home;
    private String away;
    private int code;
    
    public Match(int code, String t1, String t2) {
        home = t1;
        away = t2;
        this.code = code;
    }
    
    public String getCode() {
        return ""+code;
    }
    
    public String getHomeTeam() {
        return home;
    }
    
    public String getAwayTeam() {
        return away;
    }
    
    public String toString() {
        return String.format("%s vs %s", home, away);
    }
}