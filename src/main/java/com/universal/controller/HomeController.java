
package com.universal.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.universal.config.RoleBasedSuccessHandler;
import com.universal.entities.Election;
import com.universal.service.ElectionService;

@Controller
public class HomeController {

    private final ElectionService elections;

    public HomeController(ElectionService elections) {
        this.elections = elections;
    }

    private boolean hasDashboard(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        String path = RoleBasedSuccessHandler.dashboardFor(auth);

        return !"/login".equals(path);
    }

    @GetMapping("/")
    public String indexPage(Authentication authentication, Model model) {

        if (hasDashboard(authentication)) {
            return "redirect:/dashboard";
        }

        Election e = elections.get();
        model.addAttribute("election", e);
        model.addAttribute("status", elections.status(e));

        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/test-login")
    @ResponseBody
    public String testLogin() {
        System.out.println("TEST LOGIN CONTROLLER HIT");
        return "VoteSpace Controller Working";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {

        if (!hasDashboard(authentication)) {
            return "redirect:/login";
        }

        return "redirect:" +
                RoleBasedSuccessHandler.dashboardFor(authentication);
    }
}
