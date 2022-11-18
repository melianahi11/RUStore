package com.RUStore;

import java.util.*;
import java.util.concurrent.*;

public class RUStoreData {
	private static RUStoreData ruStoreData;
	private static ConcurrentMap<String, byte[]> datastore;

	public static RUStoreData getInstance() {
		if(ruStoreData == null) {
			ruStoreData = new RUStoreData();
		}
		return ruStoreData;
	}

	public RUStoreData() {
		datastore = new ConcurrentHashMap<>();
	}
	
	public byte[] get(String key) {
		return datastore.get(key);
	}
	
	public void put(String key, byte[] data) {
		datastore.put(key, data);
	}
	
	public boolean contains(String key) {
		return datastore.containsKey(key);
	}
	
	public void remove(String key) {
		datastore.remove(key);
	}
	
	public String list() {
		Set<String> keySet = datastore.keySet();
		if(!keySet.isEmpty()) {
			String list = "";
			Iterator<String> setIterator = keySet.iterator();
	        while(setIterator.hasNext()){
	            String key = (String) setIterator.next();
	            list = list.concat(key).concat("\n");
	        }
			return list;
		}
		return null;
	}
}
