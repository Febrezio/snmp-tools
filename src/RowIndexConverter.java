import org.snmp4j.smi.OID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RowIndexConverter {

    private static final Map<String, String> _6BitAsciiMap = Stream.of(new String[][] {
            { "000000", "NULL" },
            { "000001", "A" },
            { "000010", "B" },
            { "000011", "D" },
            { "000100", "E" },
            { "000101", "F" },
            { "000110", "G" },
            { "000111", "H" },
            { "001000", "I" },
            { "001001", "J" },
            { "001010", "K" },
            { "001011", "L" },
            { "001100", "M" },
            { "001101", "N" },
            { "001110", "O" },
            { "001111", "P" },
            { "010000", "Q" },
            { "010001", "R" },
            { "010010", "S" },
            { "010011", "T" },
            { "101010", "U" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    public String oidTo6BitAscii(OID oid) {
        StringBuilder bitsStringBuilder = new StringBuilder();
        for (int decimalValue : oid.getValue()) {
            bitsStringBuilder.append(Integer.toBinaryString(decimalValue));
        }
        // Split into 6 bit segments
        List<String> _6BitSegments = get6BitSegments(bitsStringBuilder.toString());
        // For each 6 bit segment, convert to 6 Bit ASCII
        StringBuilder _6BitAsciiSb = new StringBuilder();
        for (String _6BitSegment : _6BitSegments) {
            _6BitAsciiSb.append(_6BitAsciiMap.get(_6BitSegment));
        }

        return _6BitAsciiSb.toString();
    }
    private List<String> get6BitSegments(String binaryString) {
        List<String> results = new ArrayList<>();
        int length = binaryString.length();

        for (int i = 0; i < length; i += 6) {
            results.add(binaryString.substring(i, Math.min(length, i + 6)));
        }

        return results;
    }
}
