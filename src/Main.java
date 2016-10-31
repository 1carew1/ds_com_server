import com.rabbitmq.client.*;
import rmi.RMIImplementation;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeoutException;

public class Main {


    public static void main(String[] args) {

        // RMI

        //Source : https://www.youtube.com/watch?v=GURClZeR96E
        // Remember if you are ever sending an object you need to implement serialisable
        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            RMIImplementation rmi = new RMIImplementation();
            reg.rebind("testRMI", rmi);
            System.out.println("RMI Running...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Rabbit MQ
        try {
            String queueName = "testSendQueue";
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPassword("admin");
            factory.setUsername("admin");
            factory.setVirtualHost("admin");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    try {
                        channel.close();
                        System.out.println("Channel Closed");
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            };
            channel.basicConsume(queueName, true, consumer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
