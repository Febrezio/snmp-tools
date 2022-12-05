import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snmp4j.smi.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MibStoreTest {

    @BeforeEach
    void setUp() {
        initMibStore();
    }

    @Test
    void getBindingTest_returnsExpectedIntValue() {
        OID oid = new OID("1.2.1.3.2.1");
        VariableBinding variableBinding = MibStore.getInstance().getBinding(oid);
        assertEquals(3, variableBinding.getVariable().toInt());
    }
    @Test
    void getBindingTest_returnsExpectedStringValue() {
        OID oid = new OID("1.2.1.5.3.1");
        VariableBinding variableBinding = MibStore.getInstance().getBinding(oid);
        assertEquals("Hi there", variableBinding.getVariable().toString());
    }
    @Test
    void getBindingTest_returnsExpectedIpStringValue() {
        OID oid = new OID("1.2.1.6.4.1");
        VariableBinding variableBinding = MibStore.getInstance().getBinding(oid);
        assertEquals("127.0.0.1", variableBinding.getVariable().toString());
    }

    @Test
    void getBindingTest_returnsInteger32() {
        OID oid = new OID("1.2.1.3.2.1");
        VariableBinding variableBinding = MibStore.getInstance().getBinding(oid);
        assertEquals(SMIConstants.SYNTAX_INTEGER32, variableBinding.getVariable().getSyntax());
    }
    @Test
    void getBindingTest_returnsOctetString() {
        OID oid = new OID("1.2.1.5.3.1");
        VariableBinding variableBinding = MibStore.getInstance().getBinding(oid);
        assertEquals(SMIConstants.SYNTAX_OCTET_STRING, variableBinding.getVariable().getSyntax());
    }
    @Test
    void getBindingTest_returnsIpAddress() {
        OID oid = new OID("1.2.1.6.4.1");
        VariableBinding variableBinding = MibStore.getInstance().getBinding(oid);
        assertEquals(SMIConstants.SYNTAX_IPADDRESS, variableBinding.getVariable().getSyntax());
    }

    @Test
    void addTable() {
    }

    @Test
    void addScalar() {
    }

    private static void initMibStore() {
        MibStore mibStore = MibStore.getInstance();
        mibStore.clear();
        // Scalars
        mibStore.addScalar(new VariableBinding(new OID("1.2.1.3.2.1"), new Integer32(3)));
        mibStore.addScalar(new VariableBinding(new OID("1.2.1.5.3.1"), new OctetString("Hi there")));
        mibStore.addScalar(new VariableBinding(new OID("1.2.1.6.4.1"), new IpAddress("127.0.0.1")));
        // Table 1
        Table table = new Table();
        table.setOid(new OID("1.2.1.3.5"));
        // Table 1 Model
        TableModel model = new TableModel();
        model.setOid(new OID(table.getOid() + ".1"));
        table.setModel(model);
        // Table 1 Row 1
        List<Row> rows = new ArrayList<>();
        Row row = new Row();
        row.setIndex(new OID("82.7.13.85"));
        rows.add(row);
        table.setRows(rows);
        // Table 1 Row 1 columns
        List<VariableBinding> columns = new ArrayList<>();
        columns.add(new VariableBinding(new OID(model.getOid() + ".1"), new Integer32(3)));
        columns.add(new VariableBinding(new OID(model.getOid() + ".2"), new OctetString("My Data")));
        columns.add(new VariableBinding(new OID(model.getOid() + ".3"), new IpAddress("127.0.0.1")));
        row.setColumns(columns);

        mibStore.addTable(table);
    }
}