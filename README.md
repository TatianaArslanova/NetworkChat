# NetworkChat
This project is the client-server chat for exchange of text messages.
### Server
Server has a plain graphical user interface. During operation, displays information about connecting / disconnecting clients and their authorization.

For using this chat two authorization ways are avalible: 
 * by login/pass 
 * by guest authorization.
 
Authorization service by login/pass works with the SQL database and allows users to send private messages and change their nicks.
When using guest authorization, a temporary account will be created. It is not using the database and allows user to receive private messages.
### Client
User may quickly put a nick into the message field by double clicking on it in the client list.
Some text commands are implemented:
 * /end - sign off account,
 * /wisp UserNick YourMessage - send a private message, where UserNick is the destination and YourMessage is the message for sending.
 * /change NewNick - change your nick, where NewNick is your new nick.
 
The appearance of client interface depends on authorization type: buttons for fast private messages on the client list are available with authorization by login/pass only.

Connection settings allow specifying the server address for the connection.
