package by.belstu.it.lyskov.dbrestaurant.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class PageErrorController implements ErrorController {

    @GetMapping("/error")
    public ModelAndView showErrorPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String viewName = "error";
        if (statusCode != null) {
            if (Integer.parseInt(statusCode.toString()) == HttpStatus.UNAUTHORIZED.value()
                    || Integer.parseInt(statusCode.toString()) == HttpStatus.FORBIDDEN.value()
                    || Integer.parseInt(statusCode.toString()) == HttpStatus.NOT_FOUND.value())
                viewName = statusCode.toString();
        }
        modelAndView.setViewName("error/" + viewName);
        modelAndView.addObject("timestamp",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss")));
        return modelAndView;
    }

    @GetMapping("/error/{error}")
    public ModelAndView showErrorPage(@PathVariable Integer error) {
        ModelAndView modelAndView = new ModelAndView();
        String viewName = switch (error) {
            case 401 -> "401";
            case 403 -> "403";
            default -> "404";
        };
        modelAndView.setViewName("error/" + viewName);
        modelAndView.addObject("timestamp",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss")));
        return modelAndView;
    }
}

