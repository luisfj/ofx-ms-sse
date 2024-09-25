package dev.luisjohann.ofxmssse.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record SseMessageDTO(String userId, String title, String message, LocalDateTime dateTime,
      List<Operation> operations) implements Serializable {

}
