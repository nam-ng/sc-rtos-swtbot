package model;

import java.util.Arrays;

public class FileX extends AbstractApplication {
	public FileX() {
		super();
		this.application = "filex";
		this.applicationNumber = 1;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 1);
		this.gccExecuted = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
		this.ccrxExecuted = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
		this.board = Arrays.asList("RSKRX65N-2MB", "CloudKitRX65N", "CK-RX65N", "Custom", "TargetBoardRX130",
				"RSKRX140", "RSKRX660", "RSKRX66T", "EnvisionKitRX72N", "RSKRX671");
	}
}
