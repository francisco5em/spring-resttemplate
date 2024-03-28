/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import org.springframework.data.domain.Page;

import com.francisco5em.springresttemplate.model.*;

/**
 * Creado por Francisco E.
 */
public interface BeerClient {
	Page<BeerDTO> listBeers();

}
