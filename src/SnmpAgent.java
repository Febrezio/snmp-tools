import org.snmp4j.CommandResponder;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.*;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
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

    public static void main(String[] args) {
        SnmpAgent snmpAgent = new SnmpAgent();
        List<CommandResponder> commandResponders = new ArrayList<>();
        commandResponders.add(new GetCommandResponder());
        commandResponders.add(new GetBulkCommandResponder());
        commandResponders.add(new SetCommandResponder());
        snmpAgent.setCommandResponders(commandResponders);
        try {
            snmpAgent.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
