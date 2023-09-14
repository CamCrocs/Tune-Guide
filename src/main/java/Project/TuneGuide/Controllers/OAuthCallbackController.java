package Project.TuneGuide.Controllers;

import Project.TuneGuide.Components.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class OAuthCallbackController {

    private final Authorization authorization;

    @Autowired
    public OAuthCallbackController(Authorization authorization) {
        this.authorization = authorization;
    }

    @GetMapping("/oauth-callback")
    public ModelAndView handleCallback(@RequestParam("code") String authorizationCode) {
        try {
            authorization.setAuthorizationCode(authorizationCode);
            authorization.exchangeAuthorizationCodeForToken();
            return new ModelAndView("redirect:/callback-success");
        } catch (IOException e) {
            return new ModelAndView("redirect:/callback-error");
        }
    }

    @GetMapping("/callback-success")
    public ModelAndView handleCallbackSuccess() {
        ModelAndView modelAndView = new ModelAndView("oauth-callback");
        modelAndView.addObject("success", true);
        return modelAndView;
    }

    @GetMapping("/callback-error")
    public ModelAndView handleCallbackError() {
        ModelAndView modelAndView = new ModelAndView("oauth-callback");
        modelAndView.addObject("success", false);
        return modelAndView;
    }


}
