package com.bbg.fx.fx_deals_importer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FxDealsImporterApplicationTests {

	@Test
	void contextLoads() {
	}

}
