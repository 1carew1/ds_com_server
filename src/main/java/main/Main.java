package main;

import HelloApp.Hello;
import HelloApp.HelloHelper;
import HelloApp.HelloServant;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import rabbitmq.RabbitRPCServer;
import rmi.ComplextRMIObject;
import rmi.RMIImplementation;
import utils.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeoutException;

import static spark.Spark.*;

import org.omg.CORBA.ORB;

public class Main {


    public static void main(String[] args) {
        Gson gson = new Gson();


        //REST Start
        port(8091);
        get("/createComplexObj", (req, res) -> gson.toJson(Utils.createComplextObj()));
        System.out.println("Rest Running...");
        //Rest End


        //Corba Start
        Runnable corbaThread = new Runnable() {
            public void run() {
                try {
                    ORB orb = ORB.init(args, null);
                    POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
                    rootpoa.the_POAManager().activate();

                    HelloServant helloServant = new HelloServant();
                    helloServant.hellomessage(gson.toJson(Utils.createComplextObj()));
                    helloServant.setOrb(orb);

                    org.omg.CORBA.Object ref = rootpoa.servant_to_reference(helloServant);
                    Hello href = HelloHelper.narrow(ref);

                    org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                    NamingContextExt namingContextExt = NamingContextExtHelper.narrow(objRef);

                    NameComponent path[] = namingContextExt.to_name("ABC");
                    namingContextExt.rebind(path, href);

                    System.out.println("Corba....");

                    for (; ; ) {
                        orb.run();
                    }

                    //In a command window run : sudo tnameserv -ORBInitialPort 900

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(corbaThread).start();
        //Corba End


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
        Runnable rabbitThread = new Runnable() {
            public void run() {
                try {
                    RabbitRPCServer rabbitRPCServer = new RabbitRPCServer();
                    rabbitRPCServer.receiveAndRespond();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(rabbitThread).start();
        // Rabbit MQ End

        //Basic Socket Start
        Runnable socketThread = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(1025);
                    while (true) {
                        System.out.println("Socket Running...");
                        Socket socket = serverSocket.accept();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                                socket.getInputStream()));
                        PrintStream printStream = new PrintStream(socket.getOutputStream());
                        printStream.print(gson.toJson(Utils.createComplextObj()));
                        printStream.print('\n');
                        bufferedReader.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        socketThread.run();
        //Basic Socket End

    }
}

