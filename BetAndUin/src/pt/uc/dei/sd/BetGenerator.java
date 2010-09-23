package pt.uc.dei.sd;

import java.util.Random;

public class BetGenerator {
    
    private int counter = 0;
    
    private String[] teams = new String[]{ 
        "Benfica", "Porto", "Braga", "Sporting",
        "Nacional", "V. Guimaraes", "P. Ferreira", "Olhanense",
        "Academica", "Beira-Mar", "V. Setubal", "Naval", "Leiria",
        "Portimonense", "Rio Ave", "Maritimo"
    };
    
    public Result getRandomResult() {
        int prob = new Random().nextInt(100);
        if (prob < 40) {
            return Result.HOME; // 40% probability
        } else if (prob < 80) {
            return Result.AWAY; // 40% probability
        } else {
            return Result.TIE; // 20% probability
        }
    }
    
    public Match getRandomMatch() {
        String home = this.getRandomTeam();
        String away = null;
        
        // make sure the team isn't playing against itself
        while (away == null || away.equals(home)) {
            away = this.getRandomTeam();
        }
        
        return new Match(counter++, home, away);
        
    }
    
    private String getRandomTeam() {
        int index = new Random().nextInt(teams.length);
        return teams[index];
    }
    
    public static Match createRandomMatch() {
        BetGenerator betgen = new BetGenerator();
        return betgen.getRandomMatch();
    }
    
    public static void main(String[] args) {
        BetGenerator bet = new BetGenerator();
        Match m = bet.getRandomMatch();
        System.out.println(m);
    }
}