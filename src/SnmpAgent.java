import org.snmp4j.CommandResponder;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SnmpAgent {
    private static final String SMNP_USER = "testsnmp";
    private static final String SMNP_AUTH_PASSWORD = "password";
    private static final String SMNP_PRIV_PASSWORD = "password";
    private static final String SMNP_IP = "127.0.0.1";
    private static final int SMNP_PORT = 161;
    private final Snmp snmp;
    private TransportMapping transportMapping;
    private List<CommandResponder> commandResponders;

    public SnmpAgent() {
        snmp = new Snmp();

        try {
            transportMapping = new DefaultUdpTransportMapping(new UdpAddress(SMNP_IP + "/" + SMNP_PORT));
            snmp.addTransportMapping(transportMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
        SecurityProtocols.getInstance().addDefaultProtocols();
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);
        usm.setEngineDiscoveryEnabled(true);
        SecurityModels.getInstance().addSecurityModel(usm);

        UsmUser user = new UsmUser(new OctetString(SMNP_USER), AuthSHA.ID,
                new OctetString(SMNP_AUTH_PASSWORD), PrivAES128.ID, new OctetString(SMNP_PRIV_PASSWORD));
        usm.addUser(new OctetString(SMNP_USER), user);

        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm.getLocalEngineID().getValue()));
    }

    public void run() throws IOException {
        while (true) {
            if (!transportMapping.isListening()) {
                System.out.println("Setting transport mappings to listen mode");
                snmp.listen();
            }
            try {
                System.out.println("Sleep 5 seconds before next transport mappings listen mode check...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setCommandResponders(List<CommandResponder> commandResponders) {
        this.commandResponders = commandResponders;
        for (CommandResponder commandResponder : commandResponders) {
            snmp.getMessageDispatcher().addCommandResponder(commandResponder);
        }
    }

    private static void initMibStore() {
        MibStore mibStore = MibStore.getInstance();
        mibStore.clear();
        mibStore.addScalar(new VariableBinding(new OID("1.2.1.3.2.1"), new Integer32(3)));
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
        row.setIndex(new OID("2.7.3.5"));
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

    private static List<CommandResponder> makeCommandResponders() {
        List<CommandResponder> commandResponders = new ArrayList<>();
        commandResponders.add(new GetCommandResponder());
        commandResponders.add(new GetBulkCommandResponder());
        commandResponders.add(new SetCommandResponder());
        return commandResponders;
    }

    public static void main(String[] args) {
        SnmpAgent snmpAgent = new SnmpAgent();
        snmpAgent.setCommandResponders(makeCommandResponders());
        initMibStore();
        try {
            snmpAgent.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
