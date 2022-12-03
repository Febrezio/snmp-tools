import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.RetrievalEvent;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SnmpAgentTest {
    private static final String SMNP_USER = "testsnmp";
    private static final String SMNP_AUTH_PASSWORD = "password";
    private static final String SMNP_PRIV_PASSWORD = "password";
    private static final String SMNP_IP = "127.0.0.1";
    private static final int SMNP_PORT = 161;
    private static Snmp snmp;
    private static Address targetAddress;
    private static UserTarget target;

    @BeforeAll
    public static void setup() {
        targetAddress = GenericAddress.parse("udp:" + SMNP_IP + "/" + SMNP_PORT);
        TransportMapping transport;
        try {
            transport = new DefaultUdpTransportMapping();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        try {
            transport.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // add user to the USM
        snmp.getUSM().addUser(new OctetString(SMNP_USER),
                new UsmUser(new OctetString(SMNP_USER),
                        AuthSHA.ID,
                        new OctetString(SMNP_AUTH_PASSWORD),
                        PrivAES128.ID,
                        new OctetString(SMNP_PRIV_PASSWORD)));
        // create the target
        target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(5000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(new OctetString(SMNP_USER));
    }

    @Test
    public void setTest() {
        // create the PDU
        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID("1.3.6"), new OctetString("HI")));

        // send the PDU
        ResponseEvent responseEvent;
        try {
            responseEvent = snmp.set(pdu, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // extract the response PDU (could be null if timed out)
        PDU responsePDU = responseEvent.getResponse();

        if (responsePDU != null && responsePDU.getErrorStatus() == PDU.noError) {
            System.out.println("responsePDU VariableBindings:");
            for (VariableBinding binding : responsePDU.getVariableBindings()) {
                System.out.println("VariableBinding OID: " + binding.getOid().format());
                System.out.println("VariableBinding Syntax: " + binding.getSyntax());
                System.out.println("VariableBinding Variable: " + binding.getVariable());
            }

            VariableBinding binding = responsePDU.getVariableBindings().get(0);
            assertEquals("HI", binding.getVariable().toString());

        } else if (responsePDU != null) {
            System.err.println("responsePDU ErrorStatus: " + responsePDU.getErrorStatus());
            System.err.println("responsePDU ErrorStatusText: " + responsePDU.getErrorStatusText());
            System.err.println("responsePDU ErrorIndex: " + responsePDU.getErrorIndex());
            fail("responsePDU has error");
        }
        else {
            if (responseEvent.getError() != null) {
                responseEvent.getError().printStackTrace();
            }
            fail("responsePDU is null");
        }
    }

    @Test
    public void getTest() {
        // create the PDU
        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID("1.3.6"), new OctetString("HI")));

        // send the PDU
        ResponseEvent responseEvent;
        try {
            responseEvent = snmp.get(pdu, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // extract the response PDU (could be null if timed out)
        PDU responsePDU = responseEvent.getResponse();

        if (responsePDU != null && responsePDU.getErrorStatus() == PDU.noError) {
            System.out.println("responsePDU VariableBindings:");
            for (VariableBinding binding : responsePDU.getVariableBindings()) {
                System.out.println("VariableBinding OID: " + binding.getOid().format());
                System.out.println("VariableBinding Syntax: " + binding.getSyntax());
                System.out.println("VariableBinding Variable: " + binding.getVariable());
            }

            VariableBinding binding = responsePDU.getVariableBindings().get(0);
            assertEquals("HELLO", binding.getVariable().toString());
        } else if (responsePDU != null) {
            System.err.println("responsePDU ErrorStatus: " + responsePDU.getErrorStatus());
            System.err.println("responsePDU ErrorStatusText: " + responsePDU.getErrorStatusText());
            System.err.println("responsePDU ErrorIndex: " + responsePDU.getErrorIndex());
            fail("responsePDU has error");
        }
        else {
            if (responseEvent.getError() != null) {
                responseEvent.getError().printStackTrace();
            }
            fail("responsePDU is null");
        }
    }

    @Test
    public void getTableTest() {
        OID[] columnOids = new OID[1];
        columnOids[0] = new OID("1.3.6");

        TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
        List<TableEvent> tableEvents = utils.getTable(target, columnOids, null, null);
        for (TableEvent tableEvent : tableEvents) {

            if (tableEvent.getStatus() == RetrievalEvent.STATUS_OK) {
                System.out.println("tableEvent VariableBindings:");
                for (VariableBinding columns : tableEvent.getColumns()) {
                    System.out.println("VariableBinding OID: " + columns.getOid().format());
                    System.out.println("VariableBinding Syntax: " + columns.getSyntax());
                    System.out.println("VariableBinding Variable: " + columns.getVariable());
                }
            } else {
                System.err.println("tableEvent ErrorStatus: " + tableEvent.getStatus());
                System.err.println("tableEvent ErrorMessage: " + tableEvent.getErrorMessage());
                if (tableEvent.getException() != null) {
                    tableEvent.getException().printStackTrace();
                }
                fail("tableEvent has error");
            }
        }
    }
}