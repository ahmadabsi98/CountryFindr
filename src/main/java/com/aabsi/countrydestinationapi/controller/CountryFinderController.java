package com.aabsi.countrydestinationapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aabsi.countrydestinationapi.service.CountryFinderService;

/**
 * @author HP
 */
@RestController
@RequestMapping("/api/routing")
public class CountryFinderController {

	@Autowired
	private CountryFinderService countryFinderService;
	
	@GetMapping("/{source}/{destination}")
	public ResponseEntity<?> getPathEp(@PathVariable String source, @PathVariable String destination) {
		return countryFinderService.getPath(source, destination);
	}
}
