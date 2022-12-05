import org.snmp4j.smi.OID;

import java.util.List;

public class Table {

    private OID oid;
    private TableModel model;
    private List<Row> rows;

    public OID getOid() {
        return oid;
    }

    public void setOid(OID oid) {
        this.oid = oid;
    }

    public TableModel getModel() {
        return model;
    }

    public void setModel(TableModel model) {
        this.model = model;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
