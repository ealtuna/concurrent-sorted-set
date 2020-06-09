package com.concurrentsortedset.client.command;

import java.io.IOException;

import com.concurrentsortedset.config.CommonParam;

public class GetCommand extends ClientCommand {

	int set, key;
	
	public GetCommand(int set, int key) {
		this.set = set;
		this.key = key;
	}
	
	@Override
	public void fullCommand() throws IOException {
		dos.writeInt(set);
		dos.writeInt(key);
		dos.flush();
		int score_retrieved = dis.readInt();
		System.out.println("The score value of set " + set + " and key " + key + " is: " + score_retrieved);
	}

	@Override
	public int commandCode() {
		return CommonParam.CODE_GET;
	}
}
