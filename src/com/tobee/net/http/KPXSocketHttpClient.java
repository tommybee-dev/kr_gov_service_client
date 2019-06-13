package com.tobee.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;

public class KPXSocketHttpClient extends SocketHttpClient {

	public KPXSocketHttpClient(ServiceProvider provider) {
		super(provider);
	}
	
	protected String getReqLineSpec(final String dateString) throws KMAException
	{
		String dnsName = null;
		StringBuilder stbuld = new StringBuilder();
		
		if(provider.canResolve())
			dnsName = provider.getServername();
		else
			dnsName = provider.getDnsName();
		
		String protocol = PROTOCOL_HTTPS;
		String httpMethod = getHttpMethod();
		String uri = provider.getUri();
		String serviceKey = provider.getServerKey();
		
		stbuld.append(httpMethod).append(SPACE);
		stbuld.append(protocol).append("://").append(dnsName).append(uri).append("?");
		stbuld.append("ServiceKey").append("=").append(serviceKey);
		stbuld.append(SPACE).append(HTTP_SPEC);
		
		return stbuld.toString();
	}
	
	protected String getReqLineSpecJson(final String dateString)
	{
		
		return null;
	}
	
	public String sendAndResponseKPX(Map<String, String> httpHeaderMap, String dateString) throws KMAException {

		StringBuilder stbuld = new StringBuilder();

		String response = null;
		BufferedReader in = null;
		PrintWriter out = null;
		Socket socket = null;
		SSLSocket ssocket = null;
		SocketFactory socketFactory = null;
		try {
			socketFactory = sslSocketFactory();
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		try {
			if(provider.canResolve())
				ssocket = (SSLSocket) socketFactory.createSocket(provider.getServerIpNet(), 443); 
			else
				ssocket = (SSLSocket) socketFactory.createSocket(provider.getServerIp(), 443);  
			
			ssocket.startHandshake();
			in = new BufferedReader(new InputStreamReader(ssocket.getInputStream(), DEFAULT_ENCODING));
			out = new PrintWriter(new OutputStreamWriter(ssocket.getOutputStream()), true);
			
			stbuld.append(getReqLineSpec(dateString)).append(ENTER);
			stbuld.append(getHeaderSpec(httpHeaderMap)).append(ENTER).append(ENTER);
			
			out.print(stbuld.toString());
			
			out.flush();
			
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
				if (ssocket != null)
					ssocket.close();
			} catch (IOException e) {
			}
		}

		return response;
	}


	@Override
	protected String sendAndResponseKPX(Map<String, String> httpHeaderMap) throws KMAException {
		return sendAndResponseKPX(httpHeaderMap, null);
	}

	@Override
	protected String sendAndResponseKMA_XML(Map<String, String> httpHeaderMap, String dateString) throws KMAException {
		throw new KMAException("No sendAndResponseKMA_XML implementation of the KPX service");
	}

	@Override
	protected String sendAndResponseKMA_XML(Map<String, String> httpHeaderMap) throws KMAException {
		throw new KMAException("No sendAndResponseKMA_XML implementation of the KPX service");
	}

	@Override
	protected String sendAndResponseKMA_JSON(Map<String, String> httpHeaderMap, String dateString) throws KMAException {
		throw new KMAException("No sendAndResponseKMA_JSON implementation of the KPX service");
	}

	@Override
	protected String sendAndResponseKMA_JSON(Map<String, String> httpHeaderMap) throws KMAException {
		throw new KMAException("No sendAndResponseKMA_JSON implementation of the KPX service");
	}
}
