package com.concurrentsortedset.client.command;

import java.io.IOException;

import com.concurrentsortedset.config.CommonParam;

public class RemCommand extends ClientCommand {

	int set, key;
	
	public RemCommand(int set, int key) {
		this.set=set;
		this.key=key;
	}
	
	@Override
	public void fullCommand() throws IOException {
		dos.writeInt(set);
		dos.writeInt(key);
	}
	
	@Override
	public int commandCode() {
		return CommonParam.CODE_REM;
	}

}
