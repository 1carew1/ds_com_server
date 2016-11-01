import com.rabbitmq.client.*;
import rabbitmq.RabbitRPCServer;
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
            RabbitRPCServer rabbitRPCServer = new RabbitRPCServer();
            rabbitRPCServer.receiveAndRespond();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
