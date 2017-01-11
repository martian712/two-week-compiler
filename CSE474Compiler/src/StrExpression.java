
public class StrExpression {
	public final static int STRIDEXPR = 0;
    public final static int STRLITERALEXPR = 1;
    public final static int STRTEMPEXPR = 2;
    
    public int expressionType;
    public String expressionName;
    public String expressionStrValue;
    
    public StrExpression( )
    {
        expressionType = 0;
        expressionName = "";
    }

    public StrExpression( int type, String name)
    {
        expressionType = type;
        expressionName = name;
    }
    
    public StrExpression( int type, String name, String val)
    {
        expressionType = type;
        expressionName = name;
        expressionStrValue = val;
    }
    
}
