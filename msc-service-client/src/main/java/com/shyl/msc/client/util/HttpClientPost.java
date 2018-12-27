package com.shyl.msc.client.util;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientPost {


	/**
     * Post Request
     * @return
     * @throws Exception
     */
    public static String doPost(String url, byte[] data, String contentType) throws Exception {
        
        URL localURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)localURL.openConnection();
        
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
       

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.write(data);
        os.flush();
        os.close();
        conn.connect();
        
        InputStream inputStream = null;
        
        try {
            
            if (conn.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }
            inputStream = conn.getInputStream();
            byte[] buf = new byte[1024];
            byte[] newbuf = new byte[0];
            int i = 0;
            while ((i=inputStream.read(buf))!=-1) {
            	int len = newbuf.length;
            	byte[] temp = new byte[len+i];
            	if (newbuf.length>0) {
                	System.arraycopy(newbuf, 0, temp, 0, len);
            	}
            	System.arraycopy(buf, 0, temp, len, i);
            	newbuf=temp;
            }
            return new String(newbuf,"UTF-8");
        } catch (Exception e) {
			throw e;
		} finally {
            if (os != null) {
            	os.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            
        }
    }
	
}
