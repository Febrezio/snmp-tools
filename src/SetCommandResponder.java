import org.snmp4j.*;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.VariableBinding;

public class SetCommandResponder implements CommandResponder {

    @Override
    public void processPdu(CommandResponderEvent event) {
        System.out.println("Incoming Event = " + event);
        PDU requestPDU = event.getPDU();

        if (requestPDU.getType() != PDU.SET) {
            System.out.println("Event PDU not for this CommandResponder, will not process.");
            event.setProcessed(false);
            return;
        }

        ScopedPDU responsePDU = (ScopedPDU) requestPDU;
        responsePDU.setErrorStatus(PDU.noError);

        for (VariableBinding binding : requestPDU.getVariableBindings()) {
            System.out.println("Incoming VariableBinding = " + binding);
            System.out.println("Outgoing VariableBinding = " + binding);
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
}
