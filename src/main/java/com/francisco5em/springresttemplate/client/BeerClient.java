/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.francisco5em.springresttemplate.model.*;

/**
 * Creado por Francisco E.
 */
public interface BeerClient {
    
    BeerDTO getBeerById(UUID beerId);
    
    Page<BeerDTO> listBeers();

    Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory,
            Integer pageNumber, Integer pageSize);
    
    BeerDTO createBeer(BeerDTO newDto);

    BeerDTO updateBeer(BeerDTO beerDto);

    BeerDTO patchBeer(BeerDTO beerDto);

    void deleteBeer(UUID beerId);

}
