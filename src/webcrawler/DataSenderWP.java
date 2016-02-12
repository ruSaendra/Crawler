/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webcrawler;

/**
 *
 * @author TFSUser
 */
/* public class DataSenderWP 
{
    static class DataSenderWPsequence extends Thread
    {
        
        
        DataSenderWPsequence()
        {
            
        }
        
        @Override
        public void run()
        {
            ServerSocket servSct;
            Socket sct;
            Boolean loopStarted=false;
            String stri = "Error!";
            Map<Integer,BetData> bdInput;
            FormWebPage fwInst = new FormWebPage();
            try
            {
                servSct = new ServerSocket(4000);
                sct = servSct.accept();
                while(true)
                {
                    try
                    {
                        if(loopStarted)
                        {
                            if(sct.isConnected())
                            {
                                sct.close();
                            }
                            if(servSct.isBound())
                            {
                                servSct.close();
                            }
                            servSct = new ServerSocket(4000);
                            sct = servSct.accept();
                        }
                        loopStarted = true;
                        PrintWriter dataOutput = new PrintWriter(sct.getOutputStream(), true);
                        BufferedReader dataReq = new BufferedReader(new InputStreamReader(sct.getInputStream()));
                        String strReq = dataReq.readLine();
                        System.out.println(strReq);
                        DataCollector dcInst = DataCollector.getInstance();
                        bdInput = dcInst.sendBDoutput();
                        if(strReq.startsWith("GET / "))
                        {
                            stri = fwInst.formMainPage(bdInput);
                        }
                        else if(strReq.startsWith("GET /getgame="))
                        {
                            CollectedBetData cbdInst = new CollectedBetData();
                            String strp[] = strReq.split(" ");
                            Integer evId = Integer.parseInt(strp[1].substring(9));
                            BetData bdElm = cbdInst.new BetData();
                            if(bdInput.containsKey(evId))
                            {
                                bdElm = bdInput.get(evId);
                                stri = fwInst.formSubPage(bdElm);
                            }
                            else
                            {
                                stri = fwInst.legion();
                            }
                        }
                        dataOutput.write(stri);
                        dataOutput.close();
                        sct.close();
                        servSct.close();
                    }
                    catch(Exception ex)
                    {
                        System.out.println(ex);
                    }
                }
            }
            catch(Exception ex)
            {
                System.out.println(ex);
            }
        }
    }
} */
