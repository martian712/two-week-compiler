import java.util.HashMap;
import java.util.Vector; 

class SymbolTable
{
        
    private HashMap<String,Value> st;
    
    public SymbolTable()
    {
        st = new HashMap<String,Value>();
    }
    
    public void addItem(String key, int value){
    	st.put(key, new Value(value));
    }
    
    public void addItem(String key, String value){
    	st.put(key, new Value(value));
    }
    
    public boolean checkSTforItem( String key )
    {
       return st.containsKey(key);
    }

}