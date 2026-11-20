package com.universal.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.dto.CandidateResult;
import com.universal.dto.ResultsSummary;
import com.universal.entities.Candidate;
import com.universal.entities.Election;
import com.universal.exception.AppException;
import com.universal.repository.CandidateRepository;
import com.universal.service.ElectionService;
import com.universal.service.ResultsService;
import com.universal.service.UserManagementService;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

	private final CandidateRepository candidates;
	private final ElectionService elections;
	private final ResultsService results;
	private final UserManagementService service;

	public CandidateController(CandidateRepository candidates, ElectionService elections, ResultsService results,
			UserManagementService service) {
		this.candidates = candidates;
		this.elections = elections;
		this.results = results;
		this.service = service;
	}

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
		Candidate me = candidates.findByUserEmail(principal.getName())
				.orElseThrow(() -> new AppException("Candidate profile not found."));
		Election e = elections.get();
		ResultsSummary summary = results.summary();
		CandidateResult mine = summary.getCandidates().stream().filter(r -> r.getId().equals(me.getId())).findFirst()
				.orElse(null);

		model.addAttribute("candidate", me);
		model.addAttribute("election", e);
		model.addAttribute("status", elections.status(e));
		model.addAttribute("summary", summary);
		model.addAttribute("mine", mine);
		model.addAttribute("candidateCount", summary.getCandidates().size());
		return "candidate/dashboard";
	}

	@PostMapping("/profile")
	public String updateProfile(Principal principal, @RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "party", required = false) String party,
			@RequestParam(value = "manifesto", required = false) String manifesto,
			@RequestParam(value = "photo", required = false) MultipartFile photo, RedirectAttributes ra) {
		try {
			service.updateOwnProfile(principal.getName(), phone, party, manifesto, photo);
			ra.addFlashAttribute("success", "Your profile was updated.");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/candidate/dashboard";
	}
}
