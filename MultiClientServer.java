import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClientServer {
    private static List<ClientHandler> clients = new ArrayList<>(); // store all client
    private static final String EXIT_KEYWORD = "bye";
    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        // server socket "listens" to connection request, message are exchanged using client's socket after connection established
        System.out.println("Server started on port " + PORT);

        while (true){
            //the main thread will keep executing the while loop,
            // and when a client connects,
            // it open up a new thread and keep running the while loop on the current thread
            Socket clientSocket = serverSocket.accept(); // wait til a client connects, creates a socket used to comm with the client
            System.out.println("New client connected");

            //add client to the list
            ClientHandler clientHandler = new ClientHandler(clientSocket, clients.size());
            // client.size assign current size (+1) of clients array to new client, +1 because the clientHandler is only added after this line
            clients.add(clientHandler);
            System.out.println(clients);
            Thread clientHandlerThread = new Thread(clientHandler);
            clientHandlerThread.start();
            // create new thread and socket for every new client, this thread will continue the while loop
            /*
            1. The Thread constructor is called with a Runnable object, which is an instance of ClientHandler that takes a Socket object.
            2. The Thread constructor creates a new Thread object and sets its internal target field to the Runnable object passed in as a parameter.
            3. When the Thread object's start() method is called, it calls the run() method of its internal target object, which is the ClientHandler instance in this case.
            4. The run() method of ClientHandler is executed in a new thread.
             */
        }

    }

    static class ClientHandler implements Runnable{
        // static class, can be instantiated without an instance of outer class ex: new OuterClass.StaticClass()
        // non-static (inner) class needs to be created  with instance of outer class ex:
        //      mainClass outerObj = new MainClass();
        //      NonStaticClass nonStaticObj = outerObj.new NonStaticClass()
        private final Socket clientSocket;
        private final BufferedReader input;
        private final PrintWriter output;
        private String username;
        private int clientId;

        public ClientHandler(Socket socket, int clientId) throws IOException {
            clientSocket = socket; // set socket
            this.clientId = clientId; // set client id
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // responsible for receiving message from clients
            output = new PrintWriter(clientSocket.getOutputStream(), true); // responsible for printing to clients
            // printWriter is used to print output to a stream
            // 'true' forces a flush of any buffered data to be written out to the underlying stream after writing a line
            output.println("Enter your username: ");
            this.username = input.readLine(); // change username for this instance
            while (this.username == null || username.trim().isEmpty()){ // check if username is null or only contain white space
                // ** isEmpty() check if the string is empty (0 characters), it is not the same as ==null.
                // Null character does not have mem address, empty string does
                output.println("Invalid username, try again: ");
                this.username = input.readLine();
            }
            this.username = this.username.trim();
            output.println("Welcome to the chat, " + this.username + "\nYou can start chatting");
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = input.readLine()) != null){
                    System.out.println(this.username + ": " + line);
                    if (line.equalsIgnoreCase(EXIT_KEYWORD)){
                        break;
                    }
                    for (ClientHandler client : clients){ // loop through client handlers and print message to all client
                        client.output.println(this.username + ": " + line); // clientId refers to current instance (the one who send the msg)
                        client.output.flush(); // not strictly necessary as autoflush is enabled in PrintWriter, but its a good practice
                    }

                }
            } catch (IOException e){
                System.out.println("Error handling client: " + e.getMessage());
                // get message is a method in throwable class that returns a string that describe the error message
            } finally {
                try{
                    clientSocket.close(); // close socket after terminate connection
                    clients.remove(this); // remove this client from the list
                    System.out.println("Client disconnected: " + clientSocket);
                } catch (IOException e){
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        }
    }
}
