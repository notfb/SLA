package com.github.notfb.sla;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Abstract base class for common test functionality.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "testSLAContext.xml" })
public abstract class MeasurementTest {

    @Resource
    protected MeasurementAspect measurementAspect;
    
    protected final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

    @Before
	public void setUp() throws Exception {
		measurementAspect.reset();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

    @After
	public void tearDown() throws Exception {
		etmMonitor.render(new SimpleTextRenderer());
		etmMonitor.reset();
	}
}
