/**
 * 
 */
package com.francisco5em.springresttemplate.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Creado por Francisco E.
 */
@SpringBootTest
class BeerClientImplTest {
	@Autowired
	BeerClientImpl beerClient;

	/**
	 * Test method for
	 * {@link com.francisco5em.springresttemplate.client.BeerClientImpl#listBeers()}.
	 */
	@Test
	void listBeers() {

		beerClient.listBeers();
	}

}
