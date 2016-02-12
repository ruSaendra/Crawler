package webcrawler;

import com.sun.javafx.scene.SceneEventDispatcher;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.Condition;

/**
 * Created by Saendra on 29/01/15.
 */

public class Logging
{
    static class LoggingSequence implements Runnable
    {
        List<String> logLst;
        private static final ReentrantLock lock = new ReentrantLock();
        private static final Condition isEmpty = lock.newCondition();

        LoggingSequence()
        {

        }

        @Override
        public void run()
        {
            Thread.currentThread().setName("LoggingThread");
            logLst = new ArrayList();
            PrintWriter logWrt;
        //    lock.lock();
            try {
            //    isEmpty.await();
                while(logList.isEmpty())
                {
                    Thread.sleep(1);
                }
                logWrt = new PrintWriter(new BufferedWriter(new FileWriter(filepath,true)));
                logLst = WriteToLog();
                for(String logString: logLst)
                {
                    logWrt.println(logString);
                }
                logWrt.close();
            }
            catch (InterruptedException ex)
            {
                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "Logging.LoggingSequence");
            }
            catch (IOException ex)
            {
                System.out.println("IOException at Logging.");
            }
            finally
            {
            //    lock.unlock();
            }
        }

        public static void LogUnlock()
        {
            if(lock.hasWaiters(isEmpty))
                isEmpty.signal();
        }
    }

    private static List<String> logList = new ArrayList();
    private static String filepath = "C:\\WCLogs\\ll\\" /* "/home/WClogs/LClogs/" */;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss.SSS");

    public static synchronized void ProgramStarted()
    {
        logList.add(sdf.format(new Date())+" WebCrawler initialized.");
    //    LoggingSequence.LogUnlock();
    }

    public static synchronized void EventsDataReceiveStarted()
    {
        logList.add(sdf.format(new Date())+" WebCrawler started events data reception.");
    //    LoggingSequence.LogUnlock();
    }

    public static synchronized void EventThreadStarted(String threadName)
    {
        logList.add(sdf.format(new Date())+" Thread started: "+threadName);
    //    LoggingSequence.LogUnlock();
    }

    public static synchronized void EventThreadStopped(String threadName)
    {
        logList.add(sdf.format(new Date())+" Thread stopped: "+threadName);
    //    LoggingSequence.LogUnlock();
    }

    public static synchronized void ErrorOccurence(Exception ex, String threadName, String methodName)
    {
        logList.add(sdf.format(new Date())+" Exception occured!\n * Thread: "+threadName+"\n * Method: "+methodName+"\n * Exception: "+ex);
    //    LoggingSequence.LogUnlock();
    }

    public static void SetLogpath()
    {
        File logs = new File(filepath);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        if(!logs.exists())
            logs.mkdir();
        filepath += "lclog_"+sdf.format(new Date())+".txt";
    }

    private static synchronized List<String> WriteToLog()
    {
        List<String> templist = logList;
        logList = new ArrayList();
        return templist;
    }

}
