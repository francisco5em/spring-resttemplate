/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.francisco5em.springresttemplate.model.BeerDTO;
import com.francisco5em.springresttemplate.model.BeerDTOPageImpl;

import lombok.RequiredArgsConstructor;

/**
 * Creado por Francisco E.
 */
@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

	private final RestTemplateBuilder restTemplateBuilder;

	private static final String HOST_URL = "http://localhost:8080";
	private static final String GET_BEER_PATH = "/api/v1/beer";

	@Override
	public Page<BeerDTO> listBeers() {
		RestTemplate restTemplate = restTemplateBuilder.build();

		ResponseEntity<BeerDTOPageImpl> stringResponse =
                restTemplate.getForEntity(HOST_URL + GET_BEER_PATH , BeerDTOPageImpl.class);

		//System.out.println(stringResponse.getBody());

		return null;
	}

}
