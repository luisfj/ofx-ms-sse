package dev.luisjohann.ofxmssse.configuration;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;

@EnableRabbit
@Configuration
@AllArgsConstructor
public class RabbitConfig {

   final ConnectionFactory connectionFactory;

   // @Bean
   // public MessageConverter jsonToMapMessageConverter() {
   // DefaultClassMapper defaultClassMapper = new DefaultClassMapper();
   // defaultClassMapper.setTrustedPackages("java.time.*"); // trusted packages
   // Jackson2JsonMessageConverter jackson2JsonMessageConverter = new
   // Jackson2JsonMessageConverter();
   // jackson2JsonMessageConverter.setClassMapper(defaultClassMapper);
   // return jackson2JsonMessageConverter;
   // }

   @Bean
   public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
      final var rabbitTemplate = new RabbitTemplate(connectionFactory);
      rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
      return rabbitTemplate;
   }

   @Bean
   public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
      return new Jackson2JsonMessageConverter();
   }

}
