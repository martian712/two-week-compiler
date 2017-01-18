import java.util.ArrayList;
import java.util.Map;

class CodeFactory {
	private static int tempCount;
	private static ArrayList<String> variablesList;
	private static SymbolTable strVariables;
	private static int labelCount = 0;
	private static boolean firstWrite = true;

	public CodeFactory() {
		tempCount = 0;
		variablesList = new ArrayList<String>();
		strVariables = new SymbolTable();
	}
	
	Expression generateNot(Expression right, Operation op){
		String falseLable = generateLabel("__false");
		String trueLable = generateLabel("__true");
		String cont = generateLabel("__cont");
		Expression tempExpr = new Expression(Expression.TEMPEXPR, createTempName());
		if (right.expressionType == Expression.LITERALEXPR) {
			System.out.println("\tMOVL " + "$" + right.expressionName + ", %eax");
		} else {
			System.out.println("\tMOVL " + right.expressionName + ", %eax");
		}
		System.out.println("\tCMPL $0, %eax");
		System.out.println("\tJE "+ trueLable);
		System.out.println("\tJNE " +falseLable);
		System.out.println(falseLable + ":");
		System.out.println("\tMOVL $0, " + tempExpr.expressionName);
		System.out.println("\tJMP " + cont);
		System.out.println(trueLable + ":");
		System.out.println("\tMOVL $1, " + tempExpr.expressionName);
		System.out.println("\tJMP " + cont);
		System.out.println(cont + ":");
		if(right.expressionIntValue == 0){
			tempExpr.expressionIntValue = 1;
		}else{
			tempExpr.expressionIntValue = 0;
		}
		return tempExpr;
	}

	void generateDeclaration(String ID) {
		variablesList.add(ID);
	}
	
	Expression generateArithExpr(Expression left, Expression right, Operation op) {
		String falseLable = generateLabel("__false");
		String trueLable = generateLabel("__true");
		String cont = generateLabel("__cont");
		Expression tempExpr = new Expression(Expression.TEMPEXPR, createTempName());
		if (right.expressionType == Expression.LITERALEXPR) {
			System.out.println("\tMOVL " + "$" + right.expressionName + ", %ebx");
		} else {
			System.out.println("\tMOVL " + right.expressionName + ", %ebx");
		}
		if (left.expressionType == Expression.LITERALEXPR) {
			System.out.println("\tMOVL " + "$" + left.expressionName + ", %eax");
		} else {
			System.out.println("\tMOVL " + left.expressionName + ", %eax");
		}
		if (op.opType == Token.PLUS) {
			System.out.println("\tADD %ebx, %eax");
			
		}else if (op.opType == Token.MINUS) {
			System.out.println("\tSUB %ebx, %eax");
		}else if (op.opType == Token.MULT){
			System.out.println("\tIMULL %ebx");
		}else if (op.opType == Token.DIV){
			System.out.println("\tXORL %edx, %edx");
			System.out.println("\tIDIV %ebx");
		}else if (op.opType == Token.MOD){
			System.out.println("\tXORL %edx, %edx");
			System.out.println("\tIDIV %ebx");
			System.out.println("\tMOVL %edx, " + tempExpr.expressionName);
			return tempExpr;
		}else if(op.opType == Token.AND){
			System.out.println("\tCMPL $0, %eax");
			System.out.println("\tJE " + falseLable);
			System.out.println("\tCMPL $0, %ebx");
			System.out.println("\tJE " + falseLable);
			System.out.println("\tJNE " + trueLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if((left.expressionIntValue != 0 && right.expressionIntValue != 0)||(left.expressionIntValue ==0 && right.expressionIntValue ==0)){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
		}else if(op.opType == Token.OR){
			System.out.println("\tCMPL $0, %eax");
			System.out.println("\tJNE " + trueLable);
			System.out.println("\tCMPL $0, %ebx");
			System.out.println("\tJNE " + trueLable);
			System.out.println("\tJE " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue != 0 || right.expressionIntValue !=0){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
		}else if(op.opType == Token.EQUAL){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJE " + trueLable);
			System.out.println("\tJMP " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue == right.expressionIntValue){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
			
		}else if(op.opType == Token.GREATEREQUAL){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJGE " + trueLable);
			System.out.println("\tJMP " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue >= right.expressionIntValue){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
		}else if(op.opType == Token.GREATERTHAN){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJG " + trueLable);
			System.out.println("\tJMP " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue > right.expressionIntValue){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
		}else if(op.opType == Token.LESSEQUAL){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJLE " + trueLable);
			System.out.println("\tJMP " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue <= right.expressionIntValue){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
		}else if(op.opType == Token.LESSTHAN){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJL " + trueLable);
			System.out.println("\tJMP " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue < right.expressionIntValue){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
		}else if (op.opType == Token.NOTEQUAL){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJNE " + trueLable);
			System.out.println("\tJMP " + falseLable);
			System.out.println(falseLable + ":");
			System.out.println("\tMOVL $0, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(trueLable + ":");
			System.out.println("\tMOVL $1, " + tempExpr.expressionName);
			System.out.println("\tJMP " + cont);
			System.out.println(cont + ":");
			if(left.expressionIntValue != right.expressionIntValue){
				tempExpr.expressionIntValue = 1;
			}else{
				tempExpr.expressionIntValue = 0;
			}
			return tempExpr;
			
		}
		System.out.println("\tMOVL " + "%eax, " + tempExpr.expressionName);
		return tempExpr;
	}

	void generateWrite(Expression expr) {
		switch (expr.expressionType) {
		case Expression.IDEXPR:
		case Expression.TEMPEXPR: {
			generateAssemblyCodeForWriting(expr.expressionName);
			break;
		}
		case Expression.LITERALEXPR: {
			generateAssemblyCodeForWriting("$" + expr.expressionName);
		}
		}
	}
	private void generateAssemblyCodeForWriting(String idName) {
		if (!firstWrite) {
			
			System.out.println("\tmovl " + idName + ",%eax");
			System.out.println("\tpushl %eax");
			System.out.println("\tcall __reversePrint    /* The return address is at top of stack! */");
			System.out.println("\tpopl  %eax    /* Remove value pushed onto the stack */");
			
		}else if(strVariables.checkSTforItem(idName)){
			System.out.println("\tMOV $4, %eax");
			System.out.println("\tMOV $1, %ebx");
			System.out.println("\tMOV $" + idName + ", %ecx");
			System.out.println("\tMOV $" + idName + "Len, %edx");
			System.out.println("\tint $0x80");
		}else
		// String reverseLoopLabel = generateLabel("reverseLoop");
		{
			firstWrite = false;
			
			System.out.println("\tmovl " + idName + ",%eax");
			//---------MARK FOR CORRECTION CODE ---------------
			String nonzeroPrintLabel = generateLabel("__nonzeroPrint");
			System.out.println("\tcmpl $0, %eax");
			System.out.println("jne " + nonzeroPrintLabel);
			System.out.println("\tpush $'0'");
			System.out.println("\tmovl $4, %eax /* The system call for write (sys_write) */");
			System.out.println("\tmovl $1, %ebx /* File descriptor 1 - standard output */");
			System.out.println("\tmovl $1, %edx /* Place number of characters to display */");
			System.out.println("\tleal (%esp), %ecx /* Put effective address of zero into ecx */");
			System.out.println("\tint $0x80 /* Call to the Linux OS */");
			System.out.println("popl %eax");
			System.out.println("\tjmp __writeExit /* Needed to jump over the reversePrint code since we printed the zero */ ");
			System.out.println(nonzeroPrintLabel + ":");
			//---------END MARK -------------------------------
			System.out.println("\tpushl %eax");
			System.out.println("\tcall __reversePrint    /* The return address is at top of stack! */");
			System.out.println("\tpopl  %eax    /* Remove value pushed onto the stack */");
			System.out.println("\tjmp __writeExit");  /* Needed to jump over the reversePrint code since it was called */

			System.out.println("__reversePrint: ");
			System.out.println("\t/* Save registers this method modifies */");
			System.out.println("\tpushl %eax");
			System.out.println("\tpushl %edx");
			System.out.println("\tpushl %ecx");
			System.out.println("\tpushl %ebx");

			System.out.println("\tcmpw $0, 20(%esp)");
			System.out.println("\tjge __positive");
			System.out.println("\t/* Display minus on console */");
			System.out.println("\tmovl $4, %eax       /* The system call for write (sys_write) */");
			System.out.println("\tmovl $1, %ebx       /* File descriptor 1 - standard output */");
			System.out.println("\tmovl $1, %edx     /* Place number of characters to display */");
			System.out.println("\tmovl $__minus, %ecx   /* Put effective address of stack into ecx */");
			System.out.println("\tint $0x80	    /* Call to the Linux OS */");
			
			System.out.println("\t__positive:");
			System.out.println("\txorl %eax, %eax       /* eax = 0 */");
			System.out.println("\txorl %ecx, %ecx       /* ecx = 0, to track characters printed */");

			System.out.println("\t/** Skip 16-bytes of register data stored on stack and 4 bytes");
			System.out.println("\tof return address to get to first parameter on stack ");
			System.out.println("\t*/   ");
			System.out.println("\tmovw 20(%esp), %ax     /* ax = parameter on stack */");

			System.out.println("\tcmpw $0, %ax");
			System.out.println("\tjge __reverseLoop");
			System.out.println("\tmulw __negOne\n");
			
			System.out.println("__reverseLoop:");

			System.out.println("\tcmpw $0, %ax");
			System.out.println("\tje   __reverseExit");
			System.out.println("\t/* Do div and mod operations */");
			System.out.println("\tmovl $10, %ebx         /* ebx = 10 as divisor  */");
			System.out.println("\txorl %edx, %edx        /* edx = 0 to get remainder */");
			System.out.println("\tidivl %ebx             /* edx = eax % 10, eax /= 10 */");
			System.out.println("\taddb $'0', %dl         /* convert 0..9 to '0'..'9'  */");

			System.out.println("\tdecl %esp              /* use stack to store digit  */");
			System.out.println("\tmovb %dl, (%esp)       /* Save character on stack.  */");
			System.out.println("\tincl %ecx              /* track number of digits.   */");

			System.out.println("\tjmp __reverseLoop");

			System.out.println("__reverseExit:");

			System.out.println("__printReverse:");

			System.out.println("\t/* Display characters on _stack_ on console */");

			System.out.println("\tmovl $4, %eax       /* The system call for write (sys_write) */");
			System.out.println("\tmovl $1, %ebx       /* File descriptor 1 - standard output */");
			System.out.println("\tmovl %ecx, %edx     /* Place number of characters to display */");
			System.out.println("\tleal (%esp), %ecx   /* Put effective address of stack into ecx */");
			System.out.println("\tint $0x80	    /* Call to the Linux OS */");

			System.out.println("\t /* Clean up data and registers on the stack */");
			System.out.println("\taddl %edx, %esp");
			System.out.println("\tpopl %ebx");
			System.out.println("\tpopl %ecx");
			System.out.println("\tpopl %edx");
			System.out.println("\t popl %eax");

			System.out.println("\tret");
			System.out.println("__writeExit:");
		}
	}

	void generateRead(Expression expr) {
		switch (expr.expressionType) {
		case Expression.IDEXPR:
		case Expression.TEMPEXPR: {
			generateAssemblyCodeForReading(expr.expressionName);
			break;
		}
		case Expression.LITERALEXPR: {
			// not possible since you cannot read into a literal. An error
			// should be generated
		}
		}
	}

	private void generateAssemblyCodeForReading(String idName) {
		
		String readLoopLabel = generateLabel("__readLoop");
		String readLoopEndLabel = generateLabel("__readLoopEnd");
		String readEndLabel = generateLabel("__readEnd");
		String readPositiveLabel = generateLabel("__readPositive");
		
		System.out.println("\tmovl $0, " + idName);
		
		System.out.println("\tmovl %esp, %ebp");
		System.out.println("\t/* read first character to check for negative */");
		System.out.println("\tmovl $3, %eax        /* The system call for read (sys_read) */");
		System.out.println("\tmovl $0, %ebx        /* File descriptor 0 - standard input */");
		System.out.println("\tlea 4(%ebp), %ecx      /* Put the address of character in a buffer */");
		System.out.println("\tmovl $1, %edx        /* Place number of characters to read in edx */");
		System.out.println("\tint $0x80	     /* Call to the Linux OS */ ");
		System.out.println("\tmovb 4(%ebp), %al");
		System.out.println("\tcmpb $'\\n', %al      /* Is the newline character? */");
		System.out.println("\tje  " + readEndLabel);
		System.out.println("\tcmpb $'-', %al		/* Is the character '-'? */");
		System.out.println("\tjne " + readPositiveLabel);
		
		System.out.println("\tmovb $'-', __negFlag	");
		System.out.println("\tjmp " + readLoopLabel);
		
		
		System.out.println(readPositiveLabel + ":");
		System.out.println("\tcmpb $'+', %al");
		System.out.println("\tje " + readLoopLabel);
		System.out.println("\t/*Process the first digit that is not a minnus or newline.*/");
		System.out.println("\tsubb $'0', 4(%ebp)      /* Convert '0'..'9' to 0..9 */ \n");

		System.out.println("\t/* result  = (result * 10) + (idName  - '0') */");
		System.out.println("\tmovl $10, %eax");
		System.out.println("\txorl %edx, %edx");
		System.out.println("\tmull " + idName + "        /* result  *= 10 */");
		System.out.println("\txorl %ebx, %ebx    /* ebx = (int) idName */");
		System.out.println("\tmovb 4(%ebp), %bl");
		System.out.println("\taddl %ebx, %eax    /* eax += idName */");
		System.out.println("\tmovl %eax, " + idName);
		
		
		System.out.println(readLoopLabel + ":");
		System.out.println("\tmovl $3, %eax        /* The system call for read (sys_read) */");
		System.out.println("\tmovl $0, %ebx        /* File descriptor 0 - standard input */");
		System.out.println("\tlea 4(%ebp), %ecx      /* Put the address of character in a buffer */");
		System.out.println("\tmovl $1, %edx        /* Place number of characters to read in edx */");
		System.out.println("\tint $0x80	     /* Call to the Linux OS */ \n");

		System.out.println("\tmovb 4(%ebp), %al");
		System.out.println("\tcmpb $'\\n', %al      /* Is the character '\\n'? */");

		
		System.out.println("\tje  " + readLoopEndLabel);
		System.out.println("\tsubb $'0', 4(%ebp)      /* Convert '0'..'9' to 0..9 */ \n");

		System.out.println("\t/* result  = (result * 10) + (idName  - '0') */");
		System.out.println("\tmovl $10, %eax");
		System.out.println("\txorl %edx, %edx");
		System.out.println("\tmull " + idName + "        /* result  *= 10 */");
		System.out.println("\txorl %ebx, %ebx    /* ebx = (int) idName */");
		System.out.println("\tmovb 4(%ebp), %bl");
		System.out.println("\taddl %ebx, %eax    /* eax += idName */");
		System.out.println("\tmovl %eax, " + idName);
		System.out.println("\t/* Read the next character */");
		System.out.println("\tjmp " + readLoopLabel);
		System.out.println(readLoopEndLabel + ":\n");
		System.out.println("\tcmpb $'-', __negFlag");
		System.out.println("\tjne " + readEndLabel);
		System.out.println("\tmovl " + readLoopEndLabel + ", %eax");
		System.out.println("\tmull __negOne");
		System.out.println("\tmovl %eax, a");
		System.out.println("\tmovb $'+', __negFlag");
		System.out.println(readEndLabel + ":\n");

	}

	private String generateLabel(String start) {
		String label = start + labelCount++;
		return label;

	}


	void generateAssignment(Expression lValue, Expression expr) {
		if (expr.expressionType == Expression.LITERALEXPR) {
			System.out.println("\tMOVL " + "$" + expr.expressionIntValue + ", %eax");
			System.out.println("\tMOVL %eax, " + lValue.expressionName);
		} else {
			System.out.println("\tMOVL " + expr.expressionName + ", %eax");
			System.out.println("\tMOVL %eax, " + lValue.expressionName);
		}
	}
	
	void generateStrAssignment(StrExpression lValue, StrExpression expr){
		if (expr.expressionType == StrExpression.STRLITERALEXPR) {
			if(strVariables.checkSTforItem(lValue.expressionName)) {
				strVariables.getValue(lValue.expressionName).setValue(expr.expressionStrValue);
			}
			else
			{
				strVariables.addItem(lValue.expressionName, expr.expressionStrValue);
			}
		}
		else {
			if(strVariables.checkSTforItem(lValue.expressionName)) {
				strVariables.getValue(lValue.expressionName).setValue(strVariables.getValue(expr.expressionStrValue).getStringValue());
			}
			else {
				System.out.println("Syntax Error! Variable on right side not previously declared!");
			}
		}
	}
	
	StrExpression generateStrConcat(StrExpression left, StrExpression right){
		StrExpression tempExpr = new StrExpression(StrExpression.STRTEMPEXPR, createTempName(), left.expressionStrValue + right.expressionStrValue);
		System.out.println("\tMOVL $0, %eax");
		System.out.println("\tMOVL $0, %ebx");
		System.out.println("LOOP1: ");
		System.out.println("\tCMPL $0, " + left.expressionName + "(%eax)");
		System.out.println("\tJE DONE1");
		System.out.println("\tMOVB " + left.expressionName + "(%eax), %cl");
		System.out.println("\tMOVB %cl, " + tempExpr.expressionName + "(%ebx)");
		System.out.println("\tINCL %ebx");
		System.out.println("\tINCL %eax");
		System.out.println("\tJMP LOOP1");
		System.out.println("DONE1: ");
		System.out.println("\tMOVL $0, %eax");
		System.out.println("\tMOVL $0, %ebx");
		System.out.println("LOOP2: ");
		System.out.println("\tCMPL $0, " + right.expressionName + "(%eax)");
		System.out.println("\tJE DONE2");
		System.out.println("\tMOVB " + right.expressionName + "(%eax), %cl");
		System.out.println("\tMOVB %cl, " + tempExpr.expressionName + "(%ebx)");
		System.out.println("\tINCL %ebx");
		System.out.println("\tINCL %eax");
		System.out.println("\tJMP LOOP2");
		System.out.println("DONE2: ");
		return tempExpr;
			
	}

	void generateStart() {
		System.out.println(".text\n.global _start\n\n_start:\n");
	}

	void generateExit() {
		System.out.println("exit:");
		System.out.println("\tmov $1, %eax");
		System.out.println("\tmov $1, %ebx");
		System.out.println("\tint $0x80");
	}

	public void generateData() {
		System.out.println("\n\n.data");
		for (String var : variablesList)
			System.out.println(var + ":\t.int 0");
		System.out.println("__minus:  .byte '-'");
		System.out.println("__negOne: .int -1");
		System.out.println("__negFlag: .byte '+'");
		generateStrData();
	}
	
	public void generateStrData(){
		for(Map.Entry<String, Value> entry : strVariables.getSymTable().entrySet())
		{
			String value = entry.getValue().getStringValue();
			String name = entry.getKey();
			System.out.println(name + ":\t.string " + "\"" + value + "\"");
			System.out.println(".equ " + name + "Len" + ", . - " + name);
		}
	}

	private String createTempName() {
		String tempVar = new String("__temp" + tempCount++);
		variablesList.add(tempVar);
		return tempVar;
	}

	public String[] generateWhile(Expression left, Expression right, Operation op) {
		String cont = generateLabel("__cont");
		String loop = generateLabel("__loop");
		System.out.println(loop + ":");
		if (right.expressionType == Expression.LITERALEXPR) {
			System.out.println("\tMOVL " + "$" + right.expressionName + ", %ebx");
		} else {
			System.out.println("\tMOVL " + right.expressionName + ", %ebx");
		}
		if (left.expressionType == Expression.LITERALEXPR) {
			System.out.println("\tMOVL " + "$" + left.expressionName + ", %eax");
		} else {
			System.out.println("\tMOVL " + left.expressionName + ", %eax");
		}
		if(op.opType == Token.EQUAL){
		System.out.println("\tCMPL %ebx, %eax");
		System.out.println("\tJNE " + cont);
		
		}else if(op.opType == Token.GREATEREQUAL){
		System.out.println("\tCMPL %ebx, %eax");
		System.out.println("\tJL " + cont);

		
		}else if(op.opType == Token.GREATERTHAN){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJLE " + cont);
		
		}else if(op.opType == Token.LESSEQUAL){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJG " + cont);

		
		}else if(op.opType == Token.LESSTHAN){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJGE " + cont);
		
		}else if (op.opType == Token.NOTEQUAL){
			System.out.println("\tCMPL %ebx, %eax");
			System.out.println("\tJE " + cont);

		
		}
		String[] labels = new String[2];
		labels[0] = loop;
		labels[1] = cont;
		return labels;
	}

	public void generateEndWhile(String loop, String cont) {
		System.out.println("\tJMP " + loop);
		System.out.println(cont + ":");
		
	}

	public String generateFuncLabel(String funcname) {
		String contLabel = generateLabel("__cont");
		System.out.println("\tJMP " + contLabel);
		System.out.println(funcname + ":");
		System.out.println("\tPUSHL %eax");
		System.out.println("\tPUSHL %ebx");
		System.out.println("\tPUSHL %ecx");
		System.out.println("\tPUSHL %edx");
		return contLabel;
	}

	public void generateFuncReturn(String contLabel) {
		System.out.println("\tPOPL %edx");
		System.out.println("\tPOPL %ecx");
		System.out.println("\tPOPL %ebx");
		System.out.println("\tPOPL %eax");
		System.out.println("\tRET");
		System.out.println(contLabel + ":");
		
	}
	public void generateCall(String funcname){
		System.out.println("\tCALL " + funcname);
	}

}
