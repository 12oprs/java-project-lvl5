package hexlet.code.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public final class WelcomeController {

//    @GetMapping
//    public RedirectView root() {
//        return new RedirectView("/welcome");
//    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
