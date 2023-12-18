# Saloon Protocol

## Section 1 - Overview

Saloon is a chat application that facilitates real-time communication between clients and a server. The project consists of two main components: the server and the client. The communication protocol is designed for efficient, low-latency communication using UDP (User Datagram Protocol) for client-server interactions.

## Section 2 - Transport Protocol

The Saloon protocol utilizes UDP for communication. The default port for Saloon is 1312.

The initial connection is initiated by the client. The server supports multiple client connections.

During communication, clients can send unicast messages to the server, which are then multicasteed to other connected clients. Private messages between clients are also supported, routed through the server.

## Section 3 - Messages

Messages exchanged between clients and the server are text-based. Each message is terminated by a '\n' character.
The format of message is : Command;<username>;<usernamedest>;<message>\n

### Client Side

#### Connecting to the Server

Clients connect to the server by specifying a unique username.

##### Request

```
CONNECT;<username>;<target_saloon>;<username>\n
```

###### How to use in terminal
```
/connect SlWa99
```

#### Sending a Message

Clients can send unicast messages to the server for broadcasting.

##### Request

```
MSG;<username>;<target_saloon>;<message>\n
```
###### How to use in terminal (write your message !)
```
Hello, my name is SlWa99 !
```

#### Private Messaging

Clients can send private messages to another user.

##### Request

```
PM;<username>;<target_username>;<message>\n
```
###### How to use in terminal
```
/pm CoJaques Hi CoJaques, how are you today ?
```

#### Requesting Connected Users

Clients can request a list of all connected users from the server.

##### Request

```
WHO;<username>;<target_saloon>\n
```
###### How to use in terminal
```
/who
```

#### Disconnecting

Clients can disconnect from the server.

##### Request

```
QUIT;<username>;<target_saloon>\n
```

### Server Side

#### Broadcasting Messages

The server multicats messages received from a client to all connected clients.

##### Request

```
MSG;<sender_username>;<message>\n
```

#### Sending Private Messages

The server routes private messages between clients by mean of unicast packet.

##### Request

```
PM;<sender_username>;<target_username>;<message>\n
```

#### Sending Connected Users

The server responds to a client's request with a list of all connected users.

##### Response

```
WHO;<user1>;<user2>;...;<userN>\n
```

#### Disconnect Notification

When a client disconnects, the server notifies other connected clients.

##### Request

```
QUIT;<username>
```

## Section 4 - Examples

### Functional Communication Between Clients and Server

![Saloon Communication](https://github.com/CoJaques/Saloon/assets/94690298/2dfadb4c-75df-4b33-a138-882756486e4e)


### Non-functional communication Between Clients and Server
![Saloon Communication with error](https://github.com/CoJaques/Saloon/assets/94690298/ca3cf262-c420-4b22-9c79-23a9ee763dfe)
