package main;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import rabbitmq.RabbitRPCServer;
import rmi.ComplextRMIObject;
import rmi.RMIImplementation;
import utils.Utils;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeoutException;

import static spark.Spark.*;

public class Main {


    public static void main(String[] args) {
        Gson gson = new Gson();

        //REST Start
        get("/createComplexObj", (req, res) -> gson.toJson(Utils.createComplextObj()));
        System.out.println("Rest Running...");
        //Rest End

        // RMI Start

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
        // RMI End

        // Rabbit MQ Start
        try {
            RabbitRPCServer rabbitRPCServer = new RabbitRPCServer();
            rabbitRPCServer.receiveAndRespond();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Rabbit MQ End


    }
}
