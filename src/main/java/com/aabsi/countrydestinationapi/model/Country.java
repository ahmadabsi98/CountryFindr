package com.aabsi.countrydestinationapi.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HP
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Country {

	private String country;
	
	private List<String> borders;
}
