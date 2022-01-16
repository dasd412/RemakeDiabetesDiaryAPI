package com.dasd412.remake.api.controller.security.domain_view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarFormController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/calendar")
    public String calendarViewResolve() {
        logger.info("calendar view resolve");
        return "calendar/calendar";
    }
}

