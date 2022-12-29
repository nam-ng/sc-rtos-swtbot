package model;

import java.util.Arrays;

public class GuiX16 extends AbstractApplication {

	public GuiX16() {
		super();
		this.application = "guix16pp";
		this.applicationNumber = 11;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 1);
		this.gccExecuted = Arrays.asList(1, 1);
		this.ccrxExecuted = Arrays.asList(1, 1);
		this.board = Arrays.asList("RSKRX65N-2MB", "EnvisionKitRX72N");
	}

}
