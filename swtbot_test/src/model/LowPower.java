package model;

import java.util.Arrays;

public class LowPower extends AbstractApplication {

	public LowPower() {
		super();
		this.application = "lowPower";
		this.applicationNumber = 15;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 1);
		this.gccExecuted = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1);
		this.ccrxExecuted = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1);
		this.board = Arrays.asList("RSKRX65N-2MB", "CloudKitRX65N", "CK-RX65N", "Custom", "TargetBoardRX130",
				"RSKRX140", "RSKRX660", "EnvisionKitRX72N", "RSKRX671");
	}
}
