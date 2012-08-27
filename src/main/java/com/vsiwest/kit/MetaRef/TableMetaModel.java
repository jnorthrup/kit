package com.vsiwest.kit.MetaRef;

import com.vsiwest.kit.MD_KEYS;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import static com.vsiwest.kit.KitChannel.getSqlConnection;

/**
 * Property of vsiwest
 * User: jim
 * Date: Jun 9, 2007
 * Time: 11:29:55 AM
 */
public class TableMetaModel {
    private static final String COLUMN__NAME = "column_name";
    private final List<InboundRef> incomingKeys = new ArrayList<InboundRef>();
    private final List<OutboundRef> outOfContextRefs = new ArrayList<OutboundRef>();
    private final List<MetaDataRef> metaDataRefs = new ArrayList<MetaDataRef>();
    private ResultSet colResultSet;

    private TableMetaModel(String tableName, boolean oid) {
        init(tableName, oid);
    }

    private void init(final String tableName, boolean oid) {
        try {
             final DatabaseMetaData metaData = getSqlConnection().getMetaData();
            final ResultSet ik = metaData.getImportedKeys(null, null, tableName);
            final ResultSet ob = metaData.getExportedKeys(null, null, tableName);

            while (ik.next()) {
                EnumMap<MD_KEYS, String> attrMap = new EnumMap<MD_KEYS, String>(MD_KEYS.class);
                for (MD_KEYS MD : MD_KEYS.values())
                    attrMap.put(MD, ik.getString(MD.name()));
                incomingKeys.add(new InboundRef(attrMap));
            }
            while (ob.next()) {
                EnumMap<MD_KEYS, String> attrMap = new EnumMap<MD_KEYS, String>(MD_KEYS.class);
                for (MD_KEYS MD : MD_KEYS.values())
                    attrMap.put(MD, ob.getString(MD.name()));

                if (attrMap.get(MD_KEYS.PKTABLE_NAME).equals(tableName))
                    outOfContextRefs.add(new OutboundRef(attrMap));
            }


            this.colResultSet = metaData.getColumns("public", null, tableName, null);

            if (oid)
                metaDataRefs.add(new OidColumnRef(tableName));

            while (colResultSet.next())
                metaDataRefs.add(new SimpleColumnRef(tableName, colResultSet.getString(COLUMN__NAME)));

            metaDataRefs.addAll(incomingKeys);
            metaDataRefs.addAll(outOfContextRefs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static TableMetaModel createTableGraph(String tableName, boolean oid) {
        return createTableMetaModel(tableName, oid);
    }

    public ResultSet getColResultSet() {
        return colResultSet;
    }

    public Collection<InboundRef> getIncomingKeyArr() {
        return incomingKeys;
    }

    public Collection<OutboundRef> getOutOfContextArr() {
        return outOfContextRefs;
    }

    static public void main(String[] args) {

    }

    public List<MetaDataRef> getMetaDataRefs() {
        return metaDataRefs;
    }

    public static TableMetaModel createTableMetaModel(String tableName, boolean oid) {
        return new TableMetaModel(tableName, oid);
    }
}
