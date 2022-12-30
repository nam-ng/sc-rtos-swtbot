package model;

import java.util.Arrays;

public class Iperf extends AbstractApplication {

	public Iperf() {
		super();
		this.application = "iperf";
		this.applicationNumber = 3;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 1);
		this.gccExecuted = Arrays.asList(1, 1, 1);
		this.ccrxExecuted = Arrays.asList(1, 1, 1);
		this.board = Arrays.asList("RSKRX65N-2MB", "CK-RX65N", "EnvisionKitRX72N");
	}

}
