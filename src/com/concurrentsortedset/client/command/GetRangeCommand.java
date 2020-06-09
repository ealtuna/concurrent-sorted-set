package com.concurrentsortedset.client.command;

import java.io.IOException;
import java.util.List;

import com.concurrentsortedset.config.CommonParam;

public class GetRangeCommand extends ClientCommand {

	List<Integer> sets;
	int lower, upper;
	
	public GetRangeCommand(List<Integer> sets, int lower, int upper) {
		this.sets=sets;
		this.lower=lower;
		this.upper=upper;
	}
	
	@Override
	public void fullCommand() throws IOException {
		for (Integer set : sets)
		{
			dos.writeInt(set);
		}
		dos.writeInt(-1);
		dos.writeInt(lower);
		dos.writeInt(upper);
		dos.flush();
		int key,score;
		System.out.println("In the range [" + lower + "-" + upper + "]there are the key->score");
		while ((key=dis.readInt()) != -1)
		{
			score=dis.readInt();
			System.out.println(key + "->" + score);
		}
	}

	@Override
	public int commandCode() {
		return CommonParam.CODE_GETRANGE;
	}
}
