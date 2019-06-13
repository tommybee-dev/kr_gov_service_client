package com.tobee.net.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class KMASocketHttpClient extends SocketHttpClient {

	public KMASocketHttpClient(ServiceProvider provider) {
		super(provider);

	}

	protected String getReqLineSpec(final String dateString) throws KMAException {
		String dnsName = null;
		StringBuilder stbuld = new StringBuilder();

		if (provider.canResolve()) {
			dnsName = provider.getServername();
		} else {
			dnsName = provider.getDnsName();
		}

		String protocol = PROTOCOL_HTTP;
		String httpMethod = getHttpMethod();
		String uri = provider.getUri();
		String serviceKey = provider.getServerKey();
		String baseDate = getBaseDate(dateString);
		String baseTime = getLastBaseTime();
		String nX = provider.getNx();
		String nY = provider.getNy();

		stbuld.append(httpMethod).append(SPACE);
		stbuld.append(protocol).append("://").append(dnsName).append(uri).append("?");
		stbuld.append("ServiceKey").append("=").append(serviceKey);
		stbuld.append("&").append("base_date").append("=").append(baseDate);
		stbuld.append("&").append("base_time").append("=").append(baseTime);
		stbuld.append("&").append("nx").append("=").append(nX);
		stbuld.append("&").append("ny").append("=").append(nY);
		stbuld.append(SPACE).append(HTTP_SPEC);
		
		return stbuld.toString();
	}
	
	
	private String getBaseDate(final String dateString) throws KMAException {
	
		String thisDateString = null;
		
		if (dateString == null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(KMA_DATE_FORMAT);
			thisDateString = dateFormat.format(new Date());
		} else {
			if(dateString.length() != KMA_DATE_FORMAT.length())
				throw new KMAException("The format of this date string must be a " + KMA_DATE_FORMAT);
			
			thisDateString = dateString;
		}
		
		return thisDateString;
	}
	
	private String getLastBaseTime() {
		Calendar now = Calendar.getInstance();	
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);	
		if (minute < 30) hour--;
		
		String time = String.format("%02d00", hour); 

		return time;
	}

	protected String getReqLineSpecJson(final String dateString) throws KMAException {
		String dnsName = null;

		StringBuilder stbuld = new StringBuilder();

		if (provider.canResolve()) {
			dnsName = provider.getServername();
		} else {
			dnsName = provider.getDnsName();
		}

		String protocol = PROTOCOL_HTTP;
		String httpMethod = getHttpMethod();
		String uri = provider.getUri();
		String serviceKey = provider.getServerKey();
		String baseDate = getBaseDate(dateString);
		String baseTime = getLastBaseTime();
		
		String nX = provider.getNx();
		String nY = provider.getNy();

		stbuld.append(httpMethod).append(SPACE);
		stbuld.append(protocol).append("://").append(dnsName).append(uri).append("?");
		stbuld.append("ServiceKey").append("=").append(serviceKey);
		stbuld.append("&").append("base_date").append("=").append(baseDate);
		stbuld.append("&").append("base_time").append("=").append(baseTime);
		stbuld.append("&").append("nx").append("=").append(nX);
		stbuld.append("&").append("ny").append("=").append(nY);
		stbuld.append("&").append("_type=json");
		stbuld.append(SPACE).append(HTTP_SPEC);
		
		return stbuld.toString();
	}

	@Override
	protected String sendAndResponseKPX(Map<String, String> httpHeaderMap, String dateString) throws KMAException {
		throw new KMAException("No sendAndResponseKPX implementation of the Weather service");
	}

	@Override
	protected String sendAndResponseKPX(Map<String, String> httpHeaderMap) throws KMAException {
		throw new KMAException("No sendAndResponseKPX implementation of the Weather service");
	}

	@Override
	protected String sendAndResponseKMA_XML(Map<String, String> httpHeaderMap, String dateString) throws KMAException {
		String response = null;
		BufferedReader in = null;
		PrintWriter out = null;
		Socket socket = null;
		StringBuilder stbuld = new StringBuilder();

		try {
			
			if (provider.canResolve())
				socket = new Socket(provider.getServerIpNet(), 80);
			else
				socket = new Socket(provider.getServerIp(), 80);

			boolean autoflush = true;
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), autoflush);

			stbuld.append(getReqLineSpec(dateString)).append(ENTER);
			stbuld.append(getHeaderSpec(httpHeaderMap)).append(ENTER);

			out.println(stbuld.toString());
			out.flush();
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			
			String line = null;
			stbuld.setLength(0);
			while ((line = in.readLine())!=null) {
				stbuld.append(line).append("$");
			}
			
			line = stbuld.toString();
			int skip = "$$".length();
			int idx = line.indexOf("$$");
			int lst = line.length()-1;
			
			response = line.substring(idx+skip, lst);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
			}
		}

		return response;
	}

	@Override
	protected String sendAndResponseKMA_XML(Map<String, String> httpHeaderMap) throws KMAException {
		return sendAndResponseKMA_XML(httpHeaderMap, null);
	}

	@Override
	protected String sendAndResponseKMA_JSON(Map<String, String> httpHeaderMap, String dateString) throws KMAException {
		StringBuilder stbuld = new StringBuilder();
		Socket socket = null;
		String response = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			if(provider.canResolve())
				socket = new Socket(provider.getServerIpNet(), 80);
			else
				socket = new Socket(provider.getServerIp(), 80);
			
			
			boolean autoflush = true;
			out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), autoflush);
			
			stbuld.append(getReqLineSpecJson(dateString)).append(ENTER);
			stbuld.append(getHeaderSpec(httpHeaderMap)).append(ENTER);
			
			out.println(stbuld.toString());
			
			out.flush();
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String line = null;
			stbuld.setLength(0);
			while ((line = in.readLine())!=null) {
				stbuld.append(line).append("$");
			}
			
			line = stbuld.toString();
			int skip = "$$".length();
			int idx = line.indexOf("$$");
			int lst = line.length()-1;
			
			response = line.substring(idx+skip, lst);
			
		} catch (IOException e) {
			throw new KMAException("Error while processing a weather service from the server.", e);
		}
		finally
		{
			try {
				if(out != null) out.close();
				if(in != null) in.close();
				if(socket != null) socket.close();
			} catch (IOException e) {
			}
		}
		
		return response;
	}

	@Override
	protected String sendAndResponseKMA_JSON(Map<String, String> httpHeaderMap) throws KMAException {
		return sendAndResponseKMA_JSON(httpHeaderMap, null);
	}
}
