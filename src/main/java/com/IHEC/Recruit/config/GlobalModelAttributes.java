package com.IHEC.Recruit.config;

import com.IHEC.Recruit.security.CurrentUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final CurrentUser currentUser;

    public GlobalModelAttributes(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @ModelAttribute("currentUser")
    public CurrentUser currentUser() {
        return currentUser;
    }
}
