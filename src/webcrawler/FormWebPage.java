/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;

import java.util.*;
import webcrawler.CollectedBetData.*;
import org.json.*;

/**
 *
 * @author Saendra
 */
public class FormWebPage 
{
    public String formMainPage(Map<Integer,BetData> dataInput) throws Exception
    {
        String strOutput;
        if(dataInput==null)
        {
            strOutput = legion();
        }
        else
        {
            strOutput = "<!DOCTYPE html>" +
                            "<html>" +
                                "<head>" +
                                    "<title>" +
                                        "WCWI" +
                                    "</title>" +
                                    "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">" +
                                    "<meta http-equiv=\"refresh\" content=\"5\">" +
                                "</head>" +
                                "<body>" +
                                    "<table border=\"1\">" +
                                        "<tr>" +
                                            "<td>#</td>" +
                                            "<td>ID</td>" +
                                            "<td>Sport</td>" +
                                            "<td>Event name</td>" +
                                            "<td>Team 1</td>" +
                                            "<td>Score 1</td>" +
                                            "<td>Score 2</td>" +
                                            "<td>Team 2</td>" +
                                        "</tr>";
            int i = 0;
            for(Map.Entry<Integer,BetData> evIn: dataInput.entrySet())
            {
                CollectedBetData.BetData din = evIn.getValue();
                strOutput = strOutput + "<tr>";
                strOutput = strOutput + "<td>" + i++ + "</td>";
                try
                {
                    strOutput = strOutput + "<td>" + "<a href=\"/getgame="+ din.Id.toString()+"\">"+ din.Id.toString() +"</a>" + "</td>";
                }
                catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null id" + "</td>";
                }
                try
                {
                   strOutput = strOutput + "<td>" + din.SportName + "</td>";
               }
               catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null sportname" + "</td>";
               }
                try
                {
                    strOutput = strOutput + "<td>" + din.Champ + "</td>";
                }
                catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null eventname" + "</td>";
                }
                try
                {
                    strOutput = strOutput + "<td>" + din.Opp1 + "</td>";
                }
                catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null opp 1" + "</td>";
                }
                try
                {
                    strOutput = strOutput + "<td>" + din.Scores.FullScore.Sc1.toString() + "</td>";
                }
                catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null sc1" + "</td>";
                }
                try
                {
                    strOutput = strOutput + "<td>" + din.Scores.FullScore.Sc2.toString() + "</td>";
                }
                catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null sc2" + "</td>";
                }
                try
                {
                    strOutput = strOutput + "<td>" + din.Opp2 + "</td>";
                }
                catch(Exception ex)
                {
                    strOutput = strOutput + "<td>" + "null opp 2" + "</td>";
                }
                strOutput = strOutput + "</tr>";
            }
            System.out.println("wat");
            strOutput = strOutput +
                                "</table>" +
                            "<body>" +
                        "</html>";
        }
        return strOutput;
    }
    
    public String formSubPage(BetData dataInput) throws Exception
    {
        String strOutput;
        strOutput = "<!DOCTYPE html>" +
                    "<html>" +
                        "<head>" +
                            "<title>" +
                                "WCWI/Event №" + dataInput.Id + ": " + dataInput.Champ + ", " + dataInput.Opp1 + " x " + dataInput.Opp2 +
                            "</title>" +
                            "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">" +
                            "<meta http-equiv=\"refresh\" content=\"5\">" +
                        "</head>" +
                        "<body>" +
                            "<a href=\"javascript:history.back()\">Back</a><br>" +
                            "Event ID: " + dataInput.Id + "<br>" +
                            dataInput.SportName +
                            dataInput.Num + ": " + dataInput.Champ + "<br>" +
                            dataInput.Opp1 + " x " + dataInput.Opp2 + "<br>" +
                            gameStatus(dataInput.Scores.CurrentPeriod, dataInput.Finished, dataInput.PeriodName) +
                            formScores(dataInput.Scores) +
                            "Finished ==" + dataInput.Finished + "<br>" +
                            "GameDescr ==" + dataInput.GameDescr + "<br>" +
                            "GameType ==" + dataInput.GameType + "<br>" +
                            "LigaId ==" + dataInput.LigaId + "<br>" +
                            "IsTourn ==" + dataInput.IsTourn + "<br>" +
                            "NameGame ==" + dataInput.NameGame + "<br>" +
                            "Scores/Podacha ==" + dataInput.Scores.Podacha + "<br>" +
                            "Scores/TimeSec ==" + dataInput.Scores.TimeSec + "<br>" +
                            "SportID ==" + dataInput.SportId + "<br>" +
                            "Start ==" + dataInput.Start + "<br>" +
                            "<table border=\"1\">" +
                                "<tr>" +
                                    "<td>#</td>" +
                                    "<td>Betgroup</td>" +
                                    "<td>Bettype</td>" +
                                    "<td>Odd</td>" +
                                    "<td>Lock</td>" +
                                "</tr>";
        for(int i=0;i<dataInput.Events.size();i++)
        {
            Events diE = dataInput.Events.get(i);
            JSONObject btin = BetTypes.betTypesList.getJSONObject(diE.G.toString());
            // btin = btin.getJSONObject("B");
            strOutput = strOutput + "<tr><td>" + i + "</td><td>" + btin.getString("G") + "</td><td>" + formBetType(btin.getJSONObject("B"),diE) + "</td><td>" + diE.C.toString() + "</td><td>" + ((Boolean.valueOf(diE.B))?"X":"") + "</tr>";
        }
        strOutput = strOutput +
                            "</table>" +
                        "</body>" +
                    "</html>";    
        return strOutput;
    }
    
    private String formScores(Scores scInput) throws Exception
    {
        String scString = null;
        if(scInput!=null)
        {
            for(Integer i=0;i<=(scInput.PeriodScores.size());i++)
            {
                if(i==0)
                {
                    scString = "<table border=\"1\">" +
                                    "<tr>" +
                                        "<td>Period</td>" +
                                        "<td>1st team</td>" +
                                        "<td>2nd team</td>" +
                                    "</tr>";
                }
                scString = scString +
                                    "<tr>" +
                                        "<td>" + (i==0?"Total":i).toString() + "</td>" +
                                        "<td>" + (i==0?scInput.FullScore.Sc1:scInput.PeriodScores.get(i-1).SValue.Sc1).toString() + "</td>" +
                                        "<td>" + (i==0?scInput.FullScore.Sc2:scInput.PeriodScores.get(i-1).SValue.Sc2).toString() + "</td>" +
                                    "</tr>";
            }
        }
        if(!scInput.SubScore.equals("null"))
        {
            String[] SSc = scInput.SubScore.split(",");
            for(int i = 0;i<SSc.length;i++)
            {
                SSc[i] = SSc[i].replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\\"", "").replaceAll("Sc", "").substring(2);
            }
            scString = scString +
                                    "<tr>" +
                                        "<td>" + "SubScore" + "</td>" +
                                        "<td>" + SSc[0] + "</td>" +
                                        "<td>" + SSc[1] + "</td>" +
                                    "</tr>";
        }
        if(scString==null)
            scString = "Match didn't start yet";
        else
            scString = scString +"</table>";
        return scString;
    }
    
    private String formBetType(JSONObject btList, Events eventInput) throws Exception
    {
        String btString;
        Integer C1,C2;
        Double temp;
        btString = btList.getString(eventInput.T.toString());
        C1 = eventInput.P.intValue();
        temp = (eventInput.P - C1)*1000;
        C2 = temp.intValue();
        switch(eventInput.G)
        {
        /*    case 2:     // Фора
                if(eventInput.P>0)
                {
                    btString = btString.replaceFirst("\\(\\)", "(+"+eventInput.P+")");
                }
                else
                {
                    btString = btString.replaceFirst("\\(\\)", "("+eventInput.P+")");
                }
            case 277:   //Команда забьёт голов подряд.
                btString = btString.replaceFirst("\\(\\)", "("+eventInput.P+")");
                break;
            case 17:    //Тотал.
                btString = btString.replaceFirst("\\(\\)", "("+eventInput.P+")");
                break;
            case 60:
                */ // Разобраться позже.
            default:    //Не требует специальных условий.
                btString = btString.replaceFirst("\\(\\)", "("+eventInput.P.toString()+")");
                break;
        }
        return btString;
    }
    
    private String gameStatus(Integer currPer, String isFin, String perName) throws Exception
    {
        if(!Boolean.valueOf(isFin))
        {
            switch(currPer)
            {
                case 0:
                    return("");
                default:
                    return("Current period: " + currPer + " " + perName + "<br>");
            }
        }
        else
            return("Game is finished<br>");
    }
    
    public String legion() throws Exception // does this method have a soul?
    {
        return("No data avaliable.");
    }
}


