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
    public void decimalTo6BitHexTest() {
        String result = rowIndexConverter.decimalTo6BitHex(85);
        assertEquals("15", result);
    }
    @Test
    public void oidTo6BitAsciiTest() {
        String result = rowIndexConverter.oidTo6BitAscii(new OID("85.49"));
        assertEquals("1513", result);
    }
}