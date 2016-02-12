/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;

import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;
import webcrawler.CollectedBetData.BetData;
import webcrawler.CollectedBetData.Events;
import webcrawler.CollectedBetData.Scores;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Saendra
 */
public class FormDataString 
{
    public Map<Integer,BetData> bdStr = new HashMap(), lineStr = new HashMap();
    public Map<String, CollectedBetData.SportData> sportData = new HashMap();
    private static volatile FormDataString fdsInst;
    public String crwlrId = "05", filepath =  "C:\\WCLogs\\el\\" /* "/WClogs/Eventlogs/" */;
    public List<Integer> pointOddsA = Arrays.asList(2,10,15,17,62,109,133,138,168,182,224,262,297,299,307,309,311,329,331,333,339,341,357,361,367,369,371,373,387,389,391,413,687,689,691,693,695,697,699,701,703,705,707,709,711,713,715,717,719,721,723,735,737,747,749,751,753,755,757,793,795),
                 pointOddsB = Arrays.asList(148,152,345);
    public int i = 100;
    public List<Integer> sportIds;
    
    public static FormDataString getInstance()
    {
        FormDataString locdcInst = fdsInst;
        if(locdcInst==null)
            synchronized(FormDataString.class)
            {
                locdcInst=fdsInst;
                if(locdcInst==null)
                {
                    fdsInst=locdcInst=new FormDataString();
                }
            }
        return locdcInst;
    }
    
    public void FormDataStringO(Boolean isMain, BetData bdInput)
    {
        File logs = new File(filepath);
        if(!logs.exists())
        {
            logs.mkdir();
        }
        DataSenderDB dsdbInst = DataSenderDB.getInstance();
        PrintWriter logWrt = null;
        try
        {
            logWrt = new PrintWriter(new BufferedWriter(new FileWriter(filepath+(isMain?bdInput.Id.toString():bdInput.MainGameId.toString())+".txt",true)));
        }
        catch(IOException ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.FormDataStringO");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        String tmpStr;
        List<String> scevLst = new ArrayList();        
        try
        {
            if(isMain&&!this.bdStr.containsKey(bdInput.Id))
            {
                tmpStr = formInitString(bdInput);
                dsdbInst.sendDataLoop(tmpStr);
                logWrt.println(sdf.format(new Date()) + " " + (isMain?bdInput.Id.toString():bdInput.MainGameId.toString()) + tmpStr);   // Initialize event.
            }
        }
        catch(Exception ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.formDataStringo (while initializing event)");
        }
        if(bdInput.Scores != null)
        {
            scevLst = formScString(bdInput.Scores, bdInput.PeriodName, isMain?bdInput.Id:bdInput.MainGameId);  // Form scores strings.
        }
        for (String aScevLst : scevLst)
        {
            dsdbInst.sendDataLoop(aScevLst);
            logWrt.println(sdf.format(new Date()) + " " + (isMain?bdInput.Id.toString():bdInput.MainGameId.toString()) + aScevLst);
        }
        Boolean isEp = false;   // Shows if certain odd is already added and didn't change.
        for(int i=0;i<bdInput.Events.size();i++)    // Form odd strings.
        {
            try
            {
                if(this.bdStr.size()!=0&&this.bdStr.containsKey(bdInput.Id))
                {
                    List<Events> evevlst = this.bdStr.get(bdInput.Id).Events;
                    for (int j = 0; j < evevlst.size(); j++)
                    {
                        Events evev = evevlst.get(j);
                        if (bdInput.Events.get(i).T.equals(evev.T) && bdInput.Events.get(i).P.equals(evev.P))
                        {
                            if (bdInput.Events.get(i).C.equals(evev.C) && bdInput.Events.get(i).B.equals(evev.B))
                            {
                                isEp = true;
                                break;
                            }
                        }
                    }

                }
            }
            catch(Exception ex)
            {
                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.formDataStringO (while forming events' strings - 1)");
            }
            if(!isEp)
            {
                try
                {
                    if(!BetTypes.betTypesList.has(bdInput.Events.get(i).G.toString())||
                            !BetTypes.betTypesList.getJSONObject(bdInput.Events.get(i).G.toString()).getJSONObject("B").has(bdInput.Events.get(i).T.toString())||
                            BetTypes.betTypesList.getJSONObject(bdInput.Events.get(i).G.toString()).getString("G").equals("")||
                            BetTypes.betTypesList.getJSONObject(bdInput.Events.get(i).G.toString()).getJSONObject("B").getJSONObject(bdInput.Events.get(i).T.toString()).getString("N").equals(""))
                    {
                        continue;
                    }
                    tmpStr = formEvString(bdInput.Events.get(i),isMain?bdInput.Id:bdInput.MainGameId,false);
                    dsdbInst.sendDataLoop(tmpStr);
                    logWrt.println(sdf.format(new Date()) + " " + (isMain?bdInput.Id.toString():bdInput.MainGameId.toString()) + tmpStr);
                }
                catch(Exception ex)
                {
                    Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.formDataStringO (while forming events' strings - 2)");
                }
            }
            isEp = false;
        }
        isEp = false;   // Check if odd still in list.
        if(this.bdStr.get(bdInput.Id)!=null&&
                this.bdStr.get(bdInput.Id).Events!=null)
        {
            for(Events evBd : this.bdStr.get(bdInput.Id).Events)
            {
                for(Events evIn : bdInput.Events)
                {
                    if(evIn.T.equals(evBd.T) && evIn.P.equals(evBd.P))
                    {
                        isEp = true;
                        break;
                    }
                }
                if(!isEp)
                {
                    if(!BetTypes.betTypesList.has(evBd.G.toString())||
                            !BetTypes.betTypesList.getJSONObject(evBd.G.toString()).getJSONObject("B").has(evBd.T.toString())||
                            BetTypes.betTypesList.getJSONObject(evBd.G.toString()).getString("G").equals("")||
                            BetTypes.betTypesList.getJSONObject(evBd.G.toString()).getJSONObject("B").getJSONObject(evBd.T.toString()).getString("N").equals(""))
                    {
                        continue;
                    }
                    tmpStr = formEvString(evBd,isMain?bdInput.Id:bdInput.MainGameId,true);
                    dsdbInst.sendDataLoop(tmpStr);
                    logWrt.println(sdf.format(new Date()) + " " + (isMain?bdInput.Id.toString():bdInput.MainGameId.toString()) + tmpStr);
                }
                isEp = false;
            }
        }
        logWrt.close();
        dsdbInst = null;
        tmpStr = null;
        bdStr.put(bdInput.Id, bdInput);
    }
    
    private String formInitString(BetData bdInput) throws Exception   // Add event to database.
    {
        Date now = new Date();
        long l = (bdInput.Start-10800)*1000;
        now.setTime(l);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        String strOut = bdInput.Id.toString()+" exec ";
        SimpleDateFormat evf = new SimpleDateFormat("yyyyMMdd HH:mm");
        strOut = strOut + "ls_UpdateEvents "+ crwlrId +"," +
                        bdInput.SportId.toString()+","+
                        "\'"+bdInput.SportName+"\'"+","+
                        bdInput.LigaId+","+
                        "\'"+bdInput.Champ.replaceAll("\'", "\'\'")+"\'"+","+
                        "NULL,"+
                        bdInput.Id+","+
                        "\'" + evf.format(now) + "\',"+
                        "NULL,"+
                        "\'"+bdInput.Opp1.replaceAll("\'", "\'\'")+"\'"+","+
                        "NULL,"+
                        "\'"+bdInput.Opp2.replaceAll("\'", "\'\'")+"\'";
        return strOut;
    }
    
    private List<String> formScString(Scores scInput, String perName, Integer evId)   // Update event and scores.
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        String strIn = evId.toString()+" exec ", tempStr;
        List<String> strOut = new ArrayList();
        CollectedBetData cbdInst = new CollectedBetData();
        if((!this.bdStr.containsKey(evId))||
                (this.bdStr.containsKey(evId)&&
                    !scInput.FullScore.equals(this.bdStr.get(evId).Scores.FullScore)))  // Update full score.
        {
            strOut.add(strIn +
                        "[ls_UpdateScores] " +
                        evId.toString() + "," +
                        crwlrId + "," +
                        "\'score\'," +
                        scInput.FullScore.Sc1 + "," +
                        scInput.FullScore.Sc2 + "," +
                        "\'Match\'");
        }
        try
        {
        for(int i=0;i<scInput.PeriodScores.size();i++)   // Update period scores.
        {
            if((!this.bdStr.containsKey(evId))||
                (this.bdStr.containsKey(evId)&&
                    this.bdStr.get(evId).Scores.PeriodScores.size()<=i)||    
                (this.bdStr.containsKey(evId)&&
                    this.bdStr.get(evId).Scores.PeriodScores.size()>i&&
                        this.bdStr.get(evId).Scores.PeriodScores.get(i).SValue!=null&&
                            !scInput.PeriodScores.get(i).SValue.equals(this.bdStr.get(evId).Scores.PeriodScores.get(i).SValue)))
            {
                strOut.add(strIn +
                        "[ls_UpdateScores] " +
                        evId.toString() + "," +
                        crwlrId + "," +
                        "\'score\'," +
                        scInput.PeriodScores.get(i).SValue.Sc1 + "," +
                        scInput.PeriodScores.get(i).SValue.Sc2 + "," +
                        "\'" + (i+1) + " " + perName + "\'");
            }
        }
        }
        catch(Exception ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.formScString");
        }
        if(!scInput.SubScore.equals("null")&&
                (!this.bdStr.containsKey(evId)||
                (this.bdStr.containsKey(evId)&&
                    !scInput.SubScore.equals(this.bdStr.get(evId).Scores.SubScore))))
        {
            String[] SSc = scInput.SubScore.split(",");
            for(int i = 0;i<SSc.length;i++)
            {
                SSc[i] = SSc[i].replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\\"", "").replaceAll("Sc", "").substring(2).replaceAll("ADV", "50");
            }
            strOut.add(strIn+
                        "[ls_UpdateScores] " +
                        evId.toString() + "," +
                        crwlrId + "," +
                        "\'score\'," +
                        SSc[0] + "," +
                        SSc[1] + "," +
                        "\'gamescore - " + scInput.CurrentPeriod.toString() + " " + perName + "\'");
        }
        if((!this.bdStr.containsKey(evId))||
                (this.bdStr.containsKey(evId)&&
                    (!scInput.CurrentPeriod.equals(this.bdStr.get(evId).Scores.CurrentPeriod)||
                        !scInput.Podacha.equals(this.bdStr.get(evId).Scores.Podacha)||
                        !scInput.TimeSec.equals(this.bdStr.get(evId).Scores.TimeSec)||
                        !scInput.TimeRun.equals(this.bdStr.get(evId).Scores.TimeRun))))
        {
            tempStr = strIn +
                        "[ls_UpdateEvent] " +
                        evId.toString() + "," +
                        crwlrId + "," +
                        scInput.CurrentPeriod.toString() + "," +
                        scInput.TimeSec.toString() + "," +
                        scInput.Podacha.toString() + ",";
            if(!scInput.TimeRun)
            {
                tempStr = tempStr + "4";
            }
            else
            {
                tempStr = tempStr + "0";
            }
            strOut.add(tempStr);
        }
        return strOut;
    }
    
    private String formEvString(Events evIn, Integer evId, Boolean lock)   // Update odds.
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        String strOut = evId.toString()+" exec ";
        JSONObject btIn;
        if(Boolean.valueOf(evIn.B))
        {
            lock = true;
        }
        strOut = strOut + "ls_UpdateOdds " +
                        evId.toString() + "," +
                        crwlrId + "," +
                        "\'";
        if(BetTypes.betTypesList.has(evIn.G.toString()))
        {
            btIn = BetTypes.betTypesList.getJSONObject(evIn.G.toString());
            if(btIn.getJSONObject("B").has(evIn.T.toString()))
            {
                strOut = strOut + formBetGroup(btIn.getString("G"),evIn.G, evIn.P) +
                        (evIn.NameGame.equals("")?"":(" - "+evIn.NameGame)) +
                        "\'," +
                        "\'" + formBetName(btIn.getJSONObject("B").getJSONObject(evIn.T.toString()).getString("N"),evIn.G, evIn.T, evIn.P);
            }
            else
            {
               // return("");
                strOut = strOut + formBetGroup(btIn.getString("G"),evIn.G, evIn.P) + "\'," +     // debug. Bettype is not avaliable or incorrect.
                        "\'" + evIn.T;
            }
        }
        else
        {
           // return("");
            strOut = strOut + evIn.G + "\'," + // debug. Bettype and betgroup are incorrect or not avaliable.
                        "\'" + evIn.T;
        }
        strOut = strOut + "\'," +
                        "1," +
                        "0," +
                        ((Boolean.valueOf(evIn.B)||lock)?"1":"0") + "," +
                        "\'" + evIn.C + "\'," +
                        formPoints(evIn);
        return strOut;
    }
    
    public void LockEvent(Boolean isMain, Integer evId)
    {
        File logs = new File(filepath);
        if(!logs.exists())
        {
            logs.mkdir();
        }
        DataSenderDB dsdbInst = DataSenderDB.getInstance();
        try
        {
            PrintWriter logWrt = new PrintWriter(new BufferedWriter(new FileWriter(filepath + evId.toString() + ".txt", true)));
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
            String strOut;
            if (bdStr.containsKey(evId)) {
                BetData bdLast = bdStr.get(evId);
                for (Events ev : bdLast.Events) {
                    strOut = formEvString(ev, bdLast.MainGameId, true);
                    dsdbInst.sendDataLoop(strOut);
                    logWrt.println(sdf.format(new Date()) + " " + evId.toString() + strOut);
                }
            }
            if (isMain) {
                strOut = evId.toString() + " exec " +
                        "[ls_UpdateEvent] " +
                        evId.toString() + "," +
                        crwlrId + "," +
                        "0" + "," +
                        "NULL" + "," +
                        "NULL" + "," +
                        "256";
                dsdbInst.sendDataLoop(strOut);
                logWrt.println(sdf.format(new Date()) + " " + evId.toString() + strOut);
            }
            logWrt.close();
        }
        catch(Exception ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.lockEvent");
        }
        if(bdStr.containsKey(evId))
        {
            bdStr.remove(evId);
        }
    }
    
    private String formPoints(Events evInput)   // Form points for handicap and total bettypes.
    {
        if(pointOddsA.contains(evInput.G))
        {
            return((evInput.P==0)?"\'0\'":("\'"+evInput.P.toString()+"\'"));
        }
        else
        {
            if(pointOddsB.contains(evInput.G))
            {
                Integer temp1 = evInput.P.intValue();
                Double temp2 = temp1.doubleValue()/100;
                return((temp1==0)?"\'0\'":"\'"+(temp2)+"\'");
            }
            else
            {
                return("NULL");
            }
        }
    }
    
    
    String formBetName(String strOut, Integer grId, Integer betId, Double point)
    {
        Integer point1 = point.intValue();
        Double temp1 = (point-point1)*1000;
        Long temp2 = Math.round(temp1);
        Integer point2 = temp2.intValue();
        switch(grId)
        {
            case 136:   // Correct score.
                strOut = strOut.replaceFirst("\\(\\)", "\\("+point1.toString()+":"+point2.toString()+"\\)");
                break;
            case 148:   //
            case 152:   // Totals on interval.
            case 345:   //
                strOut = strOut.replaceFirst(" \\(\\)", "")
                        .replaceFirst("\\(\\)", point2+":00");
                if(betId==892||betId==893||betId==1262||betId==1263||betId==1268||betId==1269||betId==1282||betId==1283||betId==1288||betId==1289||betId==1298||betId==1299)
                {
                    point2 += 9;
                }
                else
                {
                    if(betId==894||betId==895||betId==898||betId==899||betId==1264||betId==1265||betId==1270||betId==1271||betId==1284||betId==1285||betId==1290||betId==1291)
                    {
                        point += 14;
                    }
                    else
                    {
                        if(betId==896||betId==897||betId==1266||betId==1267||betId==1280||betId==1281||betId==1286||betId==1287)
                            point += 1;
                    }
                }
                strOut = strOut.replaceFirst("\\(\\)", point2.toString()+":59");
                break;
            case 347:   // Win on the time interval.
            case 381:   // Score on the time interval.
                strOut = strOut.replaceFirst("\\(\\)", point1.toString()+":00")
                        .replaceFirst("\\(\\)", point2.toString()+":59");
                break;
            case 28:    
            case 29:    // Individual total team 1 interval.
            case 30:    // Individual total team 2 interval.
            case 69:
            case 87:
            case 88:
            case 89:
            case 108:
            case 110:
            case 130:
                if(point1.equals(point2))
                {
                    strOut = strOut.replaceFirst("\\(\\)", point1.toString());
                }
                else
                {
                    if(point2>900)
                    {
                        strOut = strOut.replaceFirst("\\(\\)", point1.toString()+" and more");
                    }
                    else
                    {
                        strOut = strOut.replaceFirst("\\(\\)", "from "+point1+" to "+point2);
                    }
                }
                break;
            case 96:    //
            case 113:   // Score goal up to minute.
            case 114:   //
                strOut = strOut.replaceFirst("\\(\\)", point1.toString()).replaceFirst("\\(\\)", point2.toString());
                break;
            case 27:    // 3way handicap.
                strOut = strOut.replaceFirst("3way \\(\\) ", "");
                break;
            default:
                if(pointOddsA.contains(grId))
                {
                    strOut = strOut.replaceFirst(" \\(\\)", "");
                }
                else
                {
                    strOut = strOut.replaceFirst(" \\(\\)", " "+point1.toString());
                }
                break;
        }
        return strOut;
    }
    
    String formBetGroup(String strOut, Integer grId, Double point)
    {
        Integer point1 = point.intValue();
        Double temp1 = (point-point1)*1000;
        Long temp2 = Math.round(temp1);
        Integer point2 = temp2.intValue();
        if(grId==27)
        {
            if(point1<0)
                {
                    strOut = strOut+" 0:"+Math.abs(point1);
                }
                else
                {
                    strOut = strOut+" "+point1+":0";
                }
        }
        return(strOut);
    }

    public void FormDataStringOff(Boolean isMain, BetData bdInput)
    {
        File logs = new File(filepath);
        String strOut;
        if(!logs.exists())
        {
            logs.mkdir();
        }
        DataSenderDB dsdbInst = DataSenderDB.getInstance();
        PrintWriter logWrt = null;
        try
        {
            logWrt = new PrintWriter(new BufferedWriter(new FileWriter(filepath+bdInput.Id.toString()+".txt",true)));
        }
        catch(IOException ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.FormDataStringO");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        if(lineStr.equals(null) || lineStr.isEmpty() || lineStr.containsKey(bdInput.Id))
        {
            strOut = FormUpdateEventOff(bdInput, (isMain ? bdInput.Id : bdInput.MainGameId));
            dsdbInst.sendDataLoop(strOut);
            logWrt.println(sdf.format(new Date()) + " " + strOut);
        }
        for(Events ev: bdInput.Events)
        {
            if(lineStr.equals(null) || lineStr.isEmpty() ||
                    (lineStr.containsKey(bdInput.Id) && OddIsUpdated(ev, lineStr.get(bdInput.Id).Events)))
            {
                strOut = FormUpdateOddsOff(ev, (isMain ? bdInput.Id : bdInput.MainGameId), false);
                dsdbInst.sendDataLoop(strOut);
                logWrt.println(sdf.format(new Date()) + " " + strOut);
            }
        }
        if(!lineStr.equals(null) && !lineStr.isEmpty() && lineStr.containsKey(bdInput.Id))
        {
            for(Events ev: lineStr.get(bdInput.Id).Events)
            {
                if(OddIsUpdated(ev, bdInput.Events))
                {
                    strOut = FormUpdateOddsOff(ev, (isMain ? bdInput.Id : bdInput.MainGameId), true);
                    dsdbInst.sendDataLoop(strOut);
                    logWrt.println(sdf.format(new Date()) + " " + strOut);
                }
            }
        }
        logWrt.close();
    }

    private String FormUpdateEventOff(BetData bdInput, Integer evId)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        long datetime = bdInput.Start;
        Date evd = new Date();
        evd.setTime(datetime*1000);
        String strOut = NextId() +" exec ";
        strOut = strOut + "[es_UpdateEvent] " +
                                evId.toString() + "," +
                                bdInput.LigaId + "," +
                                "\'" + sdf.format(evd) + "\'," +
                                bdInput.Opp1Id + "," +
                                bdInput.Opp2Id + "," +
                                "\'" + bdInput.Opp1.replaceAll("\'","\'\'") + "\'," +
                                "\'" + bdInput.Opp2.replaceAll("\'","\'\'") + "\'," +
                                crwlrId + "," +
                                "NULL";

        return strOut;
    }

    private String FormUpdateOddsOff(Events evInput, Integer evId, Boolean lock)
    {
        String strOut = NextId() +" exec ";
        JSONObject btIn;
        if(Boolean.valueOf(evInput.B))
        {
            lock = true;
        }
        strOut = strOut + "[es_UpdateOdds] " +
                evId.toString() + "," +
                crwlrId + "," +
                "1" + "," +
                evInput.G.toString() + "," +
                "\'";
        if(BetTypes.betTypesList.has(evInput.G.toString()))
        {
            btIn = BetTypes.betTypesList.getJSONObject(evInput.G.toString());
            if(btIn.getJSONObject("B").has(evInput.T.toString()))
            {
                strOut = strOut + formBetGroup(btIn.getString("G"),evInput.G, evInput.P) +
                        (evInput.NameGame.equals("")?"":(" - "+evInput.NameGame)) +
                        "\'," +
                        "\'" + formBetName(btIn.getJSONObject("B").getJSONObject(evInput.T.toString()).getString("N"),evInput.G, evInput.T, evInput.P);
            }
            else
            {
                // return("");
                strOut = strOut + formBetGroup(btIn.getString("G"),evInput.G, evInput.P) + "\'," +     // debug. Bettype is not avaliable or incorrect.
                        "\'" + evInput.T;
            }
        }
        else
        {
            // return("");
            strOut = strOut + evInput.G + "\'," + // debug. Bettype and betgroup are incorrect or not avaliable.
                    "\'" + evInput.T;
        }
        strOut = strOut + "\'," +
                "0," +
                ((Boolean.valueOf(evInput.B)||lock)?"1":"0") + "," +
                evInput.C + "," +
                formPoints(evInput);
        return strOut;
    }

    private Boolean OddIsUpdated(Events evIn, List<Events> evList)
    {
        Boolean isUpdated = true;
        for(Events ev: evList)
        {
            if(evIn.G.equals(ev.G) && evIn.T.equals(ev.T) &&
                    evIn.B.equals(ev.B) &&
                    evIn.C.equals(ev.C) &&
                    evIn.P.equals(ev.P) &&
                    evIn.I.equals(ev.I))
            {
                isUpdated = false;
                break;
            }
        }
        return isUpdated;
    }

    private  String FormUpdateScoreOff(Scores scInput, Integer evId, int id)
    {
        String strOut = evId.toString()+" exec ";

        return strOut;
    }

    private String FormUpdateResultsOff(Integer evId, int id)
    {
        String strOut = evId.toString()+" exec ";

        return strOut;
    }

    public void FormDataStringOffSport(JSONArray jsonInput)
    {
        File logs = new File(filepath);
        if(!logs.exists())
        {
            logs.mkdir();
        }
        PrintWriter logWrt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss.SSS");
        try
        {
            logWrt = new PrintWriter(new BufferedWriter(new FileWriter(filepath+"sports"+".txt",true)));
        }
        catch(IOException ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormDataString.FormDataStringO");
            return;
        }
        FillBetData fbdInst = new FillBetData();
        DataSenderDB dsdbInst = DataSenderDB.getInstance();
        Map<String, CollectedBetData.SportData> sInput = fbdInst.FillSportData(jsonInput);
        String strOut = new String();
        for(Map.Entry<String, CollectedBetData.SportData> si: sInput.entrySet())
        {
            if(!sportData.containsKey(si.getKey()))
            {
                strOut = FormUpdateSport(si.getValue());
                dsdbInst.sendDataLoop(strOut);
                logWrt.println(strOut);
            }
            for(Map.Entry<String, CollectedBetData.LeagueData> li: si.getValue().leagueData.entrySet())
            {
                if(sportData.isEmpty()||(sportData.containsKey(si.getKey())&&!sportData.get(si.getKey()).leagueData.containsKey(li.getKey())))
                {
                    strOut = FormUpdateLeagueOff(li.getValue());
                    dsdbInst.sendDataLoop(strOut);
                    logWrt.println(strOut);
                }
            }
        }
        sportData = sInput;
        logWrt.close();
    }

    private String FormUpdateLeagueOff(CollectedBetData.LeagueData leagueData)
    {
        String strOut = NextId() +" exec ";
        strOut += "[es_UpdateLeague] " +
                        leagueData.leagueId + "," +
                        leagueData.sportId + "," +
                        "\'" + leagueData.leagueName.replaceAll("\'","\'\'") + "\'" + "," +
                        "\'" + leagueData.countryName.replaceAll("\'","\'\'") + "\'" + "," +
                        crwlrId + "," +
                        "NULL" + "," +
                        "NULL" + "," +
                        "NULL";
        return strOut;
    }

    private String FormUpdateSport(CollectedBetData.SportData sportData)
    {
        String strOut = NextId() +" exec ";
        strOut += "[es_UpdateSport] "+
                        sportData.sportId + "," +
                        "\'" + sportData.sportName.replaceAll("\'","\'\'") + "\'" + "," +
                        crwlrId;
        return strOut;
    }

    private int NextId()
    {
        i++;
        if(i>200)
            i=101;
        return i;
    }

    public void UpdateLineBase (Map<Integer, BetData> lineInput)
    {
        fdsInst.lineStr = new HashMap();
        fdsInst.lineStr = lineInput;
    }
}
