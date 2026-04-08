package ngo.friendship.syncapp.config;

import ngo.friendship.syncapp.model.SyncTable;

import static ngo.friendship.syncapp.util.Constant.*;

import org.springframework.stereotype.Component;

@Component
public class SqlBuilderComponent {


    // Method to generate SQL for updating the sent flag for client tables
    public String getClientTableTaskSetCompleteTemplete(SyncTable table) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ");
        builder.append(table.getTblNameWithSchema());

        String primaryColumn = table.getTblPrimaryColumn();
        String primaryColumnType = table.getTblPrimaryColumnType();
        if (table.getTblPrimaryColumn() == null || table.getTblPrimaryColumn().isEmpty()) {
            primaryColumn = "id";
        }

        String trackingColumn = table.getTblCTrackingColumn();
        if (table.getTblCTrackingColumn() == null || table.getTblCTrackingColumn().isEmpty()) {
            trackingColumn = "sent_flag";
        }

        switch (table.getDbType()) {
            case POSTGRES:
                builder.append(" SET ")
                        .append(trackingColumn)
                        .append(" = true ");
                builder.append(" WHERE ")
                        .append(primaryColumn)
                        .append(" = ");

                // Add casting based on column type
                builder.append(getPostgresCastPlaceholder(primaryColumnType));
                break;

            default:
                builder.append(" SET ")
                        .append(trackingColumn)
                        .append(" = 1 ");
                builder.append(" WHERE ")
                        .append(primaryColumn)
                        .append(" = ? ");
                break;
        }

        return builder.toString();
    }


    private String getPostgresCastPlaceholder(String columnType) {
        if (columnType == null) {
            return "?::uuid";
        }

        switch (columnType.toUpperCase()) {
            case "UUID":
                return "?::uuid";
            case "INT":
            case "INTEGER":
                return "?::integer";
            case "BIGINT":
                return "?::bigint";
            case "SMALLINT":
                return "?::smallint";
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
                return "?::text";
            case "DATE":
                return "?::date";
            case "TIMESTAMP":
                return "?::timestamp";
            case "BOOLEAN":
                return "?::boolean";
            case "DECIMAL":
            case "NUMERIC":
                return "?::numeric";
            case "REAL":
                return "?::real";
            case "DOUBLE":
            case "DOUBLE PRECISION":
                return "?::double precision";
            case "JSON":
            case "JSONB":
                return "?::jsonb";
            default:
                return "?";
        }
    }
}
