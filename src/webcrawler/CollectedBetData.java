/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;
import java.util.*;

/**
 *
 * @author TFSUser
 */
public class CollectedBetData 
{
    public class BetData
    {
        public String AnimCode;
        public String AnimParam;
        public Integer Before;
        public String Champ;
        public Integer Cont;
        public List<Events> Events;
        public String Finished;
        public String GameDescr;
        public String GameType;
        public String GameVid;
        public Integer Id;
        public Integer IsTourn;
        public Integer LigaId;
        public Integer MainGameId;
        public String NameGame;
        public Integer Num;
        public String Opp1;
        public Integer Opp1Id;
        public String Opp2;
        public Integer Opp2Id;
        public Integer Period;
        public String PeriodName;
        public Integer Risk;
        public Scores Scores;
        public Integer SportId;
        public String SportName;
        public Long Start;
        public String TvChannel;
        public String VA;
        public String VI;
        public Integer ZonePlay;
    }
    
    public class Events
    {
        public String __type;
        public String B;
        public Double C;
        public Integer G;
        public Double P;
        public String Pl;
        public Integer T;
        public Double I;
        public String NameGame;
        // Индекс значения- Т, само значение - С
        // T: 1 - П1, 2 - Х, 3 - П2, 4 - П1Х, 5- П1П2, 6 - ХП2, 7 - Фора1, 8 - Фора2, 9 - ТоталБ, 10 - ТоталМ.
        // Значение коэффициента хранится в С.
        // В случае форы в Р хранится фора, в случае тотала - тотал.
    }
    
    public class Scores
    {
        public String __type;
        public String CourseOfPlay;
        public String CurrPeriodStr;
        public Integer CurrentPeriod;
        public TeamScores FullScore;
        public List<PeriodScores> PeriodScores;
        public String Statistic;
        public String Info;
        public Integer Podacha;
        public String SubScore;
        public Boolean TimeDirection;
        public Boolean TimeRun;
        public Integer TimeSec;
    }
    
    public class TeamScores
    {
        public Integer Sc1;
        public Integer Sc2;
    }
    
    public class PeriodScores
    {
        public Integer Key;
        public TeamScores SValue;
    }
    
    public class SubGames
    {
        public Boolean Blocked;
        public Integer GameId;
        public String GameType;
        public Integer GameTypeId;
        public String GameVid;
        public Integer GameVidId;
        public Integer MainGameId;
        public String NameGame;
        public Integer Num;
        public Integer Period;
    }

    public class SportData
    {
        public String sportId;
        public String sportName;
        public Map<String, LeagueData> leagueData;
    }

    public class LeagueData
    {
        public String countryId;
        public String countryName;
        public String leagueId;
        public String leagueName;
        public String sportId;
    }
}
