package com.universal.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universal.dto.ResultsSummary;
import com.universal.service.ResultsService;

/** JSON feed used by the charts to refresh themselves (logged-in users only). */
@RestController
public class ResultsApiController {

	private final ResultsService results;

	public ResultsApiController(ResultsService results) {
		this.results = results;
	}

	@GetMapping("/api/results")
	public ResultsSummary results() {
		return results.summary();
	}
}
