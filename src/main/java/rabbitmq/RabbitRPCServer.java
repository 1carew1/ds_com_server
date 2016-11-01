package rabbitmq;
/**
 * Copyright (c) 2016 Errigal Inc.
 * <p>
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 * <p>
 * **************************************************************
 * Created by Colm Carew on 01/11/2016.
 * <p>
 * Created by Colm Carew on 01/11/2016.
 * <p>
 * Created by Colm Carew on 01/11/2016.
 * <p>
 * Created by Colm Carew on 01/11/2016.
 */

/**
 * Created by Colm Carew on 01/11/2016.
 */

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import rmi.ComplextRMIObject;
import sun.tools.jconsole.MaximizableInternalFrame;
import utils.Utils;

public class RabbitRPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    public ConnectionFactory factory;
    public Connection connection;
    public Channel channel;

    public RabbitRPCServer() throws Exception {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("admin");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel = connection.createChannel();
        channel.basicQos(1);
    }

    public void receiveAndRespond() throws Exception {
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        try {
            System.out.println("Rabbit RPC Running...");
            while (true) {
                String response = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();
                BasicProperties replyProps = new BasicProperties
                        .Builder()
                        .correlationId(props.getCorrelationId())
                        .build();

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
//                    System.out.println(message);
                    Gson gson = new Gson();
                    ComplextRMIObject complextRMIObject = Utils.createComplextObj();
                    response = gson.toJson(complextRMIObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    response = null;
                } finally {
                    channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                    System.out.println("Message sent back");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}