
/* PROGRAM Micro */

/* 	Java version of the Micro compiler from Chapter 2 of "Crafting a Compiler" --
*	for distribution to instructors 
*	Converted to Java by James Kiper in July 2003.
*
*/

/* Original Micro grammar
   <program>	    -> #Start BEGIN <statement list> END
   <statement list> -> <statement> {<statement>}
   <statement>	    -> <ident> := <expression> #Assign ;
   <statement>	    -> READ ( <id list> ) ;
   <statement>	    -> WRITE ( <expr list> ) ;
   <id list>	    -> <ident> #ReadId {, <ident> #ReadId }
   <expr list>	    -> <expression> #WriteExpr {, <expression> #WriteExpr}
   <expression>	    -> <primary> {<add op> <primary> #GenInfix}
   <primary>	    -> ( <expression> )
   <primary>	    -> <ident>
   <primary>	    -> IntLiteral #ProcessLiteral
   <primary>	    -> MinusOp #ProcessNegative IntLiteral #ProcessLiteral
   <add op>	    	-> PlusOp #ProcessOp
   <add op>	    	-> MinusOp #ProcessOp
   <ident>	    	-> Id #ProcessId
   <system goal>    -> <program> EofSym #Finish
 */

/* Updated Grammar
	<program>    		->	BEGIN <statement list> END
	<statement list>	->  <statement> | <statement><statement list>
	<statement>    		->  <assignment>;
	<statement>     	->  <declaration>;
	<statement>    		->  READ ( <variable list> ) ;
	<statement>    		->  WRITE ( <expr list> ) ;
	<assignment> 		->  <string assignment> | <int assignment>
	<string_assignment> ->  <identifier> := <string>
	<int_assignment> 	->  <identifier> := <integer> | <identifier> := <expression>
	<identifier> 		->  STRING <name> | INT <name> | <variable>
	<name> 				-> 	ID
	<string> 			-> 	StringLiteral | <concatString>
	<concatString> 		->  StringLiteral + <string>
	<declaration> 		->  <identifier>
	<integer> 			->  <digit> | <digit><integer>
	<variable list> 	->  <variable> | <variable> <variable list>
	<variable> 			->  <name>
	<expr list> 		->  <expression> | <expression><expr list>
	<expression> 		-> 	<factor><op><expression> | <factor>
	<factor> 			-> 	(<expression>)
	<factor> 			-> 	<variable> | <integer>
	<op> 				-> 	+ | -
 */


public class Parser
{
    private static Scanner scanner;
    private static SymbolTable symbolTable;
    private static CodeFactory codeFactory;
    private Token currentToken;
    private Token previousToken;
    private static boolean signSet = false;
    private static String signFlag = "+";

    public Parser()
    {
        
    }

    static public void main (String args[])
    {
        Parser parser = new Parser();
      //  scanner = new Scanner( args[0]);
        scanner = new Scanner("test.txt");
        codeFactory = new CodeFactory();
        symbolTable = new SymbolTable();
        parser.parse();
    }
    
    public void parse()
    {
        currentToken = scanner.findNextToken();
        systemGoal();
    }
    
    private void systemGoal()
    {
        program();
        codeFactory.generateData();
    }
    
    private void program()
    {
        match( Token.BEGIN );
        codeFactory.generateStart();
        statementList();
        match( Token.END );
        codeFactory.generateExit();
    }
    
    private void statementList()
    {
        while ( currentToken.getType() == Token.ID || currentToken.getType() == Token.READ || 
                    currentToken.getType() == Token.WRITE || currentToken.getType() == Token.INT || currentToken.getType() == Token.STRING)
        {
            statement();
        }
    }
    
    private void statement()
    {
        Expression lValue;
        Expression expr;
        
        switch ( currentToken.getType() )
        {
            case Token.ID:
            {
            	
            	if(symbolTable.checkSTforItem(currentToken.getId()))
            	{
            		assignment();
            	}
            	else
            	{
            		error(currentToken);
            	}
            	break;
            }
            case Token.READ :
            {
                match( Token.READ );
                match( Token.LPAREN );
                idList();
                match( Token.RPAREN );
                match( Token.SEMICOLON );
                break;
            }
            case Token.WRITE :
            {
                match( Token.WRITE );
                match( Token.LPAREN );
                expressionList();
                match( Token.RPAREN );
                match( Token.SEMICOLON );
                break;
            }
            case Token.INT :
            {
            	match( Token.INT);
            	lValue = identifier();
            	if(currentToken.getType() == Token.ASSIGNOP)		//<statement> -> <assignment>;
            	{
            		intAssignment(lValue);
                    break;
            	}
            	else if(currentToken.getType() == Token.SEMICOLON)	//<statement> -> <declaration>;
            	{
            		intDeclaration(lValue);
            		break;
            	}
            }
            case Token.STRING :
            {
            	match( Token.STRING);
            	lValue = identifier();
            	if(currentToken.getType() == Token.ASSIGNOP)
            	{
            		strAssignment(lValue);
            		break;
            	}
            	else if(currentToken.getType() == Token.SEMICOLON)
            	{
            		strDeclaration(lValue);
            		break;
            	}
            }
            default: error(currentToken);
        }
    }
    
    private void strAssignment(Expression leftSide){
    	Expression lValue = leftSide;
    	StrExpression expr;
    	match(Token.ASSIGNOP);
    	expr = strexpression();
<<<<<<< HEAD
=======
    	symbolTable.addItem(lValue.expressionName, expr.expressionStrValue);
    	codeFactory.generateStrAssignment(lValue, expr);
>>>>>>> branch 'master' of https://github.com/martian712/two-week-compiler.git
    }
    private void intAssignment(Expression leftSide){
    	Expression lValue = leftSide;
    	Expression expr;
    	match(Token.ASSIGNOP);
    	expr = expression();
    	symbolTable.addItem(lValue.expressionName, expr.expressionIntValue);
    	codeFactory.generateAssignment(lValue, expr);
    	match(Token.SEMICOLON);
    }
    
    private void intDeclaration(Expression leftSide){
    	Expression lValue = leftSide;
    	Expression expr = new Expression(Expression.LITERALEXPR, 0);
    	symbolTable.addItem(lValue.expressionName, expr.expressionIntValue);
    	codeFactory.generateAssignment(lValue, expr);
    	match(Token.SEMICOLON);
    }
    
    private void strDeclaration(Expression leftSide){
    	Expression lValue = leftSide;
    	StrExpression expr = new StrExpression(StrExpression.STRLITERALEXPR, "", "");
    	symbolTable.addItem(lValue.expressionName, expr.expressionStrValue);
    	codeFactory.generateStrAssignment(lValue, expr);
    	match(Token.SEMICOLON);
    }
    
	private void assignment() {
		Expression lValue;
		Expression expr;
		if (symbolTable.checkSTforItem(currentToken.getId())) {
			if((symbolTable.getValue(currentToken.getId()).getType()).equals("INT")){
				lValue = identifier();
				match(Token.ASSIGNOP);
				expr = expression();
				symbolTable.getValue(lValue.expressionName).setValue(expr.expressionIntValue);
				codeFactory.generateAssignment(lValue, expr);
				match(Token.SEMICOLON);
			}else{
				match(Token.ID);
				match(Token.ASSIGNOP);
				expression();
			}
		} else {
			switch (previousToken.getType()) {
			case Token.INT: {
				lValue = identifier();
                match( Token.ASSIGNOP );
                expr = expression();
                symbolTable.addItem(lValue.expressionName, expr.expressionIntValue);
                codeFactory.generateAssignment( lValue, expr );	
                match( Token.SEMICOLON );
                break;

			}
			case Token.STRING: {
				// TODO assign a new string to symbol table
				lValue = identifier();
                match( Token.ASSIGNOP );
                expr = expression();							//TODO this needs to be different for strings somehow! string() - <string> -> StringLiteral | StringLiteral + <string>
                codeFactory.generateAssignment( lValue, expr );	//TODO same todo as the one in the Token.INT case above
                match( Token.SEMICOLON );
                break;
			}
			}
		}
	}
    
    private void idList()
    {
        Expression idExpr;
        idExpr = identifier();
        codeFactory.generateRead(idExpr);
        while ( currentToken.getType() == Token.COMMA )
        {
            match(Token.COMMA);
            idExpr = identifier();
            codeFactory.generateRead(idExpr);
        }
    }
    
    private void expressionList()
    {
        Expression expr;
        expr = expression();
        codeFactory.generateWrite(expr);
        while ( currentToken.getType() == Token.COMMA )
        {
            match( Token.COMMA );
            expr = expression();
            codeFactory.generateWrite(expr);
        }
    }
    
    private Expression expression()
    {
        Expression result;
        Expression leftOperand;
        Expression rightOperand;
        Operation op;
        
        result = primary();
        while ( currentToken.getType() == Token.PLUS || currentToken.getType() == Token.MINUS )
        {
            leftOperand = result;
            op = addOperation();
            rightOperand = primary();
            result = codeFactory.generateArithExpr( leftOperand, rightOperand, op );
        }
        return result;
    }
    
    private StrExpression strprimary(){
    	StrExpression result = new StrExpression();
    	switch(currentToken.getType()){
    	case Token.ID:
    		result = stridentifier();
    		break;
    	case Token.STRINGLITERAL:
    		match(Token.STRINGLITERAL);
    		result = strprocessLiteral();
    		break;
    	case Token.PLUS:
    		match(Token.PLUS);
    		match(Token.STRINGLITERAL);
    		result = strprocessLiteral();
    		break;
    	default:
    		error(currentToken);
    		
    	}
    	return result;
    }
    
    private StrExpression strexpression()
    {
    	StrExpression result;
    	StrExpression leftOperand;
    	StrExpression rightOperand;
    	Operation op;
    	
    	result = strprimary();
    	while (currentToken.getType() == Token.PLUS)
    	{
    		leftOperand = result;
    		op = addOperation();
    		rightOperand = strprimary();
    		result = codeFactory.generateStrConcat( leftOperand, rightOperand);		//TODO add the codefactory method for string concat
    	}
    	return result;
    }
    
    private Expression primary()
    {
        Expression result = new Expression();
        switch ( currentToken.getType() )
        {
            case Token.LPAREN :
            {
                match( Token.LPAREN );
                result = expression();
                match( Token.RPAREN );
                break;
            }
            case Token.ID:
            {
                result = identifier();
                break;
            }
            case Token.INTLITERAL:
            {
                match(Token.INTLITERAL);
                result = processLiteral();
                break;
            }
            case Token.MINUS:
            {
                match(Token.MINUS);
                processSign();
                match(Token.INTLITERAL);
                result = processLiteral();
                break;
            }
            case Token.PLUS:
            {
                match(Token.PLUS);
                processSign();
                match(Token.INTLITERAL);
                result = processLiteral();
                break;
            }
            default: error( currentToken );
        }
        return result;
    }
    
    private Operation addOperation()
    {
        Operation op = new Operation();
        switch ( currentToken.getType() )
        {
            case Token.PLUS:
            {
                match( Token.PLUS ); 
                op = processOperation();
                break;
            }
            case Token.MINUS:
            {
                match( Token.MINUS ); 
                op = processOperation();
                break;
            }
            default: error( currentToken );
        }
        return op;
    }
    
    private Expression identifier()
    {
        Expression expr;
        match( Token.ID );
        expr = processIdentifier();
        return expr;
    }
    
    private StrExpression stridentifier()
    {
    	StrExpression expr;
    	match(Token.ID);
    	expr = processStrIdentifier();
    	return expr;
    }
    
    private void match( int tokenType)
    {
    	if(tokenType == Token.END) {					//Removes need for extra lines past program. 
    		if(currentToken.getType() != tokenType) {	//If looking for END and doesn't see end, return an error but keep parsing
    			error(tokenType);
    		}
    		else {										//If looking for END and find END, do not call findNextToken, break for program to end.
    			return;
    		}
    	}
        previousToken = currentToken;
        if ( currentToken.getType() == tokenType )
            currentToken = scanner.findNextToken();
        else 
        {
            error( tokenType );
            currentToken = scanner.findNextToken();
        }
    }

    private void processSign()
    {
    	Parser.signSet = true;
    	if ( previousToken.getType() == Token.PLUS ) 
    	{
    		Parser.signFlag = "+";
    	} else
    	{
    		Parser.signFlag = "-";
    	}
    }
    
    private StrExpression strprocessLiteral(){
    	return new StrExpression(StrExpression.STRLITERALEXPR, previousToken.getId(), previousToken.getId());
    }
    
    private Expression processLiteral()
    {
    	Expression expr;
        int value = ( new Integer( previousToken.getId() )).intValue();
        if (Parser.signSet && Parser.signFlag.equals("-"))
        {
        	 expr = new Expression( Expression.LITERALEXPR, "-"+previousToken.getId(), value*-1 );
        } else
        {
        	 expr = new Expression( Expression.LITERALEXPR, previousToken.getId(), value ); 
        }
        Parser.signSet = false;
        return expr;
    }
    
    private Operation processOperation()
    {
        Operation op = new Operation();
        if ( previousToken.getType() == Token.PLUS ) op.opType = Token.PLUS;
        else if ( previousToken.getType() == Token.MINUS ) op.opType = Token.MINUS;
        else error( previousToken );
        return op;
    }
    
    private Expression processIdentifier()
    {
        Expression expr = new Expression( Expression.IDEXPR, previousToken.getId());
        
        if ( ! symbolTable.checkSTforItem( previousToken.getId() ) )
        {
            //symbolTable.addItem(previousToken.getId());
            codeFactory.generateDeclaration( previousToken );
        }
        return expr;
    }
    
    private StrExpression processStrIdentifier()
    {
    	StrExpression expr = new StrExpression(StrExpression.STRIDEXPR, previousToken.getId());
    	if ( ! symbolTable.checkSTforItem( previousToken.getId()))
    	{
    		codeFactory.generateDeclaration(previousToken);
    	}
    	return expr;
    }
    
    private void error( Token token )
    {
        System.out.println( "Syntax error! Parsing token type " + token.toString() + " at line number " + 
                scanner.getLineNumber() );
        if (token.getType() == Token.ID )
            System.out.println( "ID name: " + token.getId() );
    }
    private void error( int tokenType )
    {
        System.out.println( "Syntax error! Parsing token type " +tokenType + " at line number " + 
                scanner.getLineNumber() );
    }
}