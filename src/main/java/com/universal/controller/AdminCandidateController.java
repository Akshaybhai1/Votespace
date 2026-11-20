package com.universal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.entities.AppUser;
import com.universal.entities.Candidate;
import com.universal.exception.AppException;
import com.universal.repository.CandidateRepository;
import com.universal.service.ResultsService;
import com.universal.service.UserManagementService;

@Controller
@RequestMapping("/admin/candidates")
public class AdminCandidateController {

	private final CandidateRepository candidates;
	private final ResultsService results;
	private final UserManagementService service;

	public AdminCandidateController(CandidateRepository candidates, ResultsService results,
			UserManagementService service) {
		this.candidates = candidates;
		this.results = results;
		this.service = service;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("candidates", candidates.findAllByOrderByUserFullNameAsc());
		model.addAttribute("voteCounts", results.voteCounts());
		return "admin/candidates";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		Candidate c = new Candidate();
		c.setUser(new AppUser());
		model.addAttribute("candidate", c);
		model.addAttribute("editing", false);
		return "admin/candidate-form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
		Candidate c = candidates.findById(id).orElse(null);
		if (c == null) {
			ra.addFlashAttribute("error", "Candidate not found.");
			return "redirect:/admin/candidates";
		}
		model.addAttribute("candidate", c);
		model.addAttribute("editing", true);
		return "admin/candidate-form";
	}

	@PostMapping("/save")
	public String save(@RequestParam(value = "id", required = false) Long id,
			@RequestParam("fullName") String fullName, @RequestParam("email") String email,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "party", required = false) String party,
			@RequestParam(value = "manifesto", required = false) String manifesto,
			@RequestParam(value = "photo", required = false) MultipartFile photo, Model model,
			RedirectAttributes ra) {
		try {
			if (id == null) {
				FlashHelper.registration(ra,
						service.registerCandidate(fullName, email, phone, party, manifesto, photo), "Candidate",
						false);
			} else {
				service.updateCandidate(id, fullName, email, phone, party, manifesto, photo);
				ra.addFlashAttribute("success", "Candidate updated.");
			}
			return "redirect:/admin/candidates";
		} catch (AppException ex) {
			Candidate typed = new Candidate();
			typed.setId(id);
			AppUser u = new AppUser();
			u.setFullName(fullName);
			u.setEmail(email);
			u.setPhone(phone);
			if (id != null) {
				candidates.findById(id).ifPresent(old -> u.setPhoto(old.getUser().getPhoto()));
			}
			typed.setUser(u);
			typed.setParty(party);
			typed.setManifesto(manifesto);
			model.addAttribute("candidate", typed);
			model.addAttribute("editing", id != null);
			model.addAttribute("error", ex.getMessage());
			return "admin/candidate-form";
		}
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
		try {
			service.deleteCandidate(id);
			ra.addFlashAttribute("success", "Candidate deleted (their votes were removed too).");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/candidates";
	}

	@PostMapping("/{id}/reset-password")
	public String resetPassword(@PathVariable("id") Long id, RedirectAttributes ra) {
		try {
			Candidate c = candidates.findById(id).orElseThrow(() -> new AppException("Candidate not found."));
			FlashHelper.registration(ra, service.resetPassword(c.getUser().getId()), "Candidate", true);
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/candidates";
	}
}
