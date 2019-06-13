package com.tobee.net;

import java.util.HashMap;
import java.util.Map;

import com.tobee.net.http.KMAException;
import com.tobee.net.http.KPXSocketHttpClient;
import com.tobee.net.http.SocketHttpClient;
import com.tobee.net.http.SocketHttpClient.ProviderType;

public class KPXPresenter {
	private static final String DNS_NAME = "openapi.kpx.or.kr";
	private static final String IP_ADDRESS = "211.179.209.205";
			
	public static String connectAndGetKPXInfo(final String serverKey) throws KMAException
	{
		final String serviceKey = serverKey;
		final String serverName = DNS_NAME;
		final String dnsName = DNS_NAME;
		final String ipaddress = IP_ADDRESS;
		
		Map<String,String> headerMap = new HashMap<String,String>();
		headerMap.put("Connection", "Close");
		headerMap.put("Host", dnsName);
		
		String response = new KPXPresenter().getKPXResponse(dnsName, serverName, ipaddress, serviceKey, headerMap);
		return response;
	}
	
	public static String connectDirectAndGetKPXInfo(final String serverKey) throws KMAException
	{
		final String serviceKey = serverKey;
		final String serverName = null;
		final String dnsName = DNS_NAME;
		final String ipaddress = IP_ADDRESS;
		
		Map<String,String> headerMap = new HashMap<String,String>();
		headerMap.put("Connection", "Close");
		headerMap.put("Host", dnsName);
		
		String response = new KPXPresenter().getKPXResponse(dnsName, serverName, ipaddress, serviceKey, headerMap);
		return response;
	}
	
	
	private String getKPXResponse(
			final String dnsName, final String serverName, final String ipaddress, 
			final String serviceKey, Map<String,String> headerMap
	) throws KMAException{
		final ProviderType type = ProviderType.KPX; 
		SocketHttpClient.ServiceProvider shwc =  new SocketHttpClient.ServiceProvider(type, serviceKey, serverName);
		
		shwc.setDnsName(dnsName);
		shwc.setServerIp(ipaddress);
		
		SocketHttpClient kpxSockClient = new KPXSocketHttpClient(shwc);
		kpxSockClient.setHttpMethod(SocketHttpClient.HTTP_GET);
		
		return kpxSockClient.sendAndResponse(headerMap);
	}
	
}
