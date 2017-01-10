package com.jasonwjones.hyperion;

public class RenegadeUpdaterTest {

	public static void main(String[] args) throws Exception {
		RenegadeUpdater.main(new String[]{"--dimension", "Products", "--member", "Photo Printers"});
		//RenegadeUpdater.main(new String[] { "--dimension", "Products", "--unset" });
	}

}
