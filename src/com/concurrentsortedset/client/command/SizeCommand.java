package com.concurrentsortedset.client.command;

import java.io.IOException;

import com.concurrentsortedset.config.CommonParam;

public class SizeCommand extends ClientCommand {

	int set;
	
	public SizeCommand(int set) {
		this.set=set;
	}
	
	@Override
	public void fullCommand() throws IOException {
		dos.writeInt(set);
		dos.flush();
		int set_size = dis.readInt();
		System.out.println("The size of set " + set + " is: " + set_size);
	}

	@Override
	public int commandCode() {
		return CommonParam.CODE_SIZE;
	}
}
