package test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.axis.components.net.JSSESocketFactory;

public class MySocketFactory extends JSSESocketFactory  {

	public MySocketFactory(Hashtable attributes) {
		super(attributes);
	}
	protected void initFactory() throws IOException {

		TrustManager[] trustAllCerts = new TrustManager[] {
		    new X509TrustManager() {
		      public X509Certificate[] getAcceptedIssuers() {
		        return null;
		      }

		      public void checkClientTrusted(X509Certificate[] certs, String authType) {
		        // Trust always
		      }

		      public void checkServerTrusted(X509Certificate[] certs, String authType) {
		        // Trust always
		      }
		    }
		  };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			HostnameVerifier hv = new HostnameVerifier() {
		        public boolean verify(String arg0, SSLSession arg1) {
		            return true;
		        }
			};
			
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			sslFactory = sc.getSocketFactory();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
	}
	
}
