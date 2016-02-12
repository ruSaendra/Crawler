/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.*;
import webcrawler.CollectedBetData.*;
import webcrawler.DataCollector.*;
// import webcrawler.DataSenderWP.*;
import webcrawler.DataSenderDB.*;


/**
 *
 * @author TFSUser
 */
public class WebCrawler {

    /**
     * @param args the command line arguments
     */

    
    public static void main(String[] args) throws Exception
    {
        // TODO code application logic here
        Logging.SetLogpath();
        Logging.ProgramStarted();
        WebCrawler wcInst = new WebCrawler();
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        BetTypes.fillBTL(wcInst);
//        Thread dataSenderWP = new Thread(new DataSenderWPsequence());
//        dataSenderWP.start();
        ScheduledFuture<?> loggingThread = scheduler.scheduleWithFixedDelay(new Logging.LoggingSequence(), 0, 1000, TimeUnit.MILLISECONDS);
        ScheduledFuture<?> dataSenderDB = scheduler.schedule(new DataSenderDBsequence(), 0, TimeUnit.MILLISECONDS);
    //    ScheduledFuture<?> dataCollector = scheduler.scheduleWithFixedDelay(new DataCollectorSequence(), 0, 1000, TimeUnit.MILLISECONDS);
        ScheduledFuture<?> receiveLine = scheduler.scheduleWithFixedDelay(new ReceiveLineSequence(), 0, 1, TimeUnit.MILLISECONDS);
        while(true)
        {
            if (loggingThread.isCancelled() || loggingThread.isDone())
            {
                loggingThread = scheduler.scheduleWithFixedDelay(new Logging.LoggingSequence(), 0, 1000, TimeUnit.MILLISECONDS);
            }
            if (dataSenderDB.isCancelled() || dataSenderDB.isDone())
            {
                dataSenderDB = scheduler.schedule(new DataSenderDBsequence(), 0, TimeUnit.MILLISECONDS);
            }
        /*    if (dataCollector.isCancelled() || dataCollector.isDone())
            {
                dataCollector = scheduler.scheduleWithFixedDelay(new DataCollectorSequence(), 0, 1000, TimeUnit.MILLISECONDS);
            } */
            if (receiveLine.isCancelled() || receiveLine.isDone())
            {
                receiveLine = scheduler.scheduleWithFixedDelay(new ReceiveLineSequence(), 0, 1000, TimeUnit.MILLISECONDS);
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "root");
            }
        }
    }
        
    public JSONObject convData(String passedData) throws Exception
    {
        return new JSONObject(passedData);
    }
    
}
