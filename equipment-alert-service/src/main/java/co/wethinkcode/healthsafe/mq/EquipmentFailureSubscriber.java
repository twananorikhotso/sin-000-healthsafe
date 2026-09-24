package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class EquipmentFailureSubscriber {

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
                    Session.CLIENT_ACKNOWLEDGE
            );

            Queue queue = session.createQueue(MqConfig.QUEUE);

            consumer = session.createConsumer(queue);

            consumer.setMessageListener(this::handleMessage);

            System.out.println(
                    "Equipment Alert Service subscribed to ActiveMQ queue: "
                            + MqConfig.QUEUE
            );

        } catch (Exception e) {
            System.err.println(
                    "Unable to start equipment failure subscriber: "
                            + e.getMessage()
            );
        }
    }

    private void handleMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String event = textMessage.getText();

                System.out.println("Received equipment failure:");
                System.out.println(event);

                message.acknowledge();
            } else {
                System.err.println(
                        "Received unsupported ActiveMQ message type: "
                                + message.getClass().getSimpleName()
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "Unable to process equipment failure: "
                            + e.getMessage()
            );
        }
    }
}