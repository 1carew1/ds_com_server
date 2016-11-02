package main;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import rabbitmq.RabbitRPCServer;
import rmi.ComplextRMIObject;
import rmi.RMIImplementation;
import utils.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeoutException;

import static spark.Spark.*;

public class Main {


    public static void main(String[] args) {
        Gson gson = new Gson();

        //REST Start
        port(8091);
        get("/createComplexObj", (req, res) -> gson.toJson(Utils.createComplextObj()));
        System.out.println("Rest Running...");
        //Rest End


        // File Store Start
        String file = System.getProperty("user.home") + "/Desktop/obj.json";
        System.out.println("Writing to file: " + file);
        // Files.newBufferedWriter() uses UTF-8 encoding by default
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file))) {
            writer.write(gson.toJson(Utils.createComplextObj()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // File Store End

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
