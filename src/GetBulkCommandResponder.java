import org.snmp4j.*;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class GetBulkCommandResponder implements CommandResponder {

    @Override
    public void processPdu(CommandResponderEvent event) {
        System.out.println("Incoming Event = " + event);
        PDU requestPDU = event.getPDU();

        if (requestPDU.getType() != PDU.GETBULK) {
            System.out.println("Event PDU not for this CommandResponder, will not process.");
            event.setProcessed(false);
            return;
        }

        ScopedPDU responsePDU = (ScopedPDU) requestPDU;
        responsePDU.setErrorStatus(PDU.noError);

        for (VariableBinding binding : requestPDU.getVariableBindings()) {
            System.out.println("Incoming VariableBinding = " + binding);
            binding.setOid(new OID("1.3.6.1"));
            Variable variable = new OctetString("HELLO");
            binding.setVariable(variable);
            System.out.println("Outgoing VariableBinding = " + binding);
        }

        // FIXME the GetTable util expects an extra binding, else throws wrong order error
        VariableBinding binding = new VariableBinding(new OID("1.3.7"), new OctetString("WHAT"));
        requestPDU.add(binding);

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
}
