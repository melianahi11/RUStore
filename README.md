# RUStore

RUStore is a Java-based object store server and client implementation. This project provides a server that stores arbitrary data objects associated with unique keys and a client that can connect to this server to perform various operations such as put, get, remove, and list.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Building the Project](#building-the-project)
  - [Running the Server](#running-the-server)
  - [Running the Client](#running-the-client)
- [Usage](#usage)
  - [Client Methods](#client-methods)

## Overview

RUStore consists of two main components:
- **RUStoreServer**: Handles client requests and manages the data store.
- **RUStoreClient**: Connects to the server to interact with the data store.

## Features

- **Put**: Store a new object associated with a unique key.
- **Get**: Retrieve an object by its key.
- **Remove**: Delete an object by its key.
- **List**: Get a list of all stored keys.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- A Java IDE or text editor

### Building the Project

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/RUStore.git
   cd RUStore
Compile the Java files:
sh
Copy code
javac -d bin src/com/RUStore/*.java
Running the Server
To start the RUStore server, use the following command:

sh
Copy code
java -cp bin com.RUStore.RUStoreServer <port>
Replace <port> with the desired port number.

Running the Client
To use the RUStore client, create an instance and connect to the server:

java
Copy code
RUStoreClient client = new RUStoreClient("localhost", <port>);
client.connect();
Replace <port> with the port number on which the server is running.

Usage
Client Methods
Here are the main methods provided by the RUStoreClient class:

connect(): Establishes a connection to the server.
put(String key, byte[] data): Stores data associated with a key. Returns 0 on success, 1 if the key already exists.
put(String key, String filePath): Stores data from a file associated with a key. Returns 0 on success, 1 if the key already exists.
get(String key): Retrieves data associated with a key. Returns the data as a byte array, or null if the key doesn't exist.
get(String key, String filePath): Retrieves data associated with a key and saves it to a file. Returns 0 on success, 1 if the key doesn't exist.
remove(String key): Removes data associated with a key. Returns 0 on success, 1 if the key doesn't exist.
list(): Retrieves a list of all stored keys. Returns the list as a string array, or null if no keys are stored.
disconnect(): Closes the connection to the server.
Example usage:

java
Copy code
RUStoreClient client = new RUStoreClient("localhost", 8080);
client.connect();

// Put data
byte[] data = "Hello, World!".getBytes();
int result = client.put("greeting", data);

// Get data
byte[] retrievedData = client.get("greeting");
System.out.println(new String(retrievedData));

// List keys
String[] keys = client.list();
for (String key : keys) {
    System.out.println(key);
}

// Remove data
client.remove("greeting");

// Disconnect
client.disconnect();
