package com.tobee.net;

import java.util.HashMap;
import java.util.Map;

import com.tobee.net.http.KMAException;
import com.tobee.net.http.KMASocketHttpClient;
import com.tobee.net.http.SocketHttpClient;
import com.tobee.net.http.SocketHttpClient.ProviderType;

public class WeatherPresenter {
	private static final String DNS_NAME = "newsky2.kma.go.kr";
	private static final String IP_ADDRESS = "203.247.66.147";
	
	public static String connectAndGetWeatherInfo(final String serverKey, final int dx, final int dy, final boolean isJsonReq) throws KMAException
	{
		final String serviceKey = serverKey;
		final String Nx = String.valueOf(dx); 
		final String Ny = String.valueOf(dy); 
		
		final String serverName = DNS_NAME;
		final String dnsName = DNS_NAME;
		final String ipaddress = IP_ADDRESS;
		
		String response = "";
		
		Map<String,String> headerMap = new HashMap<String,String>();
		
		headerMap.put("Connection", "close");
		headerMap.put("Host", dnsName);
		
		
		if(isJsonReq)
			response = new WeatherPresenter().getWeatherResponseJson(dnsName, serverName, ipaddress, serviceKey, Nx, Ny, headerMap);
		else
			response = new WeatherPresenter().getWeatherResponse(dnsName, serverName, ipaddress, serviceKey, Nx, Ny, headerMap);

		return response;
	}
	
	public static String connectDirectAndGetWeatherInfo(String serverKey, final int dx, final int dy, final boolean isJsonReq) throws KMAException
	{
		final String serviceKey = serverKey;
		final String Nx = String.valueOf(dx); 
		final String Ny = String.valueOf(dy); 
		final String serverName = null;
		final String dnsName = DNS_NAME;
		final String ipaddress = IP_ADDRESS;
		String response = "";
		
		Map<String,String> headerMap = new HashMap<String,String>();
		
		headerMap.put("Connection", "close");
		headerMap.put("Host", dnsName);
		
		if(isJsonReq)
			response = new WeatherPresenter().getWeatherResponseJson(dnsName, serverName, ipaddress, serviceKey, Nx, Ny, headerMap);
		else
			response = new WeatherPresenter().getWeatherResponse(dnsName, serverName, ipaddress, serviceKey, Nx, Ny, headerMap);

		return response;
	}
	
	private String getWeatherResponseJson(
			final String dnsName, final String serverName, final String ipaddress, 
			final String serviceKey, final String Nx, final String Ny, Map<String,String> headerMap
	) throws KMAException{
		final ProviderType type = ProviderType.KMA_JSON; 
		SocketHttpClient.ServiceProvider shwc =  new SocketHttpClient.ServiceProvider(type, serviceKey, serverName);
		
		shwc.setDnsName(dnsName);
		shwc.setServerIp(ipaddress);
		shwc.setNx(Nx);
		shwc.setNy(Ny);
		
		SocketHttpClient weatherSockClient = new KMASocketHttpClient(shwc);
		weatherSockClient.setHttpMethod(SocketHttpClient.HTTP_GET);
		
		return weatherSockClient.sendAndResponse(headerMap);
	}
	

	private String getWeatherResponse(
			final String dnsName, final String serverName, final String ipaddress, 
			final String serviceKey, final String Nx, final String Ny, Map<String,String> headerMap
	) throws KMAException{
		final ProviderType type = ProviderType.KMA_XML; 
		SocketHttpClient.ServiceProvider shwc =  new SocketHttpClient.ServiceProvider(type, serviceKey, serverName);
		
		shwc.setDnsName(dnsName);
		shwc.setServerIp(ipaddress);
		shwc.setNx(Nx);
		shwc.setNy(Ny);
		
		SocketHttpClient weatherSockClient = new KMASocketHttpClient(shwc);
		weatherSockClient.setHttpMethod(SocketHttpClient.HTTP_GET);
		
		return weatherSockClient.sendAndResponse(headerMap);
	}
	
}
