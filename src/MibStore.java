import org.snmp4j.smi.*;

import java.util.*;

public class MibStore {
    private static MibStore INSTANCE;
    private final Map<OID, Table> oidTableMap = new HashMap<>();
    private final List<VariableBinding> bindings = new ArrayList<>();

    private final Comparator<VariableBinding> bindingOidComparator = new Comparator<>() {
        @Override
        public int compare(VariableBinding o1, VariableBinding o2) {
            return o1.getOid().compareTo(o2.getOid());
        }
    };

    private MibStore() {
    }

    public static MibStore getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MibStore();
        }

        return INSTANCE;
    }

    // FIXME This needs to handle both table column and scalar OIDs
    public VariableBinding getBinding(OID oid) {
        VariableBinding matchingBinding = null;
        for (VariableBinding binding : bindings) {
            if (oid.startsWith(binding.getOid())) {
                matchingBinding = binding;
                break;
            }
        }

        return matchingBinding;
    }

    public void addTable(Table table) {
        for (Row row : table.getRows()) {
            OID rowIndex = row.getIndex();
            for (VariableBinding column : row.getColumns()) {
                column.getOid().append(rowIndex);
                bindings.add(column);
            }
        }
        bindings.sort(bindingOidComparator);

        oidTableMap.put(table.getOid(), table);
    }

    public void addScalar(VariableBinding scalar){
        bindings.add(scalar);
        bindings.sort(bindingOidComparator);
    }

    public void clear(){
        bindings.clear();
        oidTableMap.clear();
    }
}
