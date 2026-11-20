package com.universal.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.universal.entities.Role;
import com.universal.exception.AppException;
import com.universal.repository.AppUserRepository;
import com.universal.service.UserManagementService;

/** Admins can see, add and remove other admins. (URL /admin/** is already locked to ADMIN.) */
@Controller
@RequestMapping("/admin/admins")
public class AdminAccountsController {

	private final AppUserRepository users;
	private final UserManagementService service;

	public AdminAccountsController(AppUserRepository users, UserManagementService service) {
		this.users = users;
		this.service = service;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("admins", users.findByRoleOrderByFullNameAsc(Role.ADMIN));
		return "admin/admins";
	}

	@PostMapping("/save")
	public String save(@RequestParam("fullName") String fullName, @RequestParam("email") String email,
			RedirectAttributes ra) {
		try {
			FlashHelper.registration(ra, service.registerAdmin(fullName, email), "Admin", false);
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/admins";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes ra) {
		try {
			service.deleteAdmin(id, principal.getName());
			ra.addFlashAttribute("success", "Admin removed.");
		} catch (AppException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/admin/admins";
	}
}
