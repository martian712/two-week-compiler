import java.util.HashMap;
import java.util.Vector; 

class SymbolTable
{
        
    private HashMap<String,Value> symTable;
    
    public HashMap<String, Value> getSymTable() {
		return symTable;
	}

	public void setSymTable(HashMap<String, Value> symTable) {
		this.symTable = symTable;
	}

	public SymbolTable()
    {
        symTable = new HashMap<String,Value>();
    }
    
    public Value getValue(String key){
    	return symTable.get(key);
    }
    public void addItem(String key, int value){
    	symTable.put(key, new Value(value));
    }
    
    public void addItem(String key, String value){
    	symTable.put(key, new Value(value));
    }
    
    public boolean checkSTforItem( String key )
    {
       return symTable.containsKey(key);
    }

}