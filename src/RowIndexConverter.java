import org.snmp4j.smi.OID;

public class RowIndexConverter {
    public String oidTo6BitAscii(OID oid) {
        StringBuilder _6BitAsciiSb = new StringBuilder();
        for (int decimalValue : oid.getValue()) {
            _6BitAsciiSb.append(decimalTo6BitAscii(decimalValue));
        }
        return _6BitAsciiSb.toString();
    }
    private String decimalTo6BitAscii(int decimalValue) {
        String _6BitHex = decimalTo6BitHex(decimalValue);
        // TODO Find ascii code in 6bit ASCII table
        return _6BitHex;
    }
    public String decimalTo6BitHex(int decimalValue) {
        int _6BitDecimal = decimalTo6BitDecimal(decimalValue);
        // Convert decimal to hex
        return Integer.toHexString(_6BitDecimal);
    }
    private int decimalTo6BitDecimal(int decimalValue) {
        String _6BitBinary = decimalTo6BitBinary(decimalValue);
        // Convert bits to decimal
        return Integer.parseUnsignedInt(_6BitBinary, 2);
    }
    private String decimalTo6BitBinary(int decimalValue) {
        // 1) Flip the nibble order
        int decimalSwappedNibibles = swapNibbles(decimalValue);
        // 2) Convert decimal to binary
        String binary = Integer.toBinaryString(decimalSwappedNibibles);
        // 3) Get last 6 bits
        return binary.substring(binary.length() - Math.min(binary.length(), 6));
    }
    private int swapNibbles(int x)
    {
        return ((x & 0x0F) << 4 | (x & 0xF0) >> 4);
    }
}
