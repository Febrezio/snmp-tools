import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SnmpAgent {
    private static final String SMNP_USER = "testsnmp";
    private static final String SMNP_AUTH_PASSWORD = "password";
    private static final String SMNP_PRIV_PASSWORD = "password";
    private static final String SMNP_IP = "127.0.0.1";
    private static final int SMNP_PORT = 161;
    private final Snmp snmp;
    private TransportMapping transportMapping;

    public SnmpAgent() {
        snmp = new Snmp();
        snmp.getMessageDispatcher().addCommandResponder(new CommandResponder() {
            @Override
            public void processPdu(CommandResponderEvent event) {
                System.out.println("Incoming Event = " + event);
                PDU requestPDU = event.getPDU();
                ScopedPDU responsePDU = (ScopedPDU) requestPDU;
                responsePDU.setErrorStatus(PDU.noError);
                OID lastOid = null;
                for (VariableBinding binding : requestPDU.getVariableBindings()) {
                    System.out.println("Incoming VariableBinding = " + binding);
                    switch (requestPDU.getType()) {
                        case PDU.GET:
                        case PDU.GETBULK:
                            Variable variable = new OctetString("HELLO");
                            binding.setVariable(variable);
                            break;
                        case PDU.SET:
                            break;
                        default:
                            System.out.println("PDU Type not supported: " + PDU.getTypeString(requestPDU.getType()));
                            responsePDU.setErrorStatus(PDU.genErr);
                    }
                    lastOid = binding.getOid();
                    System.out.println("Outgoing VariableBinding = " + binding);
                }

                if (requestPDU.getType() == PDU.GETBULK && lastOid != null) {
                    VariableBinding binding = new VariableBinding(new OID("1.3.6.2.1.5.'hello'.1"), new OctetString("WHAT?"));
                    requestPDU.add(binding);
                }

                responsePDU.setType(PDU.RESPONSE);
                responsePDU.setErrorIndex(0);
                StatusInformation statusInformation =new StatusInformation();
                event.setProcessed(true);
                try {
                    event.getMessageDispatcher().returnResponsePdu(event.getMessageProcessingModel(),
                            event.getSecurityModel(),
                            event.getSecurityName(),
                            event.getSecurityLevel(),
                            responsePDU,
                            event.getMaxSizeResponsePDU(),
                            event.getStateReference(),
                            statusInformation);
                } catch (MessageException e) {
                    e.printStackTrace();
                }
            }
        });

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

    public static void main(String[] args) {
        SnmpAgent snmpAgent = new SnmpAgent();
        try {
            snmpAgent.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
