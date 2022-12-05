import org.snmp4j.smi.OID;

import java.util.ArrayList;
import java.util.List;

public class TableModel {
    private OID oid;
    private List<Integer> columnSubOids = new ArrayList<>();

    public OID getOid() {
        return oid;
    }

    public void setOid(OID oid) {
        this.oid = oid;
    }

    public List<Integer> getColumnSubOids() {
        return columnSubOids;
    }

    public void setColumnSubOids(List<Integer> columnSubOids) {
        this.columnSubOids = columnSubOids;
    }

    public boolean hasColumnOid(OID columnOid) {
        for (Integer columnSubOid : columnSubOids) {
            OID fullColumnOid = new OID(this.oid.format() + columnSubOid);
            if (fullColumnOid.equals(columnOid)) {
                return true;
            }
        }
        return false;
    }
}
