package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class StaffingEventSubscriber {

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public void start() {
        try {
            ActiveMQConnectionFactory connectionFactory =
                    new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
            );

            Topic topic = session.createTopic(MqConfig.TOPIC);

            consumer = session.createConsumer(topic);

            consumer.setMessageListener(this::handleMessage);

            System.out.println(
                    "Ward Service subscribed to ActiveMQ topic: "
                            + MqConfig.TOPIC
            );

        } catch (Exception e) {
            System.err.println(
                    "Unable to start staffing event subscriber: "
                            + e.getMessage()
            );
        }
    }

    private void handleMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                System.out.println("Received staffing event:");
                System.out.println(textMessage.getText());
            } else {
                System.out.println(
                        "Received unsupported ActiveMQ message type: "
                                + message.getClass().getSimpleName()
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "Unable to process staffing event: "
                            + e.getMessage()
            );
        }
    }
}