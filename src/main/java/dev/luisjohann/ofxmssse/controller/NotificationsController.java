package dev.luisjohann.ofxmssse.controller;

import dev.luisjohann.ofxmssse.exceptions.UnauthorizedException;
import dev.luisjohann.ofxmssse.handler.EventHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@Slf4j
public class NotificationsController {

    private final EventHandlerAdapter eventHandlerAdapter;

    public NotificationsController(EventHandlerAdapter eventHandlerAdapter) {
        this.eventHandlerAdapter = eventHandlerAdapter;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<SseEmitter> subscribe(Authentication auth) {
        var userId = getUserId(auth);
        log.info("User subscribe sse {}", userId);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
//        sendSubscribedEvent(sseEmitter);
        eventHandlerAdapter.registerSseEmitter(userId, sseEmitter);
        sseEmitter.onCompletion(() -> eventHandlerAdapter.unregisterSseEmitter(userId));
        sseEmitter.onTimeout(() -> eventHandlerAdapter.unregisterSseEmitter(userId));
        sseEmitter.onError((e) -> onErrorEmitter(userId, e));

        return Mono.just(sseEmitter);
    }

    private void onErrorEmitter(String userId, Throwable e) {
        eventHandlerAdapter.unregisterSseEmitter(userId);
        log.error(e.getMessage(), e);
    }

    private String getUserId(Authentication authentication) {
        if (Objects.nonNull(authentication) && authentication.getPrincipal() instanceof Jwt) {
            return authentication.getName();
        }
        throw new UnauthorizedException("Sem autorização");
    }
}
