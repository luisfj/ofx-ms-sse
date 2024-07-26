package dev.luisjohann.ofxmssse.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import dev.luisjohann.ofxmssse.dto.ResponseMessageDTO;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventHandlerAdapter {
   private static final Logger logger = LoggerFactory.getLogger(EventHandlerAdapter.class);

   private Map<Long, SseEmitter> sseEmitters = new HashMap<>();

   public void processEvent(Long userId, ResponseMessageDTO event) {

      logger.info("Processing Event of type: {}", "ENVIO DE MSG");

      createNotifications(event, Set.of(userId));

      logger.info("Finished processing Event of type: {}", "ENVIO DE MSG");
   }

   public void emitEvent(Long userId, final ResponseMessageDTO messageEvent) {
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

   public void registerSseEmitter(Long userId, SseEmitter sseEmitter) {
      this.sseEmitters.put(userId, sseEmitter);
   }

   public void unregisterSseEmitter(Long userId) {
      this.sseEmitters.remove(userId);
   }

   private void createNotifications(ResponseMessageDTO event, Set<Long> recipients) {
      recipients.forEach(recipient -> {
         emitEvent(recipient, event);
         logger.info("Notification created successfully for recipient: {}",
               recipient);
      });
   }
}