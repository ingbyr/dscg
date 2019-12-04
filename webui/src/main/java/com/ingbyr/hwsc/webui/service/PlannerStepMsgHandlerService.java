package com.ingbyr.hwsc.webui.service;

import com.ingbyr.hwsc.planner.StepMsgHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlannerStepMsgHandlerService implements StepMsgHandler {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public PlannerStepMsgHandlerService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void handle(String msg) {
        simpMessagingTemplate.convertAndSend("/topic/stepResult", msg);
    }
}
