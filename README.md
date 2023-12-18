<br>SALOON</h1>
<h3>◦ Dive into the Wild West with Saloon!</h3>

<img src="https://img.shields.io/github/last-commit/CoJaques/Saloon?style=flat-square&color=5D6D7E" alt="git-last-commit" />
<img src="https://img.shields.io/github/commit-activity/m/CoJaques/Saloon?style=flat-square&color=5D6D7E" alt="GitHub commit activity" />
<img src="https://img.shields.io/github/languages/top/CoJaques/Saloon?style=flat-square&color=5D6D7E" alt="GitHub top language" />
</div>

---

## 📖 Table of Contents
- [📖 Table of Contents](#-table-of-contents)
- [� Saloon: Overview](#-saloon-overview)
  - [🌐 Server Component](#-server-component)
  - [💻 Client Component](#-client-component)
  - [🌟 Special Features](#-special-features)
  - [📚 Protocol Definition](#-protocol-definition)
- [🚀 Getting started](#-getting-started)
  - [🔧 Packaging](#-packaging)
  - [Publishing to GitHub](#publishing-to-github)
  - [📖 Run it with docker compose](#-run-it-with-docker-compose)
  - [Run it directly via Docker](#run-it-directly-via-docker)
    - [Client](#client)
      - [Running the Client Directly via Docker](#running-the-client-directly-via-docker)
      - [Command-Line Arguments for Client](#command-line-arguments-for-client)
    - [Server](#server)
      - [Running the Server Directly via Docker](#running-the-server-directly-via-docker)
      - [Command-Line Arguments for Server](#command-line-arguments-for-server)

---

## 🚀 Saloon: Overview

Saloon is a chat application that provides a platform for real-time communication. The project is divided into two main components: the server and the client. 

### 🌐 Server Component
The server acts as the central hub for message distribution. It is designed to handle client connections using UDP (User Datagram Protocol) for efficient, low-latency communication. Key features include:
- Listening for client connections and managing their session.
- Handling unicast messages from clients and broadcasting them to other connected clients using multicast.
- Supporting private messages between clients, routed through the server.
- Maintaining a list of all connected users for retrieval.

### 💻 Client Component
The client interface offers a user-friendly environment for interaction within the Saloon network. Features include:
- Connecting to the server with a unique username.
- Sending messages to the server in unicast, which are then broadcasted to other clients.
- Private messaging capabilities, allowing direct communication with selected users.
- Ability to request and view a list of all connected users.

### 🌟 Special Features
- **Docker Support**: Both the server and client components are Dockerized, enabling easy deployment and scalability. The application is tested to work flawlessly in Docker environments, with special consideration for network configurations and inter-container communication.

### 📚 Protocol Definition
For a detailed understanding of the communication protocol used in Saloon, please refer to the protocol definition available at: [Saloon Protocol Definition](https://github.com/CoJaques/Saloon/tree/main/ApplicationProtocolDiagram)

## 🚀 Getting started

### 🔧 Packaging

You can package the jar file directly by using the maven wrapper packaged into the project. If you use Intellij IDE, the packaging configuration is embedded into the project.

1. Clone the Pongly repository:
```sh
git clone https://github.com/CoJaques/Saloon
```

2. Change to the project directory:
```sh
cd Saloon
```

3. Build the client and the server:
```sh
# Download the dependencies
./mvnw dependency:resolve

# Package the application
./mvnw package
```

Once the Jar are generated, you can build the docker images.

1. **Server Image**:
   - Navigate to the server's directory.
   - Build the image using the Docker build command.
     ```bash
     docker build -t ghcr.io/cojaques/saloon_server:v1.0.0 .
     ```

2. **Client Image**:
   - Navigate to the client's directory.
   - Build the image similarly.
     ```bash
     docker build -t ghcr.io/cojaques/saloon_client:v1.0.0 .
     ```

be sure to adapt the version number as needed.

Once done, you can publish it to github by using :

```sh
docker push ghcr.io/cojaques/saloon_client:v1.0.0
docker push ghcr.io/cojaques/saloon_server:v1.0.0
```


### Publishing to GitHub

After building the images, you can publish them to github. Be sure to be logged and have right to publish image.


### 📖 Run it with docker compose

1. **Running with Docker Compose**:
   - In the directory containing your `docker-compose.yml`, start the services.
     ```bash
     docker-compose up
     ```
   - This will pull the images from github and start the server and clients.

2. **Interacting with the Application**:
   
   Once the containers are running, interact with the client as you normally would in a local setup. You can connect to the client/server terminal by using
     ```bash
     docker attach <docker ID>
     ```

    Once connected, you must connect yourself to the server by using
     ```bash
     /connect <your username>
     ```
     From here, you can chat with everybody by simply sending simple message.
     You can get a list of who's connected by sending
    ```bash
    /who
    ```
     You can send private message by using
    ```bash
    /mp <target username> your message
    ```

    Finally you can quit the application by simply sending

    ```bash
    /quit
    ```
     

4. **Shutting Down the Application**:
   - To stop the application, use:
     ```bash
     docker-compose down
     ```

### Run it directly via Docker 

To run the application without docker compose follow the guide :

First you must pull the image.

```sh
docker pull ghcr.io/cojaques/saloon_client:v1.0.0
docker pull ghcr.io/cojaques/saloon_server:v1.0.0
```

Based on the provided code for your Saloon application's command-line arguments, I'll complete the README sections for the client and server components.

---

#### Client

##### Running the Client Directly via Docker

To run the Saloon client using Docker, execute the following command with the desired options:

```sh
docker run ghcr.io/cojaques/saloon_client:v1.0.0 [options]
```

##### Command-Line Arguments for Client

- `-UP, --UniPort` : The unicast port of the server to connect to. Default is set as per `Utils.UNICAST_PORT`.
- `-MP, --MultiPort` : The multicast port of the server for group communications. Default is set as per `Utils.MULTICAST_PORT`.
- `-H, --DefaultHost` : The server host address to connect to. Default is `Utils.DEFAULT_HOST`.
- `-M, --MultiCastAddress` : The multicast address of the server. Default is `Utils.DEFAULT_MULTICAST`.
- `-A, --Adapter` : Define the network adapter to use for the multicast.

#### Server

##### Running the Server Directly via Docker

To run the Saloon server using Docker, execute the following command with the necessary options:

```sh
docker run ghcr.io/cojaques/saloon_server:v1.0.0 [options]
```

##### Command-Line Arguments for Server

- `-MP, --MultiPort` : The multicast port of the server for broadcasting messages. Default is set as per `Utils.MULTICAST_PORT`.
- `-UP, --UniPort` : The unicast port of the server for receiving client messages. Default is set as per `Utils.UNICAST_PORT`.
- `-M, --MultiCastAddress` : The multicast address used by the server. Default is `Utils.DEFAULT_MULTICAST`.
- `-A, --Adapter` : Specify the network adapter to use for handling multicast traffic.

---