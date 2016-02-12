/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.*;
import webcrawler.CollectedBetData.*;

/**
 *
 * @author TFSUser
 */
public class DataCollector 
{
    static class DataCollectorSequence implements Runnable
    {
        DataCollector dcInst = getInstance();
        GameId giInst = GameId.getInstance();
        
        DataCollectorSequence()
        {

        }
        
        @Override
        public void run()
        {
            Thread.currentThread().setName("DataCollector");
            giInst.receiveGameInfo(dcInst.convData(dcInst.receiveData(dcInst.composeUrl(0, null))));
        }
    }
    
    static class ReceiveEventSequence implements Runnable // This loop receives subgames' data.
    {
        GameId.GameInfo giInput;
        private Boolean isStarted = false;
        
        ReceiveEventSequence(GameId.GameInfo giInput)
        {
            this.giInput = giInput;
        }
        
        @Override
        public void run()
        {
            FillBetData fbdInst = new FillBetData();
            GameId giInst = GameId.getInstance();
            FormDataString fdsInst = FormDataString.getInstance();;
            String recData, error;
            CollectedBetData.BetData cbdOutput = null;
            JSONObject jsonInput = new JSONObject();
            JSONArray evInput = new JSONArray();
            Thread.currentThread().setName("RED"+giInput.Id);
            if(!isStarted)
            {
                Logging.EventThreadStarted(Thread.currentThread().getName());
                isStarted = true;
            }
            try
            {
                while(giInst.GameIsOnLive(giInput.Id))
                {
            /*    {
                    fdsInst.LockEvent(giInput.isMain, giInput.Id);
                    throw new InterruptedException();
                } */
                    recData = dcInst.receiveData(dcInst.composeUrl(2, giInput.Id.toString()));
                    jsonInput = dcInst.convData(recData);
                    if (!jsonInput.equals(null))
                    {
                        error = jsonInput.get("Error").toString();
                        if (error.isEmpty() && jsonInput.getBoolean("Success") && !jsonInput.get("Value").equals(null))
                        {
                            jsonInput = jsonInput.getJSONObject("Value");
                            cbdOutput = fbdInst.fillData(jsonInput, giInput.NameGame);
                            fdsInst.FormDataStringO(giInput.isMain, cbdOutput);
                        }
                    }
                    recData = null;
                    jsonInput = null;
                    error = null;
                    cbdOutput = null;
                    Thread.sleep(1000);
                }
                fdsInst.LockEvent(giInput.isMain, giInput.Id);
                Logging.EventThreadStopped(Thread.currentThread().getName());
            }
            catch(Exception ex)
            {
                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.ReceiveEventSequence.run");
            }
            finally
            {
                fbdInst = null;
                giInst = null;
                fdsInst = null;
            }
        }
    }

    static class ReceiveLineSequence implements Runnable // This sequence receives events line for 24 hours on program start and at 5 a.m.
    {
        Boolean init = true;
        String checktime = "06:00";


        ReceiveLineSequence()
        {

        }

        @Override
        public void run()
        {
            List<Integer> sports;
            List<String> lineData = new ArrayList();
            Thread.currentThread().setName("ReceiveLineSequence");
            Logging.EventThreadStarted(Thread.currentThread().getName());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
          /*  while(!init && !sdf.format(new Date()).toString().equals(checktime))
            {
                try
                {
                    Thread.sleep(60 * 1000);
                }
                catch (InterruptedException ex)
                {
                    Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.ReceiveLineSequence.run");
                }
            } */
            sports = FormSports();
            for(Integer sp: sports)
            {
                lineData.add(dcInst.receiveData(dcInst.composeUrl(4,sp.toString())));
            }
            for(String lData: lineData)
            {
                SendLineSport(lData);
            }
            dcInst.StartLineGameThreads();
            init = false;
        }

        private List<Integer> FormSports()
        {
            FormDataString fdsInst = FormDataString.getInstance();
            DataCollector dcInst = DataCollector.getInstance();
            List<Integer> sportIds = new ArrayList();
            String recData = new String(), url;
            JSONObject jsonInput;
            JSONArray jsonInArr;
            try
            {
                url = dcInst.composeUrl(3, "null");
                recData = dcInst.receiveData(url);
            }
            catch (Exception ex)
            {
                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "FormSports");
            }
            jsonInput = dcInst.convData(recData);
            jsonInArr = jsonInput.getJSONArray("Value");
            fdsInst.FormDataStringOffSport(jsonInArr);
            for(int i=0;i<jsonInArr.length();i++)
            {
                jsonInput = jsonInArr.getJSONObject(i);
                if(!sportIds.contains(jsonInput.getInt("SportId")))
                {
                    sportIds.add(jsonInput.getInt("SportId"));
                }
            }
            return sportIds;
        }

        private void SendLineSport(String lineData)
        {
            FillBetData fbdInst = new FillBetData();
            FormDataString fdsInst = FormDataString.getInstance();
            Map<Integer, BetData> lineUpdBase = new HashMap();
            CollectedBetData.BetData bdInput;
            JSONObject jsonInput = dcInst.convData(lineData);
            JSONArray jsonArr = jsonInput.getJSONArray("Value");
            for(int i=0; i<jsonArr.length(); i++)
            {
                jsonInput = jsonArr.getJSONObject(i);
                bdInput = fbdInst.fillData(jsonInput, jsonInput.getString("NameGame"));
                fdsInst.FormDataStringOff(bdInput.MainGameId.equals(0), bdInput);
                lineUpdBase.put(bdInput.Id, bdInput);
            }
            fdsInst.UpdateLineBase(lineUpdBase);
        }


    }

    static class ReceiveLineGame implements Runnable
    {

        ReceiveLineGame()
        {

        }

        @Override
        public void run()
        {
            DataCollector dcInst = DataCollector.getInstance();
            FillBetData fbdInst = new FillBetData();
            String id = dcInst.ReceiveNextId();
            String url = dcInst.composeUrl(4, id);
            String recData = dcInst.receiveData(url);
            JSONObject jsonInput = dcInst.convData(recData);
            CollectedBetData.BetData cbdOutput = fbdInst.fillData(jsonInput, null);

        }
    }
    
    // Some variables.
  //  private Map<Integer,BetData> betData = null;
    Boolean dataReceived = false;
    List<String> idList = new ArrayList();
    AtomicInteger threadCounter = new AtomicInteger(0);
    final ScheduledExecutorService lineScheduler = Executors.newScheduledThreadPool(100);

    private static volatile DataCollector dcInst;
    
    public class BetDataC // Class for the main collection and its methods.
    {
        private Map<Integer, BetData> betData = null;

        public synchronized void writeBetData(BetData bdInput) {
            betData.put(bdInput.Id, bdInput);
        }

        public synchronized BetData readBetdata(Integer id) {
            if (betData.containsKey(id)) {
                return (betData.get(id));
            } else {
                return (null);
            }
        }

        public synchronized Map<Integer, BetData> readBetDataAll() {
            return (betData);
        }

        public synchronized void removeBetData(Integer id) {
            if (betData.containsKey(id)) {
                betData.remove(id);
            }
        }
    }
    
    public static class GameId
    {
        private static Map<Integer,GameInfo> gameInfo = new HashMap(); // Key == Id
        private static volatile GameId giInst;
        private static Boolean isInitialized = false;
        
        public static GameId getInstance()
        {
            GameId locdcInst = giInst;
            if(locdcInst==null)
                synchronized(GameId.class)
                {
                    locdcInst=giInst;
                    if(locdcInst==null)
                    {
                        giInst=locdcInst=new GameId();
                        gameInfo = null;
                    }
                }
            return locdcInst;
        }
        
        public class GameInfo
        {
            public Integer Id;
            public Integer MainId;
            public String NameGame;
            public Boolean isMain;
        }
        
        public synchronized void receiveGameInfo(JSONObject convData)
        {
            Map<Integer,GameInfo> tempGameInfo = new HashMap(), threadStartMap = new HashMap();
            GameInfo gInfo;
            JSONArray dataArray = convData.getJSONArray("Value"), tempArray;
            JSONObject tempObj;
            for (int i = 0; i < dataArray.length(); i++) {
                if (dataArray.get(i).equals(null)) {
                    System.out.println("null at the subgames receiveGameInfo");
                    continue;
                }
                tempObj = dataArray.getJSONObject(i);
                tempArray = tempObj.getJSONArray("SubGames");
                for (int j = 0; j < tempArray.length(); j++) {
                    gInfo = new GameInfo();
                    if (tempArray.get(j).equals(null)) {
                        System.out.println("null at the subgame receiveGameInfo inner cycle");
                        continue;
                    }
                    gInfo.Id = tempArray.getJSONObject(j).getInt("GameId");
                    gInfo.NameGame = tempArray.getJSONObject(j).getString("NameGame");
                    gInfo.MainId = tempArray.getJSONObject(j).getInt("MainGameId");
                    if (gInfo.MainId.equals(gInfo.Id) || gInfo.MainId == 0) {
                        gInfo.isMain = true;
                    } else {
                        gInfo.isMain = false;
                    }
                    tempGameInfo.put(gInfo.Id, gInfo);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.GameId.ReceiveGameInfo");
                    }
                }
            }
            for (Map.Entry<Integer, GameInfo> tgi : tempGameInfo.entrySet()) {
                if (!isInitialized || !gameInfo.containsKey(tgi.getKey())) {
                    threadStartMap.put(tgi.getKey(), tgi.getValue());
                }
            }
            final ScheduledExecutorService redThreads = Executors.newScheduledThreadPool(threadStartMap.size());
            for (Map.Entry<Integer, GameInfo> tsm : threadStartMap.entrySet()) {
                final ScheduledFuture<?> recEventData = redThreads.schedule(new ReceiveEventSequence(tsm.getValue()), 100, TimeUnit.MILLISECONDS);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.GameId.receiveGameInfo");
                }
            }
            gameInfo = tempGameInfo;
            isInitialized = true;
        }

        public synchronized Boolean GameIsOnLive(Integer gameId)
        {
            return((gameInfo.containsKey(gameId))?true:false);
        }

        public synchronized Map<Integer,GameInfo> RemoveGames(Map<Integer,GameInfo> inputGameInfo)
        {
            Map<Integer,GameInfo> outputGameInfo = gameInfo;
            for (Map.Entry<Integer, GameInfo> giEntry : outputGameInfo.entrySet()) {
                if (!inputGameInfo.containsKey(giEntry.getKey())) {
                    outputGameInfo.remove(giEntry.getKey());
                }
            }
            return(outputGameInfo);
        }
    }
    
    static public DataCollector getInstance()
    {
        DataCollector locdcInst = dcInst;
        if(locdcInst==null)
            synchronized(DataCollector.class)
            {
                locdcInst=dcInst;
                if(locdcInst==null)
                {
                    dcInst=locdcInst=new DataCollector();
                }
            }
        return locdcInst;
    }

    public void StartLineGameThreads()
    {
        while(threadCounter.intValue()<100)
        {
            ScheduledFuture<?> newLineThread = lineScheduler.scheduleWithFixedDelay(new ReceiveLineGame(), 0, 1000, TimeUnit.MILLISECONDS);
            threadCounter.incrementAndGet();
        }
    }

    public void ThreadCounterDecrease()
    {
        threadCounter.decrementAndGet();
    }
    
    public String receiveData(String passedUrl)
    {
        String rData = "Fail! "+passedUrl;
        HttpURLConnection con = null;
        Boolean dataReceived = false;
        while(!dataReceived)
        {
            try
            {
                URL url = new URL(passedUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.connect();
                InputStream dstream = con.getInputStream();
                BufferedReader readData = new BufferedReader(new InputStreamReader(dstream, "UTF-8"));
                rData = readData.readLine();
                dstream.close();
                con.disconnect();
                dataReceived = true;
            }
            catch (Exception ex)
            {
                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.receiveData");
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.receiveData");
                }
            }
            finally
            {
                con.disconnect();
            }
        }
        return rData;
    }
    
    String composeUrl(int type, String Id)
    {
        String url = "https://1-x-bet.com/" + ((type<3)?"LiveFeed/":"LineFeed/");
        switch(type)
        {
            case 0:
                url = url + "GetLeftMenu?lng=en_GB";
                break;
            case 1:
                url = url + "Get1x2?sportid=" + Id + "&count=100&lng=en_GB";
                break;
            case 2:
                url = url + "GetGame?id=" + Id + "&lng=en_GB";
                break;
            case 3:
                url = url + "Web_GetSports?tf=1000000&lng=en";
                break;
            case 4:
                url = url + "Get1x2?sportId=" + Id + "&count=2000&lng=en&tf=1000000";
                break;
            default:
                return null;
        }
        return url;
    }

    public synchronized String ReceiveNextId()
    {
        while(idList.isEmpty()||idList.equals(null))
        {

        }
        String strOut = idList.get(0);
        idList.remove(0);
        return strOut;
    }
    
    public JSONObject convData(String passedData)
    {
        JSONObject json = null;
        try
        {
            json = new JSONObject(passedData);
        }
        catch(Exception ex)
        {
            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataCollector.convData");
        }
        return json;
    }
}


