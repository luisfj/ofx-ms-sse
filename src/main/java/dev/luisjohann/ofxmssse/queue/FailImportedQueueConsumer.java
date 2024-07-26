package dev.luisjohann.ofxmssse.queue;

import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import dev.luisjohann.ofxmssse.dto.ResponseMessageDTO;
import dev.luisjohann.ofxmssse.dto.SseMessageDTO;
import dev.luisjohann.ofxmssse.enums.EnumEvent;
import dev.luisjohann.ofxmssse.enums.EnumStatus;
import dev.luisjohann.ofxmssse.handler.EventHandlerAdapter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FailImportedQueueConsumer {

   final EventHandlerAdapter eventHandlerAdapter;

   @RabbitListener(queues = { "${queue.name.imported-fail}" })
   public void receive(SseMessageDTO message) {
      var messageDto = new ResponseMessageDTO(EnumStatus.FAIL, EnumEvent.IMPORT_OFX, message.title(),
            message.message(), message.dateTime(),
            message.dateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            null);

      eventHandlerAdapter.emitEvent(message.userId(), messageDto);
   }
}
