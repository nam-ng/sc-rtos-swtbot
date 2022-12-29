package model;

import java.util.Arrays;

public class GuiXDraw2d extends AbstractApplication {
	public GuiXDraw2d() {
		super();
		this.application = "guixDraw2d";
		this.applicationNumber = 12;
		this.toolchain = Arrays.asList("GCC", "CCRX");
		this.statusToolchain = Arrays.asList(1, 1);
		this.gccExecuted = Arrays.asList(1, 1);
		this.ccrxExecuted = Arrays.asList(1, 1);
		this.board = Arrays.asList("RSKRX65N-2MB", "EnvisionKitRX72N");
	}
}
