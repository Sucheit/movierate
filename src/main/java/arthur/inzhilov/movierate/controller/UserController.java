package arthur.inzhilov.movierate.controller;

import arthur.inzhilov.movierate.dto.UserDtoRegistration;
import arthur.inzhilov.movierate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/registration")
    public String showRegistrationPage(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("user", user);
        model.addAttribute("userDto", new UserDtoRegistration());
        return "user/registration.html";
    }

    @PostMapping(value = "/addUser")
    public String addNewUser(@Valid UserDtoRegistration userDto, BindingResult bindingResult, Model model,
                             @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("user", user);
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "user/registration.html";
        }
        userService.addUser(userDto);
        return "user/registered.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user/login.html";
    }
}
