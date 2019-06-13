package com.tobee.net;

import com.tobee.net.http.SocketHttpClient;

public class GovPubServiceClient {
	
	public static void main(String[] args) throws Exception {
		final String ServiceType = System.getProperty("service.type");
		
		final String ConnType = System.getProperty("connection.type");
		final String KPXServiceKey = System.getProperty("KPX.service.key");
		final String WeatherServiceKey = System.getProperty("KMA.service.key");
		final String WeatherLocationX = System.getProperty("KMA.location.X");
		final String WeatherLocationY = System.getProperty("KMA.location.Y");

		final int Nx = WeatherLocationX == null ? -1 : Integer.parseInt(WeatherLocationX);
		final int Ny = WeatherLocationY == null ? -1 : Integer.parseInt(WeatherLocationY);
		String response = null;
		
		//System.out.println(SocketHttpClient.ProviderType.KMA.toString() +" : " + ServiceType);
		//System.out.println(WeatherServiceKey +" : " + KPXServiceKey);
		
		if(SocketHttpClient.ProviderType.KMA_XML.toString().equals(ServiceType))
		{
			if(Nx == -1 || Ny == -1) 
			{
				System.out.println("You must specify a X and Y location for the local weather service");
				return;
			}
				
			if(ConnType.toLowerCase().equals("direct"))
			{
				System.out.println("///----------------------- START:: Local Weather with a direct connection ----------------------------");
				response = WeatherPresenter.connectDirectAndGetWeatherInfo(WeatherServiceKey, Nx, Ny, false);
				System.out.println(response);
				System.out.println("----------------------- END:: Local Weather with a direct connection ------------------------------///");
			}
			else if(ConnType.toLowerCase().equals("normal"))
			{
				System.out.println("///----------------------- START:: Local Weather with DNS -----------------------------");
				response = WeatherPresenter.connectAndGetWeatherInfo(WeatherServiceKey, Nx, Ny, false);
				System.out.println(response);
				System.out.println("----------------------- END:: Local Weather with DNS -------------------------------///");
			}
			else
			{
				System.out.println("///----------------------- NOT a valid servie type ----------------------------------------///");
			}
		}
		else if(SocketHttpClient.ProviderType.KMA_JSON.toString().equals(ServiceType))
		{
			if(Nx == -1 || Ny == -1) 
			{
				System.out.println("You must specify a X and Y location for the local weather service");
				return;
			}
				
			if(ConnType.toLowerCase().equals("direct"))
			{
				System.out.println("///----------------------- START:: Local Weather with a direct connection ----------------------------");
				response = WeatherPresenter.connectDirectAndGetWeatherInfo(WeatherServiceKey, Nx, Ny, true);
				System.out.println(response);
				System.out.println("----------------------- END:: Local Weather with a direct connection ------------------------------///");
			}
			else if(ConnType.toLowerCase().equals("normal"))
			{
				System.out.println("///----------------------- START:: Local Weather with DNS -----------------------------");
				response = WeatherPresenter.connectAndGetWeatherInfo(WeatherServiceKey, Nx, Ny, true);
				System.out.println(response);
				System.out.println("----------------------- END:: Local Weather with DNS -------------------------------///");
			}
			else
			{
				System.out.println("///----------------------- NOT a valid servie type ----------------------------------------///");
			}
		}
		else if(SocketHttpClient.ProviderType.KPX.toString().equals(ServiceType))
		{
			if(ConnType.toLowerCase().equals("direct"))
			{
				System.out.println("///----------------------- START:: KPX with a direct connection --------------------------------------");
				response = KPXPresenter.connectDirectAndGetKPXInfo(KPXServiceKey);
				System.out.println(response);
				System.out.println("----------------------- END:: KPX with a direct connection ----------------------------------------///");
			}
			else if(ConnType.toLowerCase().equals("normal"))
			{
				System.out.println("///----------------------- START:: KPX with DNS ---------------------------------------");
				response = KPXPresenter.connectAndGetKPXInfo(KPXServiceKey);
				System.out.println(response);
				System.out.println("----------------------- END:: KPX with DNS -----------------------------------------///");
			}
			else
			{
				System.out.println("///----------------------- NOT a valid servie type ----------------------------------------///");
			}
			
		}
		else
		{
			System.out.println("///----------------------- Service is NOT Available ----------------------------------------///");
		}
				
	}
}
