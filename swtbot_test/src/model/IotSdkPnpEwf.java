package model;

import java.util.Arrays;

public class IotSdkPnpEwf extends AbstractApplication {

	public IotSdkPnpEwf() {
		super();
		this.application = "iotSdkPnpEwf";
		this.applicationNumber = 7;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 0);
		this.gccExecuted = Arrays.asList(1);
		this.ccrxExecuted = Arrays.asList(1);
		this.board = Arrays.asList("CK-RX65N");
	}

}
