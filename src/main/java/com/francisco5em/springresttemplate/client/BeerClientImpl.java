/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.apache.hc.client5.*;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.francisco5em.springresttemplate.model.BeerDTO;
import com.francisco5em.springresttemplate.model.BeerDTOPageImpl;
import com.francisco5em.springresttemplate.model.BeerStyle;

import lombok.RequiredArgsConstructor;

/**
 * Creado por Francisco E.
 */
@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory,
            Integer pageNumber, Integer pageSize) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if (beerName != null) {
            if (!beerName.isEmpty()) {
                uriComponentsBuilder.queryParam("beerName", beerName);
            }
        }

        if (beerStyle != null) {
            uriComponentsBuilder.queryParam("beerStyle", beerStyle);
        }

        if (showInventory != null) {
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }

        if (pageNumber != null) {
            if (pageNumber > -1) {
                uriComponentsBuilder.queryParam("pageNumber", pageNumber);
            }
        }

        if (pageSize != null) {
            if (pageSize > 0) {
                uriComponentsBuilder.queryParam("pageSize", pageSize);
            }
        }

        ResponseEntity<BeerDTOPageImpl> response = restTemplate
                .getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        // System.out.println(stringResponse.getBody());

        return response.getBody();
    }

    @Override
    public Page<BeerDTO> listBeers() {
        // TODO Auto-generated method stub
        return this.listBeers(null, null, null, null, null);
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        // TODO Auto-generated method stub
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO newDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto);
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(GET_BEER_BY_ID_PATH, beerDto, beerDto.getId());

        return getBeerById(beerDto.getId());
    }

    @Override
    public BeerDTO patchBeer(@Value("${rest.template.rootUrl}") String rootURL, BeerDTO beerDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        
        restTemplate.setRequestFactory(requestFactory);
        
        URI uri=UriComponentsBuilder.fromPath(GET_BEER_BY_ID_PATH)
                .build(beerDto.getId());
         //URI uri = restTemplate.postForLocation(GET_BEER_BY_ID_PATH, beerDto);
        //GET_BEER_BY_ID_PATH
         
         restTemplate.patchForObject(uri, beerDto, BeerDTO.class);
        
        //restTemplate.patchForObject(GET_BEER_PATH+"/"+beerDto.getId(), beerDto, BeerDTO.class);

        return getBeerById(beerDto.getId());

    }
    
    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.delete(GET_BEER_BY_ID_PATH, beerId);
    }

}
