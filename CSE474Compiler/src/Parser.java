
/* PROGRAM Micro */

/* 	Java version of the Micro compiler from Chapter 2 of "Crafting a Compiler" --
*	for distribution to instructors 
*	Converted to Java by James Kiper in July 2003.
*	Further work and completion done by Nic Larson and Brandon Langmeier
*/

/* Original Micro grammar (Dr. Kiper)
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

/* Updated Phase 2 grammar (us)
	<system goal>       -> <program> #Finish
	<program>        	-> BEGIN #Start <statement_list> END
	<statement_list>   	-> <statement> | <statement><statement_list>
	<statement>        	-> READ( <id_list>);
	<statement>        	-> WRITE( <expr_list> );
	<statement>        	-> <declaration>
	<statement>        	-> <assignment>
	<declaration>      	-> INT id; | STRING id;
	<assignment>       	-> INT id <int_assignment> | STRING id <string_assignment> |
                			INT id <logic_assignment> |
							id #processID := <expression> |
							id #processID := <string_expression> |
							id #processID ~= <logexpression>
	<int_assignment>    -> := <expression>;
	<string_assignment> -> := <string_expression>;
	<logic_assignment>  -> ~= <logexpression>;
	<expression>        -> <factor> <addop> <expression> | <factor> 
	<logexpression>    	-> <logfactor> OR <logexpression> | <logfactor>
	<logfactor>        	-> <primary> AND <logfactor> | <logterm>
	<logterm>        	-> NOT <primary> | <primary>
	<factor>        	-> <primary> <multop> <factor> | <primary>
	<primary>        	-> id | IntLiteral | - IntLiteral | ( <expression> ) | ( <logexpression> )
	<str_expression>    -> <strprimary> | <strprimary> + <strprimary>
	<strprimary>        -> id | StringLiteral | + StringLiteral
	<addop>        		-> + | -
	<multop>        	-> * | / | %
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
        
        switch ( currentToken.getType() )
        {
            case Token.ID:
            {
            	
            	if(symbolTable.checkSTforItem(currentToken.getId()))	//<statment> -> <assignment> but where the id in assignment already exists
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
            	Expression lValue;
            	match( Token.INT);
            	lValue = decIdentifier();
            	if(currentToken.getType() == Token.ASSIGNOP)		//<statement> -> <assignment> then <assignment> -> INT id<int_assignment>;
            	{
            		intAssignment(lValue);
                    break;
            	}
            	else if(currentToken.getType() == Token.SEMICOLON)	//<statement> -> <declaration> then <declaration> -> <int_declaration>;
            	{
            		intDeclaration(lValue);
            		break;
            	}
            	else if(currentToken.getType() == Token.LOGASSIGNOP) //<statement> -> <assignment> then <assignement> -> INT id <log_assignement>;
            	{
            		logAssignment(lValue);
            		break;
            	}
            }
            case Token.STRING :
            {
            	StrExpression lValue;
            	match( Token.STRING);
            	lValue = decStrIdentifier();
            	if(currentToken.getType() == Token.ASSIGNOP)		//<statement> -> <assignment> then <assignment> -> <str_assignment>
            	{
            		strAssignment(lValue);
            		break;
            	}
            	else if(currentToken.getType() == Token.SEMICOLON)	//<statement> -> <declaration> then <declaration> -> <str_declaration>;
            	{
            		strDeclaration(lValue);
            		break;
            	}
            }
            default: error(currentToken);
        }
    }
    private void logAssignment(Expression leftSide) {
    	Expression lValue = leftSide;
    	Expression expr;
    	match(Token.LOGASSIGNOP);
    	expr = logexpression();
    	symbolTable.addItem(lValue.expressionName, expr.expressionIntValue);
    	codeFactory.generateAssignment(lValue, expr);
    	match(Token.SEMICOLON);
    }
    private void strAssignment(StrExpression leftSide){
    	StrExpression lValue = leftSide;
    	StrExpression expr;
    	match(Token.ASSIGNOP);
    	expr = strexpression();
    	symbolTable.addItem(lValue.expressionName, expr.expressionStrValue);
    	codeFactory.generateStrAssignment(lValue, expr);
    	match(Token.SEMICOLON);
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
    
    private void strDeclaration(StrExpression leftSide){
    	StrExpression lValue = leftSide;
    	StrExpression expr = new StrExpression(StrExpression.STRLITERALEXPR, "", "");
    	symbolTable.addItem(lValue.expressionName, expr.expressionStrValue);
    	codeFactory.generateStrAssignment(lValue, expr);
    	match(Token.SEMICOLON);
    }
    
	private void assignment() {
		if (symbolTable.checkSTforItem(currentToken.getId())) {
			if((symbolTable.getValue(currentToken.getId()).getType()).equals("INT")){
				Expression lValue;
				Expression expr;
				lValue = identifier();		
				if(currentToken.getType() == Token.ASSIGNOP) {
					match(Token.ASSIGNOP);
					expr = expression();
				}
				else if(currentToken.getType() == Token.LOGASSIGNOP) {
					match(Token.LOGASSIGNOP);
					expr = logexpression();
				}
				else {
					error(currentToken, "Incorrect Assignment Operator for an INT type on the left. Use \":=\" or \"~=\" for int assignments and logic assignments respectively.");
					return;
				}
				symbolTable.getValue(lValue.expressionName).setValue(expr.expressionIntValue);
				codeFactory.generateAssignment(lValue, expr);
				match(Token.SEMICOLON);
			}else{
				StrExpression lValue;
				StrExpression expr;
				lValue = stridentifier();
				match(Token.ASSIGNOP);
				expr = strexpression();
				if(expr.expressionType == StrExpression.STRIDEXPR) {
					symbolTable.getValue(lValue.expressionName).setValue(
							symbolTable.getValue(expr.expressionName).getStringValue());		//FLAG for possible problems
					codeFactory.generateStrAssignment(lValue, new StrExpression(StrExpression.STRLITERALEXPR, expr.expressionName, symbolTable.getValue(expr.expressionName).getStringValue()));
				}
				else {
					symbolTable.getValue(lValue.expressionName).setValue(expr.expressionStrValue);
					codeFactory.generateStrAssignment(lValue, expr);
				}
				match(Token.SEMICOLON);
			}
		} else {
			error(currentToken, "ID seen at beginning of statement, but variable has not been declared!");		//statment started with an ID but was this var was not previously declared
			
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
        
        result = factor();
        while ( currentToken.getType() == Token.PLUS || currentToken.getType() == Token.MINUS )
        {
            leftOperand = result;
            op = addOperation();
            rightOperand = expression();
            result = codeFactory.generateArithExpr( leftOperand, rightOperand, op );
        }
        return result;
    }
    
    private Expression logexpression()
    {
    	Expression result;
    	Expression leftOperand;
    	Expression rightOperand;
    	Operation op;
    	
    	result = logfactor();
    	while ( currentToken.getType() == Token.AND) {
    		leftOperand = result;
    		op = andOperation();
    		rightOperand = logexpression();
    		result = codeFactory.generateArithExpr(leftOperand, rightOperand, op);
    	}
    	return result;
    }
    
    private Expression factor()
    {
    	Expression result;
    	Expression leftOperand;
    	Expression rightOperand;
    	Operation op;
    	
    	result = primary();
    	while(currentToken.getType() == Token.MULT || currentToken.getType() == Token.DIV || currentToken.getType() == Token.MOD){
    		
    		leftOperand = result;
    		op = multOperation();
    		rightOperand = factor();
    		if(op.opType == Token.DIV && (leftOperand.expressionIntValue == 0 || rightOperand.expressionIntValue == 0)) {
    			error(currentToken, "ERROR! Detected a divide by zero operation.");
    			return result;
    		}
    		result = codeFactory.generateArithExpr(leftOperand, rightOperand, op);
    	}
    	
    	return result;
    }
    
    private Expression logfactor() {
    	Expression result;
    	Expression leftOperand;
    	Expression rightOperand;
    	Operation op;
    	
    	result = logterm();
    	while(currentToken.getType() == Token.OR) {
    		leftOperand = result;
    		op = orOperation();
    		rightOperand = logfactor();
    		result = codeFactory.generateArithExpr(leftOperand, rightOperand, op);
    	}
    	return result;
    }
    
    private Expression logterm() {
    	Expression result;
    	
    	if(currentToken.getType() == Token.NOT) {
    		match(Token.NOT);
    		result = primary();
    		if(result.expressionType == Expression.IDEXPR) {
    			if(symbolTable.checkSTforItem(result.expressionName)) {
    				if(symbolTable.getValue(result.expressionName).getIntValue() == 0) {
    					symbolTable.getValue(result.expressionName).setValue(1);
    					result.expressionIntValue = 1;
    				}
    				else {
    					symbolTable.getValue(result.expressionName).setValue(0);
    					result.expressionIntValue = 0;
    				}
    			}
    			else
    				error(currentToken, "Variable used in logic expression not defined?");
    		}
    		if(result.expressionIntValue == 0)
    			result.expressionIntValue = 1;
    		else
    			result.expressionIntValue = 0;
    	}
    	else {
    		result = primary();
    	}
    	return result;
    }
    
    private StrExpression strprimary() {
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
    		result = codeFactory.generateStrConcat( leftOperand, rightOperand);	
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
    private Operation andOperation(){
    	
    	Operation op = new Operation();
    	match(Token.AND);
    	op = processOperation();
    	return op;
    	
    }
    
    private Operation orOperation(){
    	Operation op = new Operation();
    	match(Token.OR);
    	op = processOperation();
    	return op;
    }
    
    private Operation multOperation() 
    {
    	Operation op = new Operation();
    	switch (currentToken.getType())
    	{
    	case Token.MULT:
    	{
    		match(Token.MULT);
    		op = processOperation();
    		break;
    	}
    	case Token.DIV:
    	{
    		match(Token.DIV);
    		op = processOperation();
    		break;
    	}
    	case Token.MOD:
    	{
    		match(Token.MOD);
    		op = processOperation();
    		break;
    	}
    	default: error(currentToken);
    	}
    	return op;
    }
    
    private Expression decIdentifier()
    {
        Expression expr;
        match( Token.ID );
        expr = processNewIdentifier();
        return expr;
    }
    
    private Expression identifier()
    {
    	Expression expr;
    	match( Token.ID);
    	expr = processIdentifier();
    	return expr;
    }
    
    private StrExpression decStrIdentifier() {
    	StrExpression expr;
    	match(Token.ID);
    	expr = processNewStrIdentifier();
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
        else if ( previousToken.getType() == Token.MULT) op.opType = Token.MULT;
        else if ( previousToken.getType() == Token.DIV) op.opType = Token.DIV;
        else if ( previousToken.getType() == Token.MOD) op.opType = Token.MOD;
        else if ( previousToken.getType() == Token.AND) op.opType = Token.AND;
        else if ( previousToken.getType() == Token.OR) op.opType = Token.OR;
        else error( previousToken );
        return op;
    }
    
    private Expression processIdentifier()
    {
        Expression expr = new Expression( Expression.IDEXPR, previousToken.getId());
        
        if ( ! symbolTable.checkSTforItem( previousToken.getId() ) )
        {
            //symbolTable.addItem(previousToken.getId());
            error(previousToken, "ERROR variable undefined");
        }
        return expr;
    }
    
    private Expression processNewIdentifier() {
    	Expression expr = new Expression(Expression.IDEXPR, previousToken.getId());
    	
    	if( symbolTable.checkSTforItem(previousToken.getId())) {
    		error(previousToken, "ERROR Variable already defined!");
    	}
    	return expr;
    }
    
    private StrExpression processStrIdentifier()
    {
    	StrExpression expr = new StrExpression(StrExpression.STRIDEXPR, previousToken.getId());
    	if ( ! symbolTable.checkSTforItem( previousToken.getId()))
    	{
    		//codeFactory.generateStrAssignment(previousToken.getId(), new StrExpression(StrExpression.STRIDEXPR, "", ""));
    		error(previousToken, "ERROR Variable not defined");
    	}
    	return expr;
    }
    
    private StrExpression processNewStrIdentifier()
    {
    	StrExpression expr = new StrExpression(StrExpression.STRIDEXPR, previousToken.getId());
    	if ( symbolTable.checkSTforItem( previousToken.getId()))
    	{
    		//codeFactory.generateStrAssignment(previousToken.getId(), new StrExpression(StrExpression.STRIDEXPR, "", ""));
    		error(previousToken, "ERROR Variable already defined");
    	}
    	return expr;
    }
    
    private void error(Token token, String str) {
    	System.out.println( "Syntax error! Parsing token type " + token.toString() + " at line number " + 
                scanner.getLineNumber() );
    	System.out.println("Extra error message: " + str);
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