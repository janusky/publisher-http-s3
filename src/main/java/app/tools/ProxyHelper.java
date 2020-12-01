package app.tools;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProxyHelper {
	@Value("${app.proxy.host:''}")
	public String HTTP_PROXY_HOST;
	@Value("${app.proxy.port:80}")
	public Integer HTTP_PROXY_PORT;

	public Proxy getProxy() {
		log.debug("Create proxy {}:{}", HTTP_PROXY_HOST, HTTP_PROXY_PORT);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(HTTP_PROXY_HOST, HTTP_PROXY_PORT));
		return proxy;
	}
}
