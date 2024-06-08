package com.asm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.asm.entity.Customers;
import com.asm.entity.Users;
import com.asm.entity.dao.CustomersDAO;
import com.asm.entity.dao.UsersDAO;
import com.asm.services.SessionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/app")
public class LoginController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private CustomersDAO customersDAO;

    @GetMapping("/user/login")
    public String loginForm(Model model) {
        // Kiểm tra nếu người dùng đã đăng nhập, chuyển hướng đến trang chính
        if (sessionService.get("loggedInUser") != null) {
            return "redirect:/home/login";
        }
        model.addAttribute("user", new Users());
        return "user/login";
    }

    @PostMapping("/user/login")
    public String login(@Valid @ModelAttribute("user") Users user, BindingResult result, Model model) {
        if (loginSuccessful(user)) {
            sessionService.set("loggedInUser", user.getUsername());

            Users usersIdByUserName = usersDAO.findByUserIdByUsername(user.getUsername());
            Customers customer = customersDAO.findByUserId(usersIdByUserName.getUserId());

            if (customer != null) {
                sessionService.set("loggedInCustomer", customer);
            }

            model.addAttribute("role", usersIdByUserName.getUserRole().getRoleId());
            sessionService.set("roleUserLogin", usersIdByUserName.getUserRole().getRoleId());

            return "redirect:/home/index";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "user/login";
        }
    }

    @GetMapping("/user/index")
    public String index(Model model) {
        return "user/index";
    }

    @GetMapping("/user/logout")
    public String logout() {
        sessionService.remove("loggedInUser");
        sessionService.remove("loggedInCustomer");
        return "redirect:/home/index";
    }

    // Phương thức kiểm tra đăng nhập thành công
    private boolean loginSuccessful(Users user) {
        Users existingUser = usersDAO.findByUsername(user.getUsername());
        return existingUser != null && existingUser.getPasswordUser().equals(user.getPasswordUser());
    }
}
