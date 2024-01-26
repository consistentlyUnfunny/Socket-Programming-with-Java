import java.io.*;
import java.net.*;
import java.sql.Array;
import java.util.*;

public class Server {

    private Socket socket;
    private ServerSocket serverSocket;
    // server socket is responsible for creating a socket for the server to listen for incoming connections
    private DataInputStream inputStream;

    public Server(int port){
        // starts server and wait for connection
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for client");

            socket = serverSocket.accept();
            // wait until a client connects to the server then returns a new "Socket"
            // object representing the connection to client
            // new socket is instantiate when new client connected
            System.out.println("Client Connected");

            // takes input from client socket
            inputStream = new DataInputStream(socket.getInputStream());

            String line = "";
            while (!line.equalsIgnoreCase("over")) {
                try {
                    line = inputStream.readUTF();
                    System.out.println(line);
                } catch (EOFException eofException) { // a type of io exception that is thrown when end of a stream is reached
                    System.out.println("Client disconnected");
                } catch (IOException ioException) {
                    System.err.println(ioException);
                }
            }
            System.out.println("Closing Connection");

            socket.close();
            inputStream.close();
        } catch (IOException ioException){
            System.err.println(ioException);
        }
    }
    public static void main (String[] args){
        new Server(5000);
    }
}
