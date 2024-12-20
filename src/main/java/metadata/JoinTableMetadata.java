package metadata;

public class JoinTableMetadata {
    private String joinTableName;
    private String sourceTable;
    private String targetTable;
    private String sourceJoinColumn;
    private String targetJoinColumn;

    public String getJoinTableName() { return joinTableName; }
    public void setJoinTableName(String joinTableName) { this.joinTableName = joinTableName; }

    public String getSourceTable() { return sourceTable; }
    public void setSourceTable(String sourceTable) { this.sourceTable = sourceTable; }

    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }

    public String getSourceJoinColumn() { return sourceJoinColumn; }
    public void setSourceJoinColumn(String sourceJoinColumn) { this.sourceJoinColumn = sourceJoinColumn; }

    public String getTargetJoinColumn() { return targetJoinColumn; }
    public void setTargetJoinColumn(String targetJoinColumn) { this.targetJoinColumn = targetJoinColumn; }
}
