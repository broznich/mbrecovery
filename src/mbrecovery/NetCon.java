/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbrecovery;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 *
 * @author mitt
 */
public class NetCon {
    URL urlObj;
    public NetCon (String url) throws MalformedURLException {
        urlObj = new URL(url);        
    }
    
    public long getNewIndex () throws IOException {
        String res = getData();
        return Long.parseLong(res);
    }
    
    public String getData () throws IOException {
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("GET");
        
        if (conn.getResponseCode() == 200) {
            return conn.getResponseMessage();
        }
        
        return "";
    }
    
    public boolean setData (long index, String message) throws ProtocolException, IOException {
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        
        os.write(message.getBytes());
        os.flush();
        os.close();
        
        return conn.getResponseCode() == 200;
    }
}
