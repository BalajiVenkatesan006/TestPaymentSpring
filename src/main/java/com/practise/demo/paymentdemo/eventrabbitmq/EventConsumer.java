package com.practise.demo.paymentdemo.eventrabbitmq;


import com.google.gson.Gson;
import com.practise.demo.paymentdemo.cache.RedisRepositoryImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer{

    @Autowired
    RedisRepositoryImplementation redisRepositoryImplementation;
    //@RabbitListener(queues="${rabbitmq.queueName}")
    public void listen(byte[] message) {
        String msg = new String(message);
        Notification not = new Gson().fromJson(msg, Notification.class);
        if(not!=null)
            redisRepositoryImplementation.addAccounttocache(not.getAccountNumber(),not.getTotalAmount());
    }
}