package app.api;

import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientAuthenticationConfig {
	private char[] allPassword = "storepass".toCharArray();
    private String keyStore = "classpath:ssl/client.localhost.pfx";
    
    @Bean
    public RestTemplate restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        return restTemplate;
    }
    
    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() throws Exception {    	
    	SSLContext sslContext = SSLContextBuilder
    			.create()
    			.loadKeyMaterial(ResourceUtils.getFile(keyStore), allPassword, allPassword)
    			.loadTrustMaterial(null, new TrustSelfSignedStrategy())
    			.build();
    	
    	//FIXME Remove when you can add the appropriate domain in your hosts file
    	// hosts file	-> c:/Window/System32/etc/diver/hosts or /etc/hosts
    	// 				-> 127.0.0.1	local.localhost
    	// IMPORTANT: To disable Host Name validation, when run as localhost.
    	// allowAllHostnameVerifier() or .setHostnameVerifier(AllowAllHostnameVerifier.INSTANCE) 
    	// or new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
				 sslContext, NoopHostnameVerifier.INSTANCE);

		HttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                //.setHostnameVerifier(AllowAllHostnameVerifier.INSTANCE)
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }
    
    protected void allowAllHostnameVerifier() {
    	final Properties props = System.getProperties(); 
    	props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
    }
}
