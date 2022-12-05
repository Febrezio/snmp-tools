import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.util.List;

public class Row {
    private OID index;
    private List<VariableBinding> columns;

    public OID getIndex() {
        return index;
    }

    public void setIndex(OID index) {
        this.index = index;
    }

    public List<VariableBinding> getColumns() {
        return columns;
    }

    public void setColumns(List<VariableBinding> columns) {
        this.columns = columns;
    }
}
