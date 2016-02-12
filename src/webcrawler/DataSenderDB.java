/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Saendra
 */
public class DataSenderDB 
{
 
    static class DataSenderDBsequence implements Runnable
    {
        private DataSenderDB dsdbInst = getInstance();
        private List<String> dataStrings = new ArrayList();
        private Socket dataSocket = new Socket();
        private OutputStream dataStream;


        DataSenderDBsequence()
        {

        }
        
        @Override
        public void run()
        {
            Thread.currentThread().setName("DataSenderDB");
            OpenSocketConnection();
            int i = 0;
        /*    while(true)
            {
                try
                {
                    dataStream = dataSocket.getOutputStream();
                    break;
                }
                catch (IOException ex)
                {
                    Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataSenderDBSequence.run");
                }
            }  */
            while(true)
            {
                while (dsdbInst.strData.isEmpty())
                {
                    dsdbInst = DataSenderDB.getInstance();
                }
                dataStrings = dsdbInst.RemoveString();
                for (String dataInput : dataStrings)
                {
                    while (true)
                    {
                        try
                        {
                            String newSTR = "<<" + dataInput + ">>";
                            dataStream.write(newSTR.getBytes());
                            dataStream.flush();
                            i++;
                            if(i>=50000)
                            {
                                Thread.sleep(1000);
                                i=0;
                            }
                        }
                        catch (Exception ex)
                        {
                            Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataSenderDB.DataSenderDBsequence.run");
                            System.out.println("Socket connection failed: " + ex);
                            OpenSocketConnection();
                            continue;
                        }
                        break;
                    }
                }
            }
        /*    while(true)
            {
                try
                {
                    while(dsdbInst.strData.isEmpty())
                    {
                        dsdbInst = DataSenderDB.getInstance();
                    }
                    dataStrings = dsdbInst.RemoveString();
                    for(String dataInput: dataStrings)
                    {
                    try
                    {
                        dsdbInst.wrData = new PrintWriter(dsdbInst.dataStream,true);
                        String newSTR = " <<"+dataInput+" >> ";
                        dsdbInst.wrData.println(newSTR);
                    //    System.out.println(newSTR);
                        dsdbInst.dataStream.flush();
                        dsdbInst.RemoveString();
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex+" DS.");
                    }
                    }
                    Thread.sleep(100);
                } catch(Exception ex) {
                    // TODO: handle exceptions
                }
            } */
        }

        private void OpenSocketConnection()
                {
                    if(!dataSocket.isConnected()) {
                        while (true)
                        {
                            try
                            {
                                dataSocket = new Socket();
                                dataSocket.bind(null);
                                dataSocket.setKeepAlive(true);
                                dataSocket.connect(new InetSocketAddress("85.17.190.211", 906)); // CONNECTION STRING
                                dataStream = dataSocket.getOutputStream();
                                dataStream.flush();
                                Logging.EventThreadStarted(Thread.currentThread().getName());

                            }
                            catch (Exception ex)
                            {
                                Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataSenderDB.OpenSocketConnection");
                                continue;
                            }
                            break;
                }
            }
        }
    }
    
    
    
    public Socket dataSocket = new Socket();
    public OutputStream dataStream;
    public PrintWriter wrData;
    public List<String> strData = new ArrayList();
    private static volatile DataSenderDB dsdbInst;
    
    public static DataSenderDB getInstance()
    {
        DataSenderDB locdcInst = dsdbInst;
        if(locdcInst==null)
            synchronized(DataSenderDB.class)
            {
                locdcInst=dsdbInst;
                if(locdcInst==null)
                {
                    dsdbInst=locdcInst=new DataSenderDB();
                }
            }
        return locdcInst;
    }

    public void OpenSocketConnection()
    {
        if(!dataSocket.isConnected()) {
            while (true)
            {
                try
                {
                    dataSocket = new Socket();
                    dataSocket.bind(null);
                    dataSocket.setKeepAlive(true);
                    dataSocket.connect(new InetSocketAddress("192.168.89.39", 22345));
                //    dataStream = dataSocket.getOutputStream();
                    Logging.EventThreadStarted(Thread.currentThread().getName());
                }
                catch (Exception ex)
                {
                    Logging.ErrorOccurence(ex, Thread.currentThread().getName(), "DataSenderDB.OpenSocketConnection");
                    continue;
                }
                break;
            }
        }
    }
    
    public synchronized void sendDataLoop(String dataInput)
    {
        DataSenderDB dsdbInst = DataSenderDB.getInstance();
        dsdbInst.strData.add(dataInput);
        dataInput = null;
        dsdbInst = null;
    }
    
    public synchronized List<String> RemoveString()
    {
        DataSenderDB dsdbInst = DataSenderDB.getInstance();
        List<String> tempList = dsdbInst.strData;
        dsdbInst.strData = new ArrayList();
        return tempList;
    }
}
