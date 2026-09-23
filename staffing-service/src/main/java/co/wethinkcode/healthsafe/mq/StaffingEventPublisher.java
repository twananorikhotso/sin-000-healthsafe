package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class StaffingEventPublisher {

    public void publish(String event) throws JMSException {
        ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();

            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
            );

            Topic topic = session.createTopic(MqConfig.TOPIC);

            MessageProducer producer = session.createProducer(topic);

            TextMessage message = session.createTextMessage(event);

            producer.send(message);

            producer.close();
            session.close();
        }
    }
}