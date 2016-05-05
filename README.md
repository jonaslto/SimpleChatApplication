The chatroom works as follows: 
When Client.java is launched, the server requests a nickname from the client by sending 
“NICKSUBMIT”, and the clients is prompted to select a unique nickname for the chatroom. 
If the nickname provided by the client is unique, the server sends an “ACCEPTED” message. 
This message means that the client is ready to broadcast messages to everyone else in the 
chatroom by simply typing desired message in the textfield. A prefix (“MESSAGE”) is added 
to all messages unless the client starts it’s message with “@”. This indicates that the 
client wants to send a private message. A list of online users is printed in the chatroom 
every time a new user logs on or if a user logs off. A private message is sent to one of 
these other online users by typing “@username ‘this is a private message’ ”. The server 
recognizes the “@”, and makes sure only the correct user gets this message.

