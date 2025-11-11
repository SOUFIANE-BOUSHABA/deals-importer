package com.bbg.fx.fx_deals_importer;

import org.springframework.boot.SpringApplication;

public class TestFxDealsImporterApplication {

	public static void main(String[] args) {
		SpringApplication.from(FxDealsImporterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
