package dev.luisjohann.ofxmssse.handler;

import dev.luisjohann.ofxmssse.dto.ResponseMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EventHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(EventHandlerAdapter.class);

    private Map<String, SseEmitter> sseEmitters = new HashMap<>();

    public void processEvent(String userId, ResponseMessageDTO event) {

        logger.info("Processing Event of type: {}", "ENVIO DE MSG");

        createNotifications(event, Set.of(userId));

        logger.info("Finished processing Event of type: {}", "ENVIO DE MSG");
    }

    public void emitEvent(String userId, final ResponseMessageDTO messageEvent) {
        final SseEmitter sseEmitter = sseEmitters.get(userId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(messageEvent);
            } catch (IOException e) {
                sseEmitter.complete();
                unregisterSseEmitter(userId);
            }
        }
    }

    public void registerSseEmitter(String userId, SseEmitter sseEmitter) {
        var optEmitter = Optional.ofNullable(this.sseEmitters.get(userId));
        optEmitter.ifPresent(emitter -> {
            emitter.complete();
            unregisterSseEmitter(userId);
        });
        this.sseEmitters.put(userId, sseEmitter);
    }

    public void unregisterSseEmitter(String userId) {
        this.sseEmitters.remove(userId);
    }

    private void createNotifications(ResponseMessageDTO event, Set<String> recipients) {
        recipients.forEach(recipient -> {
            emitEvent(recipient, event);
            logger.info("Notification created successfully for recipient: {}",
                    recipient);
        });
    }
}