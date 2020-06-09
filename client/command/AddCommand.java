package com.concurrentsortedset.client.command;

import java.io.IOException;

import com.concurrentsortedset.config.CommonParam;

public class AddCommand extends ClientCommand {

	int set, key, score;
	
	public AddCommand(int set, int key, int score) {
		this.set=set;
		this.key=key;
		this.score=score;
	}
	
	@Override
	public void fullCommand() throws IOException {
		dos.writeInt(set);
		dos.writeInt(key);
		dos.writeInt(score);
	}

	@Override
	public int commandCode() {
		return CommonParam.CODE_ADD;
	}
}
