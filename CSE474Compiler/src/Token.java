

public class Token
{
    private String id;
    private int type;
    public final static int LexERROR = 0;
    public final static int BEGIN = 1;
    public final static int END = 2;
    public final static int READ = 3;
    public final static int WRITE = 4;
    public final static int ID = 5;
    public final static int LPAREN = 6;
    public final static int RPAREN = 7;
    public final static int COMMA = 8;
    public final static int SEMICOLON = 9;
    public final static int ASSIGNOP = 10;
    public final static int PLUS = 11;
    public final static int MINUS = 12;
    public final static int INTLITERAL = 13;
    public final static int EOF = 14;
    public final static int INT = 15;
    public final static int STRING = 16;
    public final static int STRINGLITERAL = 17;
    public final static int MULT = 18;
    public final static int DIV = 19;
    public final static int MOD = 20;
    public final static int AND = 21;
    public final static int OR = 22;
    public final static int NOT = 23;
    public final static int LOGASSIGNOP = 24;
    public final static int LESSTHAN = 25;
    public final static int GREATERTHAN = 26;
    public final static int LESSEQUAL = 27;
    public final static int GREATEREQUAL = 28;
    public final static int EQUAL = 29;
    public final static int NOTEQUAL = 30;
    public final static int IF = 31;
    public final static int ENDIF = 32;
    public final static int ELSE = 33;
    public final static int ENDELSE = 34;
    public final static int WHILE = 35;
    public final static int ENDWHILE = 36;
    public final static int FUNC = 37;
    public final static int ENDFUNC = 38;
    public final static int CALL = 39;

    public Token( String tokenString, int tokenType)
    {
        id = tokenString;
        type = tokenType;
        if (tokenType == ID)
        {
            String temp = tokenString.toLowerCase();
            if ( temp.compareTo("begin") == 0) type = BEGIN;
            else if ( temp.compareTo( "end") == 0) type = END;
            else if ( temp.compareTo("read") == 0) type = READ;
            else if ( temp.compareTo("write") == 0) type = WRITE;
            else if ( temp.compareTo("int") == 0) type = INT;
            else if ( temp.compareTo("string") == 0) type = STRING;
            else if ( temp.compareTo("and") == 0) type = AND;
            else if ( temp.compareTo("or") == 0) type = OR;
            else if ( temp.compareTo("not") == 0) type = NOT;
            else if ( temp.compareTo("if") == 0) type = IF;
            else if ( temp.compareTo("endif") == 0) type = ENDIF;
            else if ( temp.compareTo("else") == 0) type = ELSE;
            else if ( temp.compareTo("endelse") == 0) type = ENDELSE;
            else if ( temp.compareTo("while") == 0) type = WHILE;
            else if ( temp.compareTo("endwhile") == 0) type = ENDWHILE;
            else if ( temp.compareTo("func") == 0) type = FUNC;
            else if ( temp.compareTo("endfunc") == 0) type = ENDFUNC;
            else if ( temp.compareTo("call") == 0) type = CALL;
            
        }
    }
    public String getId()
    {
        return id;
    }
    public int getType()
    {
        return type;
    }
    public String toString()
    {
        String str;
        switch (type)
        {
            case LexERROR : str = "Lexical Error"; break;
            case BEGIN : str = "BEGIN"; break;
            case END : str = "END"; break;
            case READ : str = "READ"; break;
            case WRITE : str = "WRITE"; break;
            case ID: str = "ID"; break;
            case LPAREN : str = "LPAREN"; break;
            case RPAREN : str = "RPAREN"; break;
            case COMMA : str = "COMMA"; break;
            case SEMICOLON : str = "SEMICOLON"; break;
            case ASSIGNOP : str = "ASSIGNOP"; break;
            case PLUS : str = "PLUS"; break;
            case MINUS : str = "MINUS"; break;
            case INTLITERAL : str = "INTLITERAL"; break;
            case EOF : str = "EOF"; break;
            case INT : str = "INT"; break;
            case STRING : str = "STRING"; break;
            case STRINGLITERAL : str = "STRINGLITERAL"; break;
            case MULT : str = "MULT"; break;
            case DIV : str = "DIV"; break;
            case MOD : str = "MOD"; break;
            case AND : str = "AND"; break;
            case OR : str = "OR"; break;
            case NOT : str = "NOT"; break;
            case LOGASSIGNOP : str = "LOGASSIGNOP"; break;
            case LESSTHAN : str = "LESSTHAN"; break;
            case GREATERTHAN : str = "GREATERTHAN"; break;
            case LESSEQUAL : str = "LESS THAN OR EQUAL"; break;
            case GREATEREQUAL : str = "GREATER THAN OR EQUAL"; break;
            case EQUAL : str = "EQUAL"; break;
            case NOTEQUAL : str = "NOTEQUAL"; break;
            case IF : str = "IF"; break;
            case ENDIF : str = "ENDIF"; break;
            case ELSE : str = "ELSE"; break;
            case ENDELSE : str = "ENDELSE"; break;
            case WHILE : str = "WHILE"; break;
            case ENDWHILE : str = "ENDWHILE"; break;
            case FUNC : str = "FUNC"; break;
            case ENDFUNC : str = "ENDFUNC"; break;
            case CALL : str = "CALL"; break;
            default: str = "Lexical Error";
        }
        return str;
    }

}