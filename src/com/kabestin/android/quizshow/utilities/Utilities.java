package com.kabestin.android.quizshow.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Utilities {
	
    // Stolen from 
    // http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
    static public boolean isNetworkAvailable(Activity aActivity) {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) aActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
	//--------------------------------------------------------------------------------------------

	private static final String APP_DATA_PATH = "/Android/data/com.faithcrc.ncis/temp/";
	static public File getTempDir() {
	    return new File(Environment.getExternalStorageDirectory(), APP_DATA_PATH);
	}
	
	//--------------------------------------------------------------------------------------------

    public static String readFromInputStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
    
	public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
	    try {
	        InetAddress candidateAddress = null;
	        // Iterate all NICs (network interface cards)...
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // Iterate all IP addresses assigned to each card...
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address. Return it immediately...
	                        return inetAddr;
	                    }
	                    else if (candidateAddress == null) {
	                        // Found non-loopback address, but not necessarily site-local.
	                        // Store it as a candidate to be returned if site-local address is not subsequently found...
	                        candidateAddress = inetAddr;
	                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
	                        // only the first. For subsequent iterations, candidate will be non-null.
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            // We did not find a site-local address, but we found some other non-loopback address.
	            // Server might have a non-site-local address assigned to its NIC (or it might be running
	            // IPv6 which deprecates the "site-local" concept).
	            // Return this non-loopback candidate address...
	            return candidateAddress;
	        }
	        // At this point, we did not find a non-loopback address.
	        // Fall back to returning whatever InetAddress.getLocalHost() returns...
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        return jdkSuppliedAddress;
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	}


}
