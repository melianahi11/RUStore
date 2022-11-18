package com.RUStore;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RUStoreClient {

	private String host;
	private int port;

	private Socket socket;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	/**
	 * RUStoreClient Constructor, initializes default values
	 * for class members
	 *
	 * @param host	host url
	 * @param port	port number
	 */
	public RUStoreClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Opens a socket and establish a connection to the object store server
	 * running on a given host and port.
	 *
	 * @return		n/a, however throw an exception if any issues occur
	 */
	public void connect() {
		try {
			socket = new Socket(host, port);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT be 
	 * overwritten
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param data	byte array representing arbitrary data object
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 */
	public int put(String key, byte[] data) {
		int success = 1;

		try {
			toServer.writeObject(new RUDataPayload(RUStoreConstants.COMMAND_PUT, key, -1, data.length, data));
			
			RUDataPayload payload = (RUDataPayload) fromServer.readObject();
			
			success = payload.getSuccess();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		} 
		
		return success;
	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT 
	 * be overwritten.
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param file_path	path of file data to transfer
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 */
	public int put(String key, String file_path) {
		int success = 1;

		try {
			Path path = Paths.get(file_path);
			byte[] data = Files.readAllBytes(path);
			
			toServer.writeObject(new RUDataPayload(RUStoreConstants.COMMAND_PUT, key, -1, data.length, data));
			
			RUDataPayload payload = (RUDataPayload) fromServer.readObject();
			
			success = payload.getSuccess();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		} 
		
		return success;
	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server.
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		object data as a byte array, null if key doesn't exist.
	 *        		Throw an exception if any other issues occur.
	 */
	public byte[] get(String key) {
		byte[] data = null;

		try {
			toServer.writeObject(new RUDataPayload(RUStoreConstants.COMMAND_GET, key, -1, 0, null));

			RUDataPayload payload = (RUDataPayload) fromServer.readObject();
			
			data = payload.getData();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
		return data;
	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server and places it in a file. 
	 * 
	 * @param key	key associated with the object
	 * @param	file_path	output file path
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 */
	public int get(String key, String file_path) {
		int success = 1;

		try {
			toServer.writeObject(new RUDataPayload(RUStoreConstants.COMMAND_GET, key, -1, 0, null));

			RUDataPayload payload = (RUDataPayload) fromServer.readObject();
			
			byte[] responseData = payload.getData();
			
			success = payload.getSuccess();
			
			if(success == 0) {
				Path path = Paths.get(file_path);
				Files.write(path, responseData);
			}
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
		return success;
	}

	/**
	 * Removes data object associated with a given key 
	 * from the object store server. Note: No need to download the data object, 
	 * simply invoke the object store server to remove object on server side
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 */
	public int remove(String key) {
		int success = 1;

		try {
			toServer.writeObject(new RUDataPayload(RUStoreConstants.COMMAND_REMOVE, key, -1, 0, null));

			RUDataPayload payload = (RUDataPayload) fromServer.readObject();
			
			success = payload.getSuccess();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
		return success;
	}

	/**
	 * Retrieves of list of object keys from the object store server
	 * 
	 * @return		List of keys as string array, null if there are no keys.
	 *        		Throw an exception if any other issues occur.
	 */
	public String[] list() {
		String[] list = null;

		try {
			toServer.writeObject(new RUDataPayload(RUStoreConstants.COMMAND_LIST, null, -1, 0, null));

			RUDataPayload payload = (RUDataPayload) fromServer.readObject();
			
			byte[] data = payload.getData();
			if(data != null) {
				String temp = new String(data, StandardCharsets.UTF_8);
				list = temp.split("\n");
			}
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
		return list;
	}

	/**
	 * Signals to server to close connection before closes 
	 * the client socket.
	 * 
	 * @return		n/a, however throw an exception if any issues occur
	 */
	public void disconnect() {
		try {
			toServer.writeBytes(RUStoreConstants.COMMAND_DISCONNECT + "\n");

			toServer.close();
			fromServer.close();
			socket.close();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

	}

}
