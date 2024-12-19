package metadata;

public class ForeignKeyMetadata {

	private String columnName;
	private String referecedTable;
	private String referecedColumn;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getReferecedTable() {
		return referecedTable;
	}
	public void setReferecedTable(String referecedTable) {
		this.referecedTable = referecedTable;
	}
	public String getReferecedColumn() {
		return referecedColumn;
	}
	public void setReferecedColumn(String referecedColumn) {
		this.referecedColumn = referecedColumn;
	}
	
	
	
}
