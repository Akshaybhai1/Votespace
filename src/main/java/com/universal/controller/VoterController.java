package com.universal.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.entities.AppUser;
import com.universal.entities.Election;
import com.universal.entities.ElectionStatus;
import com.universal.entities.Vote;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;
import com.universal.repository.CandidateRepository;
import com.universal.service.ElectionService;
import com.universal.service.ResultsService;
import com.universal.service.VoteService;

@Controller
@RequestMapping("/voter")
public class VoterController {

	private final AppUserRepository users;
	private final CandidateRepository candidates;
	private final ElectionService elections;
	private final VoteService voteService;
	private final ResultsService results;

	public VoterController(AppUserRepository users, CandidateRepository candidates, ElectionService elections,
			VoteService voteService, ResultsService results) {
		this.users = users;
		this.candidates = candidates;
		this.elections = elections;
		this.voteService = voteService;
		this.results = results;
	}

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
		AppUser voter = users.findByEmail(principal.getName())
				.orElseThrow(() -> new AppException("Voter account not found."));
		Election e = elections.get();
		ElectionStatus status = elections.status(e);
		Vote vote = voteService.findVote(voter.getId()).orElse(null);

		model.addAttribute("election", e);
		model.addAttribute("status", status);
		model.addAttribute("candidates", candidates.findAllByOrderByUserFullNameAsc());
		model.addAttribute("summary", results.summary());
		model.addAttribute("vote", vote);
		model.addAttribute("hasVoted", vote != null);
		model.addAttribute("canVote", vote == null && status == ElectionStatus.LIVE);
		return "voter/dashboard";
	}

	@PostMapping("/vote")
	public String vote(@RequestParam("candidateId") Long candidateId, Principal principal, RedirectAttributes ra) {
		try {
			voteService.castVote(principal.getName(), candidateId);
			ra.addFlashAttribute("success", "Your vote has been recorded. Thank you for voting!");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/voter/dashboard";
	}
}
