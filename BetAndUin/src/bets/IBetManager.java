/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package bets;

import java.util.List;

public interface IBetManager {
    public List<IMatch> getMatches();
    public Result getResult(IMatch m);
    public void refreshMatches();
}