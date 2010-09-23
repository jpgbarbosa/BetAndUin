package pt.uc.dei.sd;

import java.util.List;

public interface IBetManager {
    public List<IMatch> getMatches();
    public Result getResult(IMatch m);
    public void refreshMatches();
}