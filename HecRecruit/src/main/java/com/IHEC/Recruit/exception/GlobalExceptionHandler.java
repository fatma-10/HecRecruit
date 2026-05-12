package com.IHEC.Recruit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erreur metier : on redirige vers la page precedente avec un flash message.
     */
    @ExceptionHandler(BusinessException.class)
    public RedirectView handleBusiness(BusinessException ex,
                                       HttpServletRequest request,
                                       RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", ex.getMessage());
        String referer = request.getHeader("Referer");
        return new RedirectView(referer != null ? referer : "/");
    }

    @ExceptionHandler(ForbiddenException.class)
    public String handleForbidden(ForbiddenException ex, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAny(Exception ex, Model model) {
        ex.printStackTrace();
        model.addAttribute("status", 500);
        model.addAttribute("message", "Erreur interne : " + ex.getMessage());
        return "error/error";
    }
}
