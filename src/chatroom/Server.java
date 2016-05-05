package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class Server {

	// Add the server port
    private static final int PORT = 9001;

    // HashSet includes nickname of logged in users, and doesn't allow duplicates
    public static HashSet<String> names = new HashSet<String>();


    // HashMap includes a PrintWriter and nickname of all users, and is used to easily broadcast messages
    public static HashMap<String, PrintWriter> writers = new HashMap<String, PrintWriter>();

    // Listen to server port and start ClientHandler threads for each Client
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new ClientHandler(listener.accept()).start();
            }
        } finally {
        	listener.close();
        }
    }

    // Class for handling clients. A single ClientHandler deals with the messages of a single Client
    public static class ClientHandler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        // The run method does most of the work. 
        @Override
		public void run() {
            try {
                // Create streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                // A while loop runs until a name has been submitted from the client.
                // If the name is not already in use, it is added to the HashSet of names.
                while (true) {
                    out.println("NICKSUBMIT");
                    name = in.readLine();
                    if (name == null) {
                    	return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                // The new client has a unique nickname. Now the socket's PrintWriter
                // and the client's nickname is added to the HasMap of writers. The Client can now
                // send messages.
                out.println("ACCEPTED");
                writers.put(name, out);
                // The set of online users broadcasted to all online users, when a new Client is added
                for (PrintWriter writer : writers.values()){
                	writer.println("USERS " + "These guys are online: " + names);	
                }

                // A while loop listens for messages from a client
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    // An '@' at the beginning of a client's message indicates that the clients
                    // wishes to send a private message
                    if (input.startsWith("@")) {
                    	// Find the receiver of the private message
                    	String[] receiver = input.split(" ");
                    	PrintWriter PMwriter = writers.get(receiver[0].replace("@", ""));
                    	String PMinput = input.substring(1 + name.length());
                    	// String manipulation makes sure only useful information is sent to the receiver
                    	PMwriter.println("PRIVATEMESSAGE Private message from " + name + ": " + PMinput);
                     
                    } else {
                    	// Public broadcasts are sent to all Clients
                    	for (PrintWriter writer : writers.values()) {
                            writer.println("MESSAGE " + name + ": " + input);
                        }
                    }
                }
                
            } catch (IOException e) {
                System.out.println(e);
            } finally {
            	// If a Client is finished chatting, the nickname and PrintWriter is removed,
            	// and the socket is closed
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                    // The set of online users broadcasted to all online users, when a new Client is removed
                    for (PrintWriter writer : writers.values()){
                    	writer.println("USERS " + "These guys are online: " + names);	
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
