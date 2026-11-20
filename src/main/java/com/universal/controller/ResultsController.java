package com.universal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.universal.entities.Election;
import com.universal.service.ElectionService;
import com.universal.service.ResultsService;

/** The common results page with graphs - open to every logged-in user. */
@Controller
public class ResultsController {

	private final ResultsService results;
	private final ElectionService elections;

	public ResultsController(ResultsService results, ElectionService elections) {
		this.results = results;
		this.elections = elections;
	}

	@GetMapping("/results")
	public String results(Model model) {
		Election e = elections.get();
		model.addAttribute("election", e);
		model.addAttribute("status", elections.status(e));
		model.addAttribute("summary", results.summary());
		return "results";
	}
}
