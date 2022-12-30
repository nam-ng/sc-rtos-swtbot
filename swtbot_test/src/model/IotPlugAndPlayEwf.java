package model;

import java.util.Arrays;

public class IotPlugAndPlayEwf extends AbstractApplication {

	public IotPlugAndPlayEwf() {
		super();
		this.application = "iotPlugAndPlayEwf";
		this.applicationNumber = 9;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 0);
		this.gccExecuted = Arrays.asList(1);
		this.ccrxExecuted = Arrays.asList(1);
		this.board = Arrays.asList("CK-RX65N");
	}

}
