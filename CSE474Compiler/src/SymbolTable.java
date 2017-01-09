import java.util.Vector; 

class SymbolTable
{
        
    private Vector st;
    
    public SymbolTable()
    {
        st = new Vector();
    }
    
    public void addItem( Token token )
    {
        st.add( token.getId() );
    }
    
    public boolean checkSTforItem( String id )
    {
       return st.contains( id );
    }

}