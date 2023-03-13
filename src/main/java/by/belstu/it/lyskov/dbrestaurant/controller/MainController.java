package by.belstu.it.lyskov.dbrestaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping({"", "/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/account/**")
    public String accountProfile() {
        return "account";
    }

    @GetMapping("/managing")
    public String managing() {
        return "managing";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
