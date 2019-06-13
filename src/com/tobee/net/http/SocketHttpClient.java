package com.tobee.net.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public abstract class SocketHttpClient implements HttpOpt {
	
	static InetAddress getInetAddress(final String dnsname)
	{
		InetAddress address = null;
		try {
			address = InetAddress.getByName(dnsname);
		} catch (UnknownHostException e) {
		}
		
		return address;
	}
	
	public static final class ProviderType
	{
		public static ProviderType KMA_XML = new ProviderType();
		public static ProviderType KMA_JSON = new ProviderType();
		public static ProviderType KPX = new ProviderType();
		
		public String toDomainName()
		{
			if(this == KMA_XML || this == KMA_JSON)
				return "kma.go.kr";
			else if(this == KPX)
				return "kpx.or.kr";
			else
				return "no provider";
		}
		
		@Override
		public String toString()
		{
			if(this == KMA_XML)
				return "KMA_XML";
			else if(this == KMA_JSON)
				return "KMA_JSON";
			else if(this == KPX)
				return "KPX";
			else
				return "no provider";
		}
	}
	
	public static final class ServiceProvider
	{
		private String ServerKey;
		private String ServerName, ServerIp, DnsName;
		private InetAddress ServerIpNet;
		private boolean canResolve;
		private final ProviderType type;
		
		private String nx,ny;
		
		public ServiceProvider(final ProviderType type, final String serverKey, final String ServerName)
		{
			this.type = type;
			setServerKey(serverKey);
			setServerName(ServerName);
		}
		
		private void setServerKey(final String serverKey)
		{
			ServerKey = serverKey;
		}
		
		public String getServerKey()
		{
			return ServerKey;
		}
		
		private void setServerName(final String ServerName)
		{
			if(ServerName == null)
			{
				canResolve = false;
			}
			else
			{
				this.ServerName = ServerName;
				if((ServerIpNet = getInetAddress(ServerName)) != null)
				{
					canResolve = true;
				}
				else
				{
					canResolve = false;
				}
			}
		}
		
		public ProviderType getProviderType()
		{
			return type;
		}
		
		public boolean canResolve()
		{
			return canResolve;
		}
		
		public String getServername()
		{
			return ServerName;
		}
		
		public String getServerIp()
		{
			return ServerIp;
		}
		
		public void setServerIp(final String serverip)
		{
			ServerIp = serverip;
		}
		
		public InetAddress getServerIpNet()
		{
			return ServerIpNet;
		}
		
		public String getUri()
		{
			if(type == ProviderType.KMA_XML || type == ProviderType.KMA_JSON)
				return "/service/SecndSrtpdFrcstInfoService2/ForecastTimeData";
			else if(type == ProviderType.KPX)
				return "/openapi/sukub5mMaxDatetime/getSukub5mMaxDatetime";
			else
				return "";
		}

		public String getNx() {
			return nx;
		}

		public void setNx(String nx) {
			this.nx = nx;
		}

		public String getNy() {
			return ny;
		}

		public void setNy(String ny) {
			this.ny = ny;
		}

		public String getDnsName() {
			return DnsName;
		}

		public void setDnsName(String dnsName) {
			DnsName = dnsName;
		}
	}
	
	protected final ServiceProvider provider;
	private String mMtd;
	
	public SocketHttpClient(final ServiceProvider provider)
	{
		this.provider = provider;
	}
	
	public void setHttpMethod(final String method)
	{
		mMtd = method;
	}
	
	public String getHttpMethod()
	{
		return mMtd;
	}
	
	protected abstract String getReqLineSpec(final String dateString) throws KMAException;
	protected abstract String getReqLineSpecJson(final String dateString) throws KMAException;
	
	protected abstract String sendAndResponseKPX(Map<String, String> httpHeaderMap, final String dateString) throws KMAException;
	protected abstract String sendAndResponseKPX(Map<String, String> httpHeaderMap) throws KMAException;
	
	protected abstract String sendAndResponseKMA_XML(Map<String, String> httpHeaderMap, final String dateString) throws KMAException;
	protected abstract String sendAndResponseKMA_XML(Map<String, String> httpHeaderMap) throws KMAException;
	
	protected abstract String sendAndResponseKMA_JSON(Map<String, String> httpHeaderMap, final String dateString) throws KMAException;
	protected abstract String sendAndResponseKMA_JSON(Map<String, String> httpHeaderMap) throws KMAException;
	
	public String sendAndResponse(Map<String, String> httpHeaderMap, final String dateString) throws KMAException
	{
		if(provider.type == ProviderType.KMA_JSON)
		{
			return sendAndResponseKMA_JSON(httpHeaderMap, dateString);
		}
		else if(provider.type == ProviderType.KMA_XML)
		{
			return sendAndResponseKMA_XML(httpHeaderMap, dateString);
		}
		else if(provider.type == ProviderType.KPX)
		{
			return sendAndResponseKPX(httpHeaderMap, dateString);
		}
		else
		{
			throw new KMAException("NotAOneOfThem Exception: KMA_JSON|KMA_XML|KPX");
		}
	}
	
	public String sendAndResponse(Map<String, String> httpHeaderMap) throws KMAException
	{
		if(provider.type == ProviderType.KMA_JSON)
		{
			return sendAndResponseKMA_JSON(httpHeaderMap);
		}
		else if(provider.type == ProviderType.KMA_XML)
		{
			return sendAndResponseKMA_XML(httpHeaderMap);
		}
		else if(provider.type == ProviderType.KPX)
		{
			return sendAndResponseKPX(httpHeaderMap);
		}
		else
		{
			throw new KMAException("NotAOneOfThem Exception: KMA_JSON|KMA_XML|KPX");
		}
	}
	
	protected String getHeaderSpec(Map<String, String> httpHeaderMap)
	{
		Set<String> keyset = httpHeaderMap.keySet();
		
		StringBuilder stbuld = new StringBuilder();
		
		for(Iterator<String> keyIter = keyset.iterator(); keyIter.hasNext();)
		{
			String key = keyIter.next();
			
			stbuld.append(key).append(":").append(SPACE).append(httpHeaderMap.get(key)).append(ENTER);
		}
		
		return stbuld.toString();
	}
	
	public SocketFactory sslSocketFactory() throws IOException, NoSuchAlgorithmException, KeyManagementException {
		SocketFactory socketFactory = null;
		
		// SSL setting
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// client certification check
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

				try {
					String cacertPath = System.getProperty("java.home") + "/lib/security/cacerts";
					KeyStore trustStore = KeyStore.getInstance("JKS"); 
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
					trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray());
					trustManagerFactory.init(trustStore);
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} }, null);
		socketFactory = context.getSocketFactory();

		return socketFactory;
	}
}
