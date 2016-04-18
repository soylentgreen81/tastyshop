package de.beukmann.controllers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
 
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@RestController
@RequestMapping("/lecker/")
public class MeyerProxyController {

	@Autowired
	private RestTemplate restTemplate;
	
	private final String url = "https://shop.meyer-menue.de/api/v1/menue/MDI5ODM3/year/{year}/week/{week}";
	private Cache<String, String> meyerCache;
	
	@PostConstruct
	private void init() {
		
		meyerCache = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(7, TimeUnit.DAYS).build();
	}

	@RequestMapping(method = RequestMethod.GET, path = { "menu/{year}/{week}" })
	public String loadMenu(@PathVariable String year, @PathVariable String week) throws ExecutionException {
		return meyerCache.get(year + "/" +  week,
				() -> restTemplate.getForEntity(url, String.class, year, week).getBody());
	}

}
