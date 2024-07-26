package dev.luisjohann.ofxmssse.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import dev.luisjohann.ofxmssse.handler.EventHandlerAdapter;
import reactor.core.publisher.Mono;

@RestController
public class NotificationsController {

   private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

   private final EventHandlerAdapter eventHandlerAdapter;

   public NotificationsController(EventHandlerAdapter eventHandlerAdapter) {
      this.eventHandlerAdapter = eventHandlerAdapter;
   }

   @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   public Mono<SseEmitter> subscribe(@PathVariable final Long userId) {
      SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
      sendSubscribedEvent(sseEmitter);
      eventHandlerAdapter.registerSseEmitter(userId, sseEmitter);
      sseEmitter.onCompletion(() -> eventHandlerAdapter.unregisterSseEmitter(userId));
      sseEmitter.onTimeout(() -> eventHandlerAdapter.unregisterSseEmitter(userId));
      sseEmitter.onError((e) -> onErrorEmitter(userId, e));

      return Mono.just(sseEmitter);
   }

   private void sendSubscribedEvent(final SseEmitter sseEmitter) {
      try {
         sseEmitter.send(SseEmitter.event().name("Subscribed successfully!"));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void onErrorEmitter(Long userId, Throwable e) {
      eventHandlerAdapter.unregisterSseEmitter(userId);
      logger.error(e.getMessage(), e);
   }
}
