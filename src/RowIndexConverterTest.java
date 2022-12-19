import org.junit.Before;
import org.junit.Test;
import org.snmp4j.smi.OID;

import static org.junit.Assert.*;

public class RowIndexConverterTest {
    private RowIndexConverter rowIndexConverter;

    @Before
    public void setUp() throws Exception {
        rowIndexConverter = new RowIndexConverter();
    }
    @Test
    public void oidTo6BitAsciiTest() {
        String result = rowIndexConverter.oidTo6BitAscii(new OID("85.49.90"));
        assertEquals("USE", result);
    }
}