package com.tcz.listen.controllers;

import com.tcz.listen.response.LobbyResponse;
import com.tcz.listen.response.NotificationResponse;
import com.tcz.listen.response.Response;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<Response> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            String message = "Error.";

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                message = "Not found.";
            }

            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                message = "Error on server side.";
            }

            return new ResponseEntity<>(new NotificationResponse(message), HttpStatus.valueOf(statusCode));
        }

        return new ResponseEntity<>(new NotificationResponse("Error."), HttpStatus.BAD_REQUEST);
    }
}
