package app.tools;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

@Component
public class WebClientUtil {
	@Autowired
	private ProxyHelper helper;

	public WebClient webClientCreate() throws SSLException {
		 //https://github.com/spring-projects/spring-framework/issues/22309
        final SslContext sslContext = SslContextBuilder.forClient().build();
		HttpClient httpClient = HttpClient.create()
				.secure(t -> t.sslContext(sslContext))
				.tcpConfiguration(tcpClient ->
					tcpClient.option(
						ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000
					).doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10))
                    ).proxy(proxy -> {
                    	proxy.type(ProxyProvider.Proxy.HTTP)
                    	.host(helper.HTTP_PROXY_HOST)
                    	.port(helper.HTTP_PROXY_PORT);
                    })
				);
		return  WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))				
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}
