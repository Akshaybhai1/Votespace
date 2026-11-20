package com.universal.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.entities.AppUser;
import com.universal.entities.Election;
import com.universal.entities.Role;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;
import com.universal.service.ElectionService;
import com.universal.service.MailService;
import com.universal.service.ResultsService;
import com.universal.service.VoteService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final AppUserRepository users;
	private final ElectionService elections;
	private final ResultsService results;
	private final VoteService voteService;
	private final MailService mailService;

	public AdminController(AppUserRepository users, ElectionService elections, ResultsService results,
			VoteService voteService, MailService mailService) {
		this.users = users;
		this.elections = elections;
		this.results = results;
		this.voteService = voteService;
		this.mailService = mailService;
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		Election e = elections.get();
		model.addAttribute("election", e);
		model.addAttribute("status", elections.status(e));
		model.addAttribute("summary", results.summary());
		model.addAttribute("candidateCount", users.countByRole(Role.CANDIDATE));
		model.addAttribute("recentVoters", users.findTop5ByRoleOrderByCreatedAtDesc(Role.VOTER));
		return "admin/dashboard";
	}

	@GetMapping("/election")
	public String electionForm(Model model) {
		Election e = elections.get();
		model.addAttribute("election", e);
		model.addAttribute("status", elections.status(e));
		model.addAttribute("zone", ZoneId.systemDefault().getId());
		return "admin/election";
	}

	@PostMapping("/election")
	public String saveElection(@RequestParam("title") String title,
			@RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
			@RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
			@RequestParam(value = "notifyUsers", defaultValue = "false") boolean notifyUsers,
			RedirectAttributes ra) {
		try {
			Election e = elections.update(title, startTime, endTime);
			String msg = "Election schedule saved.";
			if (notifyUsers) {
				List<AppUser> recipients = users.findByRoleIn(List.of(Role.VOTER, Role.CANDIDATE));
				mailService.sendElectionNotice(recipients, e);
				msg += " E-mail notices are being sent to " + recipients.size() + " people.";
			}
			ra.addFlashAttribute("success", msg);
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/election";
	}

	@PostMapping("/election/reset-votes")
	public String resetVotes(RedirectAttributes ra) {
		voteService.resetVotes();
		ra.addFlashAttribute("success", "All votes were cleared. Every voter can vote again.");
		return "redirect:/admin/election";
	}
}
