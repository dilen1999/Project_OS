# Network Programming Project: File Sharing

This project is a simple file sharing system implemented using Java and socket programming. It consists of two main components: a server application and a client application. The server listens for incoming connections from clients and receives files sent by them, while the client allows users to select files and send them to the server.

## Features

- **Server Application**: 
  - Listens for incoming connections on a specified port.
  - Receives files sent by clients and stores them.
  - Displays received files to users.

- **Client Application**:
  - Allows users to select files from their local system.
  - Sends selected files to the server for storage.
  - Provides options to update the host IP address for file transfer.
  - Supports file sharing with multiple devices by specifying the IP address of the server.

## How to Use

### Server Application
1. Compile and run the `Server.java` file.
2. The server will start listening for connections on the specified port.
3. Once a client connects and sends a file, the server will receive and display it.

### Client Application
1. Compile and run the `Client.java` file.
2. Enter the IP address of the server in the host text field. The default is `localhost`.
3. Click on the "Choose File" button to select a file from your local system.
4. Click on the "Send File" button to send the selected file to the server.

## File Types
- The system supports both text files (`txt`) and other file types such as images, videos, etc.
- Text files are displayed directly in the client application, while other file types can be downloaded and viewed.

## Sharing with Multiple Devices
- Users can share files with multiple devices by specifying the IP address of the server.
- Each client connecting to the server can send files independently, enabling simultaneous file sharing among multiple devices.

## Requirements
- Java Development Kit (JDK) 8 or higher.
- Java IDE or compiler to build and run the Java files.
