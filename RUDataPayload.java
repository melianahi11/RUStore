package com.RUStore;

import java.io.Serializable;

public class RUDataPayload implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String command;
	private final String key;
	private final int success;
	private final int dataLength;
	private final byte[] data;

	public RUDataPayload(String command, String key, int success, int dataLength, byte[] data)
	{
	   this.command = command;
	   this.key = key;
	   this.success = success;
	   this.dataLength = dataLength;
	   this.data = data;
	}

	public String getCommand()
	{
	    return this.command;
	}

	public String getKey()
	{
	    return this.key;
	}

	public int getSuccess()
	{
	    return this.success;
	}
	
	public int getDataLength()
	{
	    return this.dataLength;
	}

	public byte[] getData()
	{
	    return this.data;
	}
}
