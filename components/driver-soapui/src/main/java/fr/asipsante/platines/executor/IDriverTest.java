/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.executor;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.asipsante.platines.model.DriverResult;

public interface IDriverTest {

	/**
	 * 
	 * @param ctx
	 * @return
	 * @throws JsonProcessingException
	 */
    DriverResult executeClient() throws JsonProcessingException;
    
    
    
    
}
