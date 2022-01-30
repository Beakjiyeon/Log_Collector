package com.visualizer.log.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class VisualizeLogController {



    @GetMapping(value = {"index"})
    public String index2() {
        log.debug("## home page call.");
        return "index";
    }

    @GetMapping(value = {"history"})
    public String history() {
        log.debug("## home page call.");
        return "history";
    }

    @GetMapping(value = {"history/tag-id/{tagId}/searchDate/{searchDate}"})
    public String historyService(@PathVariable("tagId") String tagId, @PathVariable("searchDate") String searchDate) {
        log.debug("## home page call.");
        return "history";
    }

    @GetMapping(value = {"mail"})
    public String mail() {
        log.debug("## home page call.");
        return "mail";
    }
}
