/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//Test git
package webcrawler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author TFSUser
 */
public class BetTypes 
{
    public static JSONObject betTypesList = new JSONObject();
    
    public static void fillBTL(WebCrawler __inst) throws Exception
    {
        String strInp = "";
/*        Path path = Paths.get("bettypes.txt");
        Scanner scanBtl = new Scanner(path, "UTF-8");        
        while(scanBtl.hasNextLine())
        {
            strInp = strInp + scanBtl.nextLine().trim();
        }
        scanBtl.close(); */
        while(true)
        {
            try
            {
                URLConnection con = new URL("https://1-x-bet.com/js/betsNames_en.js").openConnection();
                con.setConnectTimeout(5000);
                con.connect();
                InputStream dstream = con.getInputStream();
                BufferedReader readData = new BufferedReader(new InputStreamReader(dstream, "UTF-8"));
                readData.mark(1000000);
                while(readData.readLine()!=null)
                {
                    readData.reset();
                    strInp = strInp + readData.readLine().trim();
                    readData.mark(1000000);
                }
                dstream.close();
                Thread.sleep(100);
            }
            catch(SocketTimeoutException ex)
            {
                continue;
            }
            break;
        }
        strInp = strInp.substring(16);
        betTypesList = __inst.convData(strInp);
    }
}
