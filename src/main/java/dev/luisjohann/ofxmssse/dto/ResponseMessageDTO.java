package dev.luisjohann.ofxmssse.dto;

import java.time.LocalDateTime;
import java.util.List;

import dev.luisjohann.ofxmssse.enums.EnumEvent;
import dev.luisjohann.ofxmssse.enums.EnumStatus;

public record ResponseMessageDTO(EnumStatus status, EnumEvent event, String title, String message,
            LocalDateTime eventDateTime, String formatedEventDateTime, List<Operation> data) {

}
