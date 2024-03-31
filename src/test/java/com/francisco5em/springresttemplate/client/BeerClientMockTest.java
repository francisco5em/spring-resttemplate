/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.francisco5em.springresttemplate.model.*;
import com.francisco5em.springresttemplate.client.*;
import com.francisco5em.springresttemplate.config.RestTemplateBuilderConfig;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withAccepted;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Creado por Francisco E.
 */
/**
 * Creado por Francisco E.
 */
/**
 * Creado por Francisco E.
 */
@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {

    static final String HOST_URL = "http://localhost:8080";

    BeerClient beerClient;

    MockRestServiceServer server;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(
            new MockServerRestTemplateCustomizer());

    ArrayList<BeerDTO> beerList;

    BeerDTO dto;
    String dtoJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);
        beerList = loadBeerObjects();

        dto = beerList.get(0);
        dtoJson = objectMapper.writeValueAsString(dto);
    }

    /**
     * @throws JsonProcessingException
     */
    @Test
    void testListBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(HOST_URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();
        assertThat(dtos.getContent().size()).isGreaterThan(10);
    }

    @Test
    void testGetBeerById() throws JsonProcessingException {
        String payload_ListBeers = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(HOST_URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload_ListBeers, MediaType.APPLICATION_JSON));

        mockGetOperation();

        BeerDTO dto = beerClient.listBeers().getContent().get(0);

        BeerDTO responseDto = beerClient.getBeerById(dto.getId());
        assertThat(responseDto.getId()).isEqualTo(dto.getId());

    }

    @Test
    void testCreateBeer() throws JsonProcessingException {
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
                .build(dto.getId());

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(HOST_URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withAccepted().location(uri));

        mockGetOperation();

        BeerDTO responseDto = beerClient.createBeer(dto);
        assertThat(responseDto.getId()).isEqualTo(dto.getId());
    }

    @Test
    void testUpdateBeer() {
        BeerDTO updatedDTO = BeerDTO.builder().id(dto.getId()).beerName(dto.getBeerName())
                .beerStyle(dto.getBeerStyle()).createdDate(dto.getCreatedDate())
                .price(dto.getPrice()).quantityOnHand(dto.getQuantityOnHand()).upc(dto.getUpc())
                .updateDate(dto.getUpdateDate()).version(dto.getVersion()).build();
        server.expect(method(HttpMethod.PUT)).andExpect(
                requestToUriTemplate(HOST_URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withNoContent());

        mockGetOperation();

        updatedDTO.setBeerName("MAGIC");

        BeerDTO responseDto = beerClient.updateBeer(updatedDTO);
        assertThat(responseDto.getId()).isEqualTo(updatedDTO.getId());
        // assertThat(dto.getBeerName()).isEqualTo(updatedDTO.getBeerName());
    }

    @Test
    void testPatchBeer() throws JsonProcessingException {
        BeerDTO updatedDTO = BeerDTO.builder().id(dto.getId()).beerName(dto.getBeerName())
                .price(dto.getPrice()).build();

        String newBeerName = "MAGIC BEER";
        BigDecimal newBeerPrice = new BigDecimal(5.13);
        updatedDTO.setBeerName(newBeerName);
        updatedDTO.setPrice(newBeerPrice);

        mockGetOperation();

        server.expect(method(HttpMethod.PATCH))
                .andExpect(requestToUriTemplate(HOST_URL + BeerClientImpl.GET_BEER_BY_ID_PATH,
                        updatedDTO.getId()))
                .andRespond(withNoContent());

        /*
         * withSuccess(
         * objectMapper.writeValueAsString(BeerDTO.builder().id(dto.getId())
         * .beerName(newBeerName).beerStyle(dto.getBeerStyle())
         * .createdDate(dto.getCreatedDate()).price(newBeerPrice)
         * .quantityOnHand(dto.getQuantityOnHand()).upc(dto.getUpc())
         * .updateDate(dto.getUpdateDate()).version(dto.getVersion()).build()),
         * MediaType.APPLICATION_JSON)
         */

        BeerDTO responseDto = beerClient.patchBeer(updatedDTO);

        assertThat(responseDto.getId()).isEqualTo(updatedDTO.getId());
        assertThat(responseDto.getBeerName()).isEqualTo(updatedDTO.getBeerName());
        assertThat(responseDto.getPrice()).isEqualTo(updatedDTO.getPrice());

        // assertThat(dto.getBeerName()).isEqualTo(updatedDTO.getBeerName());
    }

    @Test
    void testDeleteBeer() {
        server.expect(method(HttpMethod.DELETE)).andExpect(
                requestToUriTemplate(HOST_URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(dto.getId());

        server.verify();
    }

    @Test
    void testDeleteNotFound() {
        server.expect(method(HttpMethod.DELETE)).andExpect(
                requestToUriTemplate(HOST_URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> {
            beerClient.deleteBeer(dto.getId());
        });

        server.verify();
    }

    BeerDTOPageImpl getPage() {
        return new BeerDTOPageImpl(beerList, 1, 25, 30);
    }

    BeerDTOPageImpl getPage(String beerName) {
        List<BeerDTO> lisss = beerList.stream().filter(x -> (x.getBeerName().contains(beerName)))
                .collect(Collectors.toList());
        return new BeerDTOPageImpl(lisss, 1, 25, lisss.size());
    }

    @Test
    void testListBeersWithQueryParam() throws JsonProcessingException {
        String beerNameToSearch = "ALE";
        String response = objectMapper.writeValueAsString(getPage(beerNameToSearch));

        URI uri = UriComponentsBuilder.fromHttpUrl(HOST_URL + BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", beerNameToSearch).build().toUri();

        server.expect(method(HttpMethod.GET)).andExpect(requestTo(uri))
                .andExpect(queryParam("beerName", beerNameToSearch))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient.listBeers(beerNameToSearch, null, null, null, null);

        assertThat(responsePage.getContent().size()).isEqualTo(1);
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET)).andExpect(
                requestToUriTemplate(HOST_URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));
    }

    ArrayList<BeerDTO> loadBeerObjects() {
        ArrayList<BeerDTO> listt = new ArrayList<BeerDTO>();

        Random random = new Random();

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Mango Bobs")
                .beerStyle(BeerStyle.ALE).upc(BEER_1_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Galaxic ALE")
                .beerStyle(BeerStyle.PALE_ALE).upc(BEER_2_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("No Hammers On The Bar")
                .beerStyle(BeerStyle.WHEAT).upc(BEER_3_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Blessed")
                .beerStyle(BeerStyle.STOUT).upc(BEER_4_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Adjunct Trail")
                .beerStyle(BeerStyle.STOUT).upc(BEER_5_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Very GGGreenn")
                .beerStyle(BeerStyle.IPA).upc(BEER_6_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Double Barrel Hunahpu's")
                .beerStyle(BeerStyle.STOUT).upc(BEER_7_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Very Hazy")
                .beerStyle(BeerStyle.IPA).upc(BEER_8_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("SR-71")
                .beerStyle(BeerStyle.STOUT).upc(BEER_9_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Pliny the Younger")
                .beerStyle(BeerStyle.IPA).upc(BEER_10_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Blessed")
                .beerStyle(BeerStyle.STOUT).upc(BEER_11_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("King Krush")
                .beerStyle(BeerStyle.IPA).upc(BEER_12_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("PBS Porter")
                .beerStyle(BeerStyle.PORTER).upc(BEER_13_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Pinball Porter")
                .beerStyle(BeerStyle.STOUT).upc(BEER_14_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Golden Budda")
                .beerStyle(BeerStyle.STOUT).upc(BEER_15_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Grand Central Red")
                .beerStyle(BeerStyle.LAGER).upc(BEER_16_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Pac-Man")
                .beerStyle(BeerStyle.STOUT).upc(BEER_17_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Ro Sham Bo")
                .beerStyle(BeerStyle.IPA).upc(BEER_18_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Summer Wheatly")
                .beerStyle(BeerStyle.WHEAT).upc(BEER_19_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Java Jill")
                .beerStyle(BeerStyle.LAGER).upc(BEER_20_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Bike Trail Pale")
                .beerStyle(BeerStyle.PALE_ALE).upc(BEER_21_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("N.Z.P").beerStyle(BeerStyle.IPA)
                .upc(BEER_22_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Stawberry Blond")
                .beerStyle(BeerStyle.WHEAT).upc(BEER_23_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Loco")
                .beerStyle(BeerStyle.PORTER).upc(BEER_24_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Spocktoberfest")
                .beerStyle(BeerStyle.STOUT).upc(BEER_25_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Beach Blond Ale")
                .beerStyle(BeerStyle.ALE).upc(BEER_26_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Bimini Twist IPA")
                .beerStyle(BeerStyle.IPA).upc(BEER_27_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Rod Bender Red Ale")
                .beerStyle(BeerStyle.ALE).upc(BEER_28_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("Floating Dock")
                .beerStyle(BeerStyle.SAISON).upc(BEER_29_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        listt.add(BeerDTO.builder().id(UUID.randomUUID()).beerName("El Hefe")
                .beerStyle(BeerStyle.WHEAT).upc(BEER_30_UPC)
                .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                .quantityOnHand(random.nextInt(5000)).build());

        return listt;
    }

    private static final String BEER_1_UPC = "0631234200036";
    private static final String BEER_2_UPC = "9122089364369";
    private static final String BEER_3_UPC = "0083783375213";
    private static final String BEER_4_UPC = "4666337557578";
    private static final String BEER_5_UPC = "8380495518610";
    private static final String BEER_6_UPC = "5677465691934";
    private static final String BEER_7_UPC = "5463533082885";
    private static final String BEER_8_UPC = "5339741428398";
    private static final String BEER_9_UPC = "1726923962766";
    private static final String BEER_10_UPC = "8484957731774";
    private static final String BEER_11_UPC = "6266328524787";
    private static final String BEER_12_UPC = "7490217802727";
    private static final String BEER_13_UPC = "8579613295827";
    private static final String BEER_14_UPC = "2318301340601";
    private static final String BEER_15_UPC = "9401790633828";
    private static final String BEER_16_UPC = "4813896316225";
    private static final String BEER_17_UPC = "3431272499891";
    private static final String BEER_18_UPC = "2380867498485";
    private static final String BEER_19_UPC = "4323950503848";
    private static final String BEER_20_UPC = "4006016803570";
    private static final String BEER_21_UPC = "9883012356263";
    private static final String BEER_22_UPC = "0583668718888";
    private static final String BEER_23_UPC = "9006801347604";
    private static final String BEER_24_UPC = "0610275742736";
    private static final String BEER_25_UPC = "6504219363283";
    private static final String BEER_26_UPC = "7245173761003";
    private static final String BEER_27_UPC = "0326984155094";
    private static final String BEER_28_UPC = "1350188843012";
    private static final String BEER_29_UPC = "0986442492927";
    private static final String BEER_30_UPC = "8670687641074";

}
