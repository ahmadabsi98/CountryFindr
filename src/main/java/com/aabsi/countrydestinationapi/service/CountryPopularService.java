package com.aabsi.countrydestinationapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.aabsi.countrydestinationapi.model.Country;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author HP
 */
@Service
@Slf4j
public class CountryPopularService {

	private final String URL = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";
	
	private OkHttpClient initOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }
	
	public Map<String, List<String>> populate() {
		log.info("populate");
		Map<String, List<String>> data = new HashMap<>();
		OkHttpClient client = initOkHttpClient();
		Request request = new Request.Builder().url(URL).build();
		StringBuilder str = new StringBuilder();
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("No countries - Unexpected code " + response);
			}
			str.append(response.body().string());
			JSONArray json = new JSONArray (str.toString());
			for(int idx = 0; idx < json.length(); idx++) {
				Country c = Country.builder().country("").borders(null).build();
				JSONObject currentJson = json.getJSONObject(idx);
				String country = currentJson.optString("cca3", "n/a");
				JSONArray bordersJson = currentJson.optJSONArray("borders", new JSONArray());
				List<String> bordersData = new ArrayList<>();
				for(int countryIdx = 0; countryIdx < bordersJson.length(); countryIdx++) {
					String border = bordersJson.getString(countryIdx);
					bordersData.add(border);
				}
				c.setBorders(bordersData);
				c.setCountry(country);
				data.put(country, bordersData);
			}
		} catch (Exception e) {
			log.error("Exception @ populate", e);
			return data;
		}
		log.debug("Countries populated ({} countries)", data.size());
		return data;
		
	}
}
