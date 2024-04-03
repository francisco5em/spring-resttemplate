/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import com.francisco5em.springresttemplate.model.BeerDTO;
import com.francisco5em.springresttemplate.model.BeerStyle;

/**
 * Creado por Francisco E.
 */
@SpringBootTest
class BeerClientImplTest {
    @Autowired
    BeerClientImpl beerClient;

    /**
     * Test method for listing all beers using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#listBeers()}.
     */
    @Test
    void listBeersByNoName() {
        Page<BeerDTO> response = beerClient.listBeers();

        assertThat(response.toList().size()).isGreaterThan(0);
        assertThatObject(response.toList().get(0)).isNotNull();
    }

    /**
     * Test method for listing all bears that contain a name using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#listBeers(String,
     * com.francisco5em.springresttemplate.model.BeerStyle, Boolean, Integer,
     * Integer))}.
     */
    @Test
    void listBeersByName() {
        Page<BeerDTO> response = beerClient.listBeers("ALE", null, null, null, null);

        assertThat(response.toList().size()).isGreaterThan(0);
        assertThatObject(response.toList().get(0)).isNotNull();
        System.out.println(response.toList().get(0));
    }

    /**
     * Test method for listing all bears that contain a BeerStyle using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#listBeers(String,
     * com.francisco5em.springresttemplate.model.BeerStyle, Boolean, Integer,
     * Integer))}.
     */
    @Test
    void listBeersByStyle() {
        Page<BeerDTO> response = beerClient.listBeers(null, BeerStyle.WHEAT, null, null, null);

        assertThat(response.toList().size()).isGreaterThan(0);
        assertThatObject(response.toList().get(0)).isNotNull();
        System.out.println(response.toList().get(0));
    }

    /**
     * Test method for listing all bears by ShowInventory using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#listBeers(String,
     * com.francisco5em.springresttemplate.model.BeerStyle, Boolean, Integer,
     * Integer))}.
     */
    @Test
    void listBeersByShowInventory() {
        Page<BeerDTO> response = beerClient.listBeers(null, null, true, null, null);

        assertThat(response.toList().size()).isGreaterThan(0);
        assertThatObject(response.toList().get(0)).isNotNull();
        System.out.println(response.toList().get(0));
    }

    /**
     * Test method for listing all bears by pageNumber and pageSize using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#listBeers(String,
     * com.francisco5em.springresttemplate.model.BeerStyle, Boolean, Integer,
     * Integer))}.
     */
    @Test
    void listBeersByPageNumberAndPageSize() {
        Page<BeerDTO> responseAll = beerClient.listBeers();
        Page<BeerDTO> response = beerClient.listBeers(null, null, null, 2, 5);

        assertThat(response.toList().size()).isEqualTo(5);
        assertThatObject(response.toList().get(0)).isNotNull();
        assertThatObject(response.toList().get(0)).isEqualTo(responseAll.toList().get(5));
        System.out.println(response.toList().get(0));
    }

    /**
     * Test method for geting an specific beer by an UUID using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#getBeerById(java.util.UUID)))}.
     */
    @Test
    void testGetBeerById() {
        Page<BeerDTO> beerDTOS = beerClient.listBeers();

        BeerDTO dto = beerDTOS.getContent().get(0);

        BeerDTO byId = beerClient.getBeerById(dto.getId());
        assertThatObject(byId).isEqualTo(dto);

        assertNotNull(byId);

    }

    /**
     * Test method for creating a beer using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#createBeer(BeerDTO)))}.
     */
    @Test
    void testCreateBeer() {

        BeerDTO newDto = BeerDTO.builder().price(new BigDecimal("10.99")).beerName("Mango Bobs")
                .beerStyle(BeerStyle.IPA).quantityOnHand(500).upc("123245").build();

        BeerDTO savedDto = beerClient.createBeer(newDto);

        assertNotNull(savedDto);

        BeerDTO byId = beerClient.getBeerById(savedDto.getId());
        assertThatObject(byId).isEqualTo(savedDto);

    }

    /**
     * Test method for updating an specific beer by an UUID using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#updateBeer(BeerDTO)))}.
     */
    @Test
    void testUpdateBeer() {

        BeerDTO newDto = BeerDTO.builder().price(new BigDecimal("10.99")).beerName("Mango Bobs 2")
                .beerStyle(BeerStyle.IPA).quantityOnHand(500).upc("123245").build();

        BeerDTO beerDto = beerClient.createBeer(newDto);

        final String newName = "Mango Bobs 3";
        beerDto.setBeerName(newName);
        BeerDTO updatedBeer = beerClient.updateBeer(beerDto);

        assertEquals(newName, updatedBeer.getBeerName());
    }

    /**
     * Test method for patching an specific beer by an UUID using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#patchBeer(BeerDTO)))}.
     */
    @Test
    void testPatchBeer() {
        BeerDTO newDto = BeerDTO.builder().price(new BigDecimal("10.99")).beerName("Mango Bobs 2")
                .beerStyle(BeerStyle.IPA).quantityOnHand(500).upc("123245").build();

        BeerDTO beerDto = beerClient.createBeer(newDto);

        beerDto.setBeerStyle(BeerStyle.IPA);
        beerDto.setQuantityOnHand(50);
        BeerDTO updatedBeer = beerClient.patchBeer(BeerClientMockTest.HOST_URL,beerDto);

        assertEquals(BeerStyle.IPA, updatedBeer.getBeerStyle());
        assertEquals(50, updatedBeer.getQuantityOnHand());
    }

    /**
     * Test method for deleting an specific beer by an UUID using:
     * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#deleteBeer(java.util.UUID)))}.
     */
    @Test
    void testDeleteBeer() {
        BeerDTO newDto = BeerDTO.builder().price(new BigDecimal("10.99")).beerName("Mango Bobs 2")
                .beerStyle(BeerStyle.IPA).quantityOnHand(500).upc("123245").build();

        BeerDTO beerDto = beerClient.createBeer(newDto);

        beerClient.deleteBeer(beerDto.getId());

        assertThrows(HttpClientErrorException.class, () -> {
            // should error
            beerClient.getBeerById(beerDto.getId());
        });
    }

}
