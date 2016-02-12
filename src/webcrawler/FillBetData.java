/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;
import java.util.*;
import org.json.*;
import java.text.*;
import webcrawler.CollectedBetData.*;
/**
 *
 * @author TFSUser
 */
public class FillBetData
{
    public BetData fillData(JSONObject jsonInput, String gName)
    {
        CollectedBetData __instance= new CollectedBetData();
        BetData dataOutput = __instance.new BetData();
        dataOutput.AnimCode = jsonInput.has("AnimCode")?jsonInput.get("AnimCode").toString():"null";//public static String AnimCode;
        dataOutput.AnimParam = jsonInput.has("AnimParam")?jsonInput.get("AnimParam").toString():"null";//public static String AnimParam;
        dataOutput.Before = jsonInput.has("Before")?jsonInput.getInt("Before"):0;//public static Integer Before;
        dataOutput.Champ = jsonInput.has("Champ")?jsonInput.getString("Champ"):"null";//public static String Champ;
        dataOutput.Cont = jsonInput.has("Cont")?jsonInput.getInt("Cont"):0;//public static Integer Cont;
        dataOutput.Events = fillEvents(jsonInput.getJSONArray("Events"), gName);//public static List<Events> Events;
        dataOutput.Finished = jsonInput.has("Finished")?jsonInput.get("Finished").toString():"null";//public static String Finished;
        dataOutput.GameDescr = jsonInput.has("GameDescr")?jsonInput.get("GameDescr").toString():"null";//public static String GameDescr;
        dataOutput.GameType = jsonInput.has("GameType")?jsonInput.getString("GameType"):"null";//public static String GameType;
        dataOutput.GameVid = jsonInput.has("GameVid")?jsonInput.getString("GameVid"):"null";//public static String GameVid;
        dataOutput.Id = jsonInput.getInt("Id");//public static Integer Id;
        dataOutput.IsTourn = jsonInput.has("IsTourn")?jsonInput.getInt("IsTourn"):0;//public static Integer IsTourn;
        dataOutput.LigaId = jsonInput.has("LigaId")?jsonInput.getInt("LigaId"):0;//public static Integer LigaId;
        dataOutput.MainGameId = jsonInput.getInt("MainGameId");//public static Integer MainGameId;
        dataOutput.NameGame = jsonInput.has("NameGame")?jsonInput.getString("NameGame"):"null";//public static String NameGame;
        dataOutput.Num = jsonInput.has("Num")?jsonInput.getInt("Num"):0;//public static Integer Num;
        dataOutput.Opp1 = jsonInput.has("Opp1")?jsonInput.getString("Opp1"):"null";//public static String Opp1;
        dataOutput.Opp1Id = jsonInput.has("Opp1Id")?jsonInput.getInt("Opp1Id"):0;
        dataOutput.Opp2 = jsonInput.has("Opp2")?jsonInput.getString("Opp2"):"null";//public static String Opp2;
        dataOutput.Opp2Id = jsonInput.has("Opp2Id")?jsonInput.getInt("Opp2Id"):0;
        dataOutput.Period = jsonInput.has("Period")?jsonInput.getInt("Period"):0;//public static Integer Period;
        dataOutput.PeriodName = jsonInput.has("PeriodName")?jsonInput.get("PeriodName").toString():"null";//public static String PeriodName;
        dataOutput.Risk = jsonInput.has("Risk")?jsonInput.getInt("Risk"):0;//public static Integer Risk;
        try
        {
//            FillMatchData __FMDinstance = new FillMatchData();
            dataOutput.Scores=fillColScores((JSONObject) jsonInput.get("Scores"));//public Scores Scores;
        }            //public static Scores Scores;
        catch(Exception ex)
        {
                    
        }
        dataOutput.SportId = jsonInput.has("SportId")?jsonInput.getInt("SportId"):0;//public static Integer SportId;
        dataOutput.SportName = jsonInput.has("SportName")?jsonInput.getString("SportName"):"null";//public static String SportName;
        dataOutput.Start = jsonInput.has("Start")?jsonInput.getLong("Start"):0;//public static Integer Start;
        dataOutput.TvChannel = jsonInput.has("TvChannel")?jsonInput.get("TvChannel").toString():"null";//public static String TvChannel;
        dataOutput.VA = jsonInput.has("VA")?jsonInput.get("VA").toString():"null";//public static String VA;
        dataOutput.VI = jsonInput.has("VI")?jsonInput.get("VI").toString():"null";//public static String VI;
        dataOutput.ZonePlay = jsonInput.has("ZonePlay")?jsonInput.getInt("ZonePlay"):0;//public static Integer ZonePlay;
        return dataOutput;
    }
    
    public List<Events> fillEvents(JSONArray jsonInput, String gName)
    {
        List<Events> dataOutput = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        for(int i=0;i<jsonInput.length();i++)
        {
            CollectedBetData __instance = new CollectedBetData();
            Events ev = __instance.new Events();
            dataOutput.add(ev);
            if(jsonInput.get(i).equals(null))
            {
                System.out.println("null at FillBetData");
                continue;
            }
            JSONObject inp = jsonInput.getJSONObject(i);
            Events evOut = dataOutput.get(i);
            try
            {
                evOut.__type = inp.get("__type").toString();
            }
            catch(Exception ex)
                    {
                        evOut.__type = "";
                    }
            evOut.B = inp.get("B").toString();//public static String B;
            evOut.C = inp.getDouble("C");//public static Integer C;
            evOut.G = inp.getInt("G");//public static Integer G;
            evOut.P = inp.getDouble("P");//public static Integer P;
            evOut.Pl = inp.get("Pl").toString();//public static String Pl;
            evOut.T = inp.getInt("T");//public static Integer T;
            try
            {
                evOut.I = inp.getDouble("I");
            }
            catch(Exception ex)
            {
                evOut.I = 0.0;
            }
            evOut.NameGame = gName;
            dataOutput.set(dataOutput.size()-1, evOut);
        }
        return dataOutput;
    }
    
        public Scores fillColScores(JSONObject jsonInput)
    {
        CollectedBetData __instance = new CollectedBetData();
        Scores dataOutput = __instance.new Scores();
        dataOutput.__type = jsonInput.has("__type")?jsonInput.getString("__type"):"null";//public String __type;
        dataOutput.CourseOfPlay = jsonInput.has("CourseOfPlay")?jsonInput.get("CourseOfPlay").toString():"null";//public Boolean CourseOfPlay;
        dataOutput.CurrPeriodStr = jsonInput.has("CurrPeriodStr")?jsonInput.get("CurrPeriodStr").toString():"null";//public String CurrPeriodStr;
        dataOutput.CurrentPeriod = jsonInput.has("CurrentPeriod")?jsonInput.getInt("CurrentPeriod"):0;//public Integer CurrentPeriod;
        dataOutput.FullScore = fillColFScore((JSONObject) jsonInput.get("FullScore"));//public TeamScores FullScore;
        dataOutput.PeriodScores = fillColPScore((JSONArray) jsonInput.get("PeriodScores"));//public List<TeamScores> PeriodScores;
        dataOutput.Statistic = jsonInput.has("Statistic")?jsonInput.get("Statistic").toString():"null";//public Boolean Statistic;
        dataOutput.Info = jsonInput.has("Info")?jsonInput.get("Info").toString():"null";//public String Info;
        dataOutput.Podacha = jsonInput.has("Podacha")?jsonInput.getInt("Podacha"):0;//public Integer Podacha;
        dataOutput.SubScore = jsonInput.has("SubScore")?jsonInput.get("SubScore").toString():"null";//public Boolean SubScore;
        dataOutput.TimeDirection = jsonInput.has("TimeDirection")?jsonInput.getBoolean("TimeDirection"):true;//public Boolean TimeDirection;
        dataOutput.TimeRun = jsonInput.has("TimeRun")?jsonInput.getBoolean("TimeRun"):false;//public Boolean TimeRun;
        dataOutput.TimeSec = jsonInput.has("TimeSec")?jsonInput.getInt("TimeSec"):0;//public Integer TimeSec;
        return dataOutput;
    }
    
    public TeamScores fillColFScore(JSONObject jsonInput)
    {
        CollectedBetData __instance = new CollectedBetData();
        TeamScores dataOutput = __instance.new TeamScores();
        dataOutput.Sc1=jsonInput.getInt("Sc1");
        dataOutput.Sc2=jsonInput.getInt("Sc2");
        return dataOutput;
    }
    
    public List<PeriodScores> fillColPScore(JSONArray jsonInput)
    {
        List<PeriodScores> dataOutput = new ArrayList<PeriodScores>();
        for (int i=0;i<jsonInput.length();i++)
        {
            CollectedBetData __instance = new CollectedBetData();
            PeriodScores psc = __instance.new PeriodScores();
            dataOutput.add(psc);
            JSONObject inp = jsonInput.getJSONObject(i);
            PeriodScores out = dataOutput.get(i);
            out.Key=inp.getInt("Key");
            out.SValue=fillColFScore(inp.getJSONObject("Value"));
            dataOutput.set(i, out);
        }
        return dataOutput;
    }
    
    public List<SubGames> fillColSubG(JSONArray jsonInput)
    {
        List<SubGames> dataOutput = new ArrayList<SubGames>();
        for (int i=0;i<jsonInput.length();i++)
        {
            CollectedBetData __instance = new CollectedBetData();
            SubGames sg = __instance.new SubGames();
            dataOutput.add(sg);
            JSONObject inp = jsonInput.getJSONObject(i);
            SubGames out = dataOutput.get(i);
            out.Blocked =inp.getBoolean("Blocked");//public Boolean Blocked;
            out.GameId = inp.getInt("GameId");//public Integer GameId;
            out.GameType = inp.getString("GameType");//public String GameType;
            out.GameTypeId = inp.getInt("GameTypeId");//public Integer GameTypeId;
            out.GameVid = inp.getString("GameVid");//public String GameVid;
            out.GameVidId = inp.getInt("GameVidId");//public Integer GameVidId;
            out.MainGameId = inp.getInt("MainGameId");//public Integer MainGameId;
            out.NameGame = inp.getString("NameGame");//public String NameGame;
            out.Num = inp.getInt("Num");//public Integer Num;
            out.Period = inp.getInt("Period");//public Integer Period;
            dataOutput.set(i, out);
        }
        return dataOutput;
    }

    public Map<String,SportData> FillSportData(JSONArray jsonInput)
    {
        CollectedBetData cbdInst = new CollectedBetData();
        Map<String,SportData> out = new HashMap();
        SportData sportData;
        JSONObject tempObj;
        for(int i=0;i<jsonInput.length();i++)
        {
            sportData = cbdInst.new SportData();
            tempObj = jsonInput.getJSONObject(i);
            sportData.sportId = tempObj.get("SportId").toString();
            sportData.sportName = tempObj.getString("SportName");
            sportData.leagueData = FillLeagueData(tempObj.getJSONArray("Champs"));
            out.put(sportData.sportId,sportData);
        }
        return out;
    }

    public Map<String,LeagueData> FillLeagueData(JSONArray jsonInput)
    {
        CollectedBetData cbdInst = new CollectedBetData();
        Map<String,LeagueData> out = new HashMap();
        LeagueData leagueData;
        JSONObject tempObj;
        for(int i=0;i<jsonInput.length();i++)
        {
            leagueData = cbdInst.new LeagueData();
            tempObj = jsonInput.getJSONObject(i);
            leagueData.countryId = tempObj.get("CountryId").toString();
            leagueData.countryName = tempObj.getString("CountryName");
            leagueData.leagueId = tempObj.get("LigaId").toString();
            leagueData.leagueName = tempObj.getString("Liga");
            leagueData.sportId = tempObj.get("Sport").toString();
            out.put(leagueData.leagueId,leagueData);
        }
        return out;
    }
}
