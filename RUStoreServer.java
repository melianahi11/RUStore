package com.RUStore;

import java.io.*;
import java.net.*;

public class RUStoreServer implements Runnable{
	private Socket socket;
	private int thread;
	private RUStoreData ruStoreData;

	public RUStoreServer(Socket socket, int thread) {
		this.socket = socket;
		this.thread = thread;

		ruStoreData = RUStoreData.getInstance();
	}

	/* ============== COMMAND UTILS =============*/

	private void disconnect() {
		try {
			socket.close();
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private RUDataPayload put(String key, byte[] data) {
		int success = 1;
		try {
			if(!ruStoreData.contains(key)) {
				ruStoreData.put(key, data);
				success = 0;
			}

		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return new RUDataPayload(RUStoreConstants.RESPONSE, null, success, 0, null);
	}

	private RUDataPayload get(String key) {
		byte[] data = null;
		int dataLength = 0;
		int success = 1;
		try {
			if(ruStoreData.contains(key)) {
				data = ruStoreData.get(key);
				dataLength = data.length;
				success = 0;	
			}

		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return new RUDataPayload(RUStoreConstants.RESPONSE, null, success, dataLength, data);
	}
	
	private RUDataPayload remove(String key) {
		int success = 1;
		try {
			if(ruStoreData.contains(key)) {
				ruStoreData.remove(key);
				success = 0;
			}

		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return new RUDataPayload(RUStoreConstants.RESPONSE, null, success, 0, null);
	}
	
	private RUDataPayload list() {
		try {
			String list = ruStoreData.list();
			
			if(list == null) {
				return new RUDataPayload(RUStoreConstants.RESPONSE, null, -1, 0, null);
			}
			else {
				byte[] data = list.getBytes();
				return new RUDataPayload(RUStoreConstants.RESPONSE, null, -1, data.length, data);
			}
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return new RUDataPayload(RUStoreConstants.RESPONSE, null, -1, 0, null);
	}

	/* ================= DRIVERS ================*/

	public void run() {
		try {
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
			
			for(;;) {
				RUDataPayload payload = (RUDataPayload) fromClient.readObject();

				String command = payload.getCommand();
				String key = payload.getKey();
				int success = payload.getSuccess();
				int dataLength = payload.getDataLength();
				byte[] data = payload.getData();
				
				RUDataPayload response;

				switch(command) {
				case RUStoreConstants.COMMAND_DISCONNECT:
					disconnect();
					return;
				case RUStoreConstants.COMMAND_PUT:
					response = put(key, data);
					toClient.writeObject(response);
					break;
				case RUStoreConstants.COMMAND_GET:
					response = get(key);
					toClient.writeObject(response);
					break;
				case RUStoreConstants.COMMAND_REMOVE:
					response = remove(key);
					toClient.writeObject(response);
					break;
				case RUStoreConstants.COMMAND_LIST:
					response = list();
					toClient.writeObject(response);
					break;
				default:
					System.out.println("Bad Command");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * RUObjectServer Main(). Note: Accepts one argument -> port number
	 */
	public static void main(String args[]){
		if(args.length != 1) {
			System.out.println("Invalid number of arguments. You must provide a port number.");
			return;
		}


		int port = Integer.parseInt(args[0]);

		try (ServerSocket serverSocket = new ServerSocket(port, RUStoreConstants.BACKLOG)) {
			int threadIndex = 0;

			for (;;) {
				Socket connection = serverSocket.accept();
				new Thread(new RUStoreServer(connection, threadIndex++)).start();
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
