package dev.luisjohann.ofxmssse.queue;

import java.time.format.DateTimeFormatter;

import org.springframework.amqp.core.Message;
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
public class SseQueueConsumer {

   final EventHandlerAdapter eventHandlerAdapter;

   @RabbitListener(queues = { "${queue.name.sse}" })
   public void receive(SseMessageDTO messageDTO, Message message) {
      var fileBody2 = String.valueOf(message.getBody());
      var headerUltima = message.getMessageProperties().getHeaders().get("ultima");
      System.out.println(String.format("--------------------- ULTIMA=%s", headerUltima));
      System.out.println("ENTROU MS SSE: " + messageDTO);
      System.out.println("-------------------------: " + fileBody2);

      var messageDto = new ResponseMessageDTO(EnumStatus.SUCCESS, EnumEvent.SSE_MESSAGE, messageDTO.title(),
            messageDTO.message(), messageDTO.dateTime(),
            messageDTO.dateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            null);

      eventHandlerAdapter.emitEvent(messageDTO.userId(), messageDto);
   }
}
