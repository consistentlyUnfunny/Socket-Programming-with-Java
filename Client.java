import java.io.*; // to read/write files or streams
import java.net.*; // to work with networking protocols

/*
streams = sequence of data that can be read from or written to. It allows program to read/write variety of sources/destination
in a uniform way, regardless of specific details of source or destination
 */
public class Client {

    private Socket socket; // socket is the endpoint for sending and receiving data over a network
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    // constructor to establish connection based on port number and ip address
    public Client(String address, int port){
        //establish a connection
        try {
            socket = new Socket(address, port);
            // return a socket when serverSocket.accept() is executed
            // when server send message to client, it writes the message to the output stream of client socket
            System.out.println("Connected");

            inputStream = new DataInputStream(socket.getInputStream()); // store location/channel for input stream
            outputStream = new DataOutputStream(socket.getOutputStream()); // store location/channel for output stream

            // create a new thread that receive message continuously from server
            Thread serverListener  = new Thread(() ->{
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(inputStream)); // read from input stream
                while (true){
                    try{
                        System.out.println(serverReader.readLine());
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            });
            serverListener.start();

        } catch (UnknownHostException unknownHostException){ // invalid host name/ address
            System.err.println(unknownHostException); // print error msg using .err, basically the same thing, just a convention
        } catch (IOException ioException) { // connection unexpectedly terminated, connection issues
            throw new RuntimeException(ioException);
        }

        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (socket.isConnected() && !line.equalsIgnoreCase("bye")){
            // message "over" can still be received by the server because over is sent before the condition is checked again for another loop
            try{
                //readUTF reads sequence of bytes from input stream (send by server side socket) and decodes it into UTF-8 (character encoding) format
                line = reader.readLine(); // read from input stream (user input)
                outputStream.write((line + "\n").getBytes());
                outputStream.flush(); // ensure the message in sent immediately without waiting for buffer to fill up
            } catch (IOException ioException){
                System.err.println(ioException);
            }
        }

        // close the connection
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException ioException){
            System.err.println(ioException);
        }
    }

    public static void main (String[] args){
        new Client("127.0.0.1", 5000); // 127.0.0.1 refers to local loopback address (this computer)
    }

}
