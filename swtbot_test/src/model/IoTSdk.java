package model;

import java.util.Arrays;

public class IoTSdk extends AbstractApplication {

	public IoTSdk() {
		super();
		this.application = "iotSdk";
		this.applicationNumber = 4;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 1);
		this.gccExecuted = Arrays.asList(1, 1, 1, 1, 1);
		this.ccrxExecuted = Arrays.asList(1, 1, 1, 1, 1);
		this.board = Arrays.asList("RSKRX65N-2MB", "CloudKitRX65N", "CK-RX65N", "EnvisionKitRX72N", "RSKRX671");
	}
}
