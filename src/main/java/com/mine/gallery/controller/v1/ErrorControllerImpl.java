package com.mine.gallery.controller.v1;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Any Errors caused by exceptions first pass through this controller
 * and are re-directed off to the exception handler by rethrowing them
 * from within a @Controller context, whereas any other errors
 * (not caused directly by an exception) pass through the ErrorController without modification.
 *
 * @author TrusTio
 */
@Hidden
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ErrorControllerImpl implements ErrorController {

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        if (request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) != null) {
            throw (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        }
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
