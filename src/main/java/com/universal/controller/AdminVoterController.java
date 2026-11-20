package com.universal.controller;

import java.util.HashSet;

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
import com.universal.entities.Role;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;
import com.universal.repository.VoteRepository;
import com.universal.service.UserManagementService;

@Controller
@RequestMapping("/admin/voters")
public class AdminVoterController {

	private final AppUserRepository users;
	private final VoteRepository voteRepo;
	private final UserManagementService service;

	public AdminVoterController(AppUserRepository users, VoteRepository voteRepo, UserManagementService service) {
		this.users = users;
		this.voteRepo = voteRepo;
		this.service = service;
	}
	@PostMapping("/{id}/make-admin")
	public String makeAdmin(@PathVariable("id") Long id, RedirectAttributes ra) {
		try {
			service.promoteVoterToAdmin(id);
			ra.addFlashAttribute("success", "Voter promoted to admin.");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/voters";
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("voters", users.findByRoleOrderByFullNameAsc(Role.VOTER));
		model.addAttribute("votedIds", new HashSet<>(voteRepo.findVotedVoterIds()));
		return "admin/voters";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("person", new AppUser());
		model.addAttribute("editing", false);
		return "admin/voter-form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
		AppUser u = users.findById(id).filter(x -> x.getRole() == Role.VOTER).orElse(null);
		if (u == null) {
			ra.addFlashAttribute("error", "Voter not found.");
			return "redirect:/admin/voters";
		}
		model.addAttribute("person", u);
		model.addAttribute("editing", true);
		return "admin/voter-form";
	}

	@PostMapping("/save")
	public String save(@RequestParam(value = "id", required = false) Long id,
			@RequestParam("fullName") String fullName, @RequestParam("email") String email,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "photo", required = false) MultipartFile photo, Model model,
			RedirectAttributes ra) {
		try {
			if (id == null) {
				FlashHelper.registration(ra, service.registerVoter(fullName, email, phone, photo), "Voter", false);
			} else {
				service.updateVoter(id, fullName, email, phone, photo);
				ra.addFlashAttribute("success", "Voter updated.");
			}
			return "redirect:/admin/voters";
		} catch (AppException ex) {
			// show the form again with what the admin typed
			AppUser typed = new AppUser();
			typed.setId(id);
			typed.setFullName(fullName);
			typed.setEmail(email);
			typed.setPhone(phone);
			if (id != null) {
				users.findById(id).ifPresent(old -> typed.setPhoto(old.getPhoto()));
			}
			model.addAttribute("person", typed);
			model.addAttribute("editing", id != null);
			model.addAttribute("error", ex.getMessage());
			return "admin/voter-form";
		}
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
		try {
			service.deleteVoter(id);
			ra.addFlashAttribute("success", "Voter deleted.");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/voters";
	}

	@PostMapping("/{id}/reset-password")
	public String resetPassword(@PathVariable("id") Long id, RedirectAttributes ra) {
		try {
			FlashHelper.registration(ra, service.resetPassword(id), "Voter", true);
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/voters";
	}
}
