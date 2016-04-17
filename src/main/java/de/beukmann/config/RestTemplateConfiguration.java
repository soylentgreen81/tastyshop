package de.beukmann.config;


import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    final Logger logger = LogManager.getLogger(RestTemplateConfiguration.class);


    @Value("${proxy.host:}")
    private String host;

    @Value("${proxy.port:}")
    private String port;

    @Bean
    public RestTemplate restTemplate(){
    	RestTemplate restTemplate = new RestTemplate();
        if (!StringUtils.isEmpty(host)){ 
        	int portNr = 8080;
            try {
                portNr = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                logger.error("Unable to parse the proxy port number");
            }
        	SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
	        InetSocketAddress address = new InetSocketAddress(host,portNr);
	        Proxy proxy = new Proxy(Proxy.Type.HTTP,address);
	        factory.setProxy(proxy);
	        restTemplate.setRequestFactory(factory);
        }
        return restTemplate;
    }

}
