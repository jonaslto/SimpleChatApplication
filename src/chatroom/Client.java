package chatroom;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chat Room");
	private JLabel label;
	private JButton logout;
    JTextField textField;
    public JTextArea messageArea;

    public Client() {

        // Add layout for GUI
    	// Add textfield for writing messages
    	JPanel northPanel = new JPanel(new GridLayout(3,1));
        label = new JLabel("Write a message:", SwingConstants.CENTER);
		northPanel.add(label);
		textField = new JTextField();
		textField.setBackground(Color.WHITE);
		northPanel.add(textField);
        textField.setEditable(false);
        add(northPanel, BorderLayout.NORTH);
        
        // Add chat room area
		messageArea = new JTextArea("Simple chat application:\n", 8, 40);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(messageArea));
        messageArea.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// Add logout button
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		
		JPanel southPanel = new JPanel();
		southPanel.add(logout);
		add(southPanel, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 400);
		setVisible(true);
		textField.requestFocus();
		
        // Add ActionListener to allow sending text from textfield
		// to server via Enter Key
        textField.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    // PopUp prompt that returns the desired nickname.
    @Override
	public String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a chat nickname:",
            "Select a chat nickname",
            JOptionPane.PLAIN_MESSAGE);
    }

     //Connects to the server then enters the processing loop.
    private void run() throws IOException {

        // Make connection and start streams
        String serverAddress = "localhost";
        int port = 9001;

        Socket socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("NICKSUBMIT")) { //send desired nickname to server
                out.println(getName());
            } else if (line.startsWith("ACCEPTED")) { //nickname has been accepted by server
                textField.setEditable(true);
                logout.setEnabled(true);
            } else if (line.startsWith("USERS")) {
                messageArea.append(line.substring(6) + "\n"); //print list of users to message area
            } else if (line.startsWith("MESSAGE")) { //recognize normal message from server
                messageArea.append(line.substring(8) + "\n");
            } else if (line.startsWith("@")) { //notify server of private message
            	out.println(textField.getText());
            } else if(line.startsWith("PRIVATEMESSAGE")) { //recognize private message from server
                messageArea.append(line.substring(15) + "\n");
            }
        }
    }
    
    // Runs the client GUI
    public static void main(String[] args) throws Exception {
        Client chatClient = new Client();
        chatClient.run();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		// logout button button
		if(source == logout) {
			System.exit(0);
		}
	}
}