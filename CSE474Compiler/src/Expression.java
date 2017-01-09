public class Expression    
{
    public final static int IDEXPR = 0;
    public final static int LITERALEXPR = 1;
    public final static int TEMPEXPR = 2;
    
    public int expressionType;
    public String expressionName;
    public int expressionIntValue;
        
    public Expression( )
    {
        expressionType = 0;
        expressionName = "";
    }
        
    public Expression( int type, int value)
    {
        expressionType = type;
        expressionIntValue = value;
    }

    public Expression( int type, String name)
    {
        expressionType = type;
        expressionName = name;
    }
    
    public Expression( int type, String name, int val)
    {
        expressionType = type;
        expressionName = name;
        expressionIntValue = val;
    }
}
