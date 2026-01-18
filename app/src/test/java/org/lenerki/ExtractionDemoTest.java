package org.lenerki;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExtractionDemoTest {
    @Test
    public void pluginJsonExists() {
        // Verify manifest.json exists in resources
        assertNotNull(getClass().getClassLoader().getResource("manifest.json"));
    }
}
