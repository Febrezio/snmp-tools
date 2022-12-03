import org.snmp4j.smi.*;

import java.util.ArrayList;
import java.util.List;

public class MibStore {
    private final List<VariableBinding> bindings = new ArrayList<>();

    public MibStore() {
        // Scalar 1
        bindings.add(new VariableBinding(new OID("1.2.1.3.2.1"), new Integer32(3)));
        // Table 1
        bindings.add(new VariableBinding(new OID("1.2.1.3.5")));
        // Table 1 Entry
        bindings.add(new VariableBinding(new OID("1.2.1.3.5.1")));
        // Table 1 Entry Columns
        bindings.add(new VariableBinding(new OID("1.2.1.3.5.1.1"), new Integer32(3)));
        bindings.add(new VariableBinding(new OID("1.2.1.3.5.1.2"), new OctetString("My Data")));
        bindings.add(new VariableBinding(new OID("1.2.1.3.5.1.3"), new IpAddress("127.0.0.1")));
    }

    public VariableBinding getBinding(OID oid) {
        VariableBinding matchingBinding = null;
        for (VariableBinding binding : bindings) {
            if (binding.getOid().startsWith(oid)) {
                matchingBinding = binding;
                break;
            }
        }

        return matchingBinding;
    }
}
