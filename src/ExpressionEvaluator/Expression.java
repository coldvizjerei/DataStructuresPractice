package ExpressionEvaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                

	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   

	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;

	/**
	 * Positions of opening brackets
	 */
	ArrayList<Integer> openingBracketIndex; 

	/**
	 * Positions of closing brackets
	 */
	ArrayList<Integer> closingBracketIndex; 

	/**
	 * String containing all delimiters (characters other than variables and constants), 
	 * to be used with StringTokenizer
	 */
	public static final String delims = " \t*+-/()[]";
	private int bracketCounter = 0;

	/**
	 * Initializes this Expression object with an input expression. Sets all other
	 * fields to null.
	 * 
	 * @param expr Expression
	 */
	public Expression(String expr) {
		this.expr = expr;
		scalars = null;
		arrays = null;
		openingBracketIndex = null;
		closingBracketIndex = null;
	}

	/**
	 * Matches parentheses and square brackets. Populates the openingBracketIndex and
	 * closingBracketIndex array lists in such a way that closingBracketIndex[i] is
	 * the position of the bracket in the expression that closes an opening bracket
	 * at position openingBracketIndex[i]. For example, if the expression is:
	 * <pre>
	 *    (a+(b-c))*(d+A[4])
	 * </pre>
	 * then the method would return true, and the array lists would be set to:
	 * <pre>
	 *    openingBracketIndex: [0 3 10 14]
	 *    closingBracketIndex: [8 7 17 16]
	 * </pe>
	 * 
	 * @return True if brackets are matched correctly, false if not
	 */
	public boolean isLegallyMatched(){
		int size = expr.length();
		int numb = 0;
		Stack<Character> stack = new Stack<Character>();
		Stack<Integer> values = new Stack<Integer>();
		openingBracketIndex = new ArrayList();
		closingBracketIndex = new ArrayList();

		for (int k = 0; k < size; k++){
			if(expr.charAt(k) == ')' || expr.charAt(k) == ']'){
				numb++;
			}
		}
		for (int z = 0; z < numb; z++){
			closingBracketIndex.add(0);
		}

		int counter = 0;
		for (int i = 0; i < size; i++){
			if (expr.charAt(i) == '(' || expr.charAt(i) == '['){
				stack.push(expr.charAt(i));
				openingBracketIndex.add(i);
				values.push(counter);
				counter++;

			}
			else if (expr.charAt(i) == ')' || expr.charAt(i) == ']'){
				if (stack.isEmpty() == true){
					return false;
				}
				else {
					char ch = stack.pop();
					if (expr.charAt(i) == ')' && ch != '(' || expr.charAt(i) == ']' && ch != '['){
						return false;
					}
					closingBracketIndex.set(values.pop(), i);
				}
			}
		}
		if (stack.isEmpty() == false){
			return false;
		}
		//    	System.out.print(openingBracketIndex);
		//    	System.out.print(closingBracketIndex);
		return true;
	}

	/**
	 * Populates the scalars and arrays lists with symbols for scalar and array
	 * variables in the expression. For every variable, a SINGLE symbol is created and stored,
	 * even if it appears more than once in the expression.
	 * At this time, values for all variables are set to
	 * zero - they will be loaded from a file in the loadSymbolValues method.
	 */
	public void buildSymbols(){
		arrays = new ArrayList();
		scalars = new ArrayList();
		Stack<String> symbols = new Stack<String>();

		StringTokenizer st = new StringTokenizer(expr, delims, true);
		//    	while (st.hasMoreTokens()){
		//    		System.out.println(st.nextToken());
		//    	}

		String token = "";
		while (st.hasMoreTokens()){
			token = st.nextToken();
			if ((token.charAt(0) >= 'a' && token.charAt(0) <= 'z') || (token.charAt(0) >= 'A' && token.charAt(0) <= 'Z' || token.equals("["))){
				symbols.push(token);
			}
		}
		//    	while (symbols.isEmpty() != true){
		//    		System.out.print(symbols.pop());
		//    	}

		while(!symbols.isEmpty()){
			token = symbols.pop();
			if (token.equals("[")){
				token = symbols.pop();
				ArraySymbol aSymbol = new ArraySymbol(token);
				//checking if it is already in the arrays ArrayList
				if(arrays.indexOf(aSymbol) == -1){
					arrays.add(aSymbol);
				}
			}
			else {
				ScalarSymbol sSymbol = new ScalarSymbol(token);
				//checking if it is already in the scalar ArrayList
				if (scalars.indexOf(sSymbol) == -1){
					scalars.add(sSymbol);
				}
			}
		}
		//    	System.out.print(scalars);
		//    	System.out.print(arrays);
	}

	/**
	 * Loads values for symbols in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input 
	 */
	public void loadSymbolValues(Scanner sc) throws IOException{
		while (sc.hasNextLine()){
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String sym = st.nextToken();
			ScalarSymbol ssymbol = new ScalarSymbol(sym);
			ArraySymbol asymbol = new ArraySymbol(sym);
			int ssi = scalars.indexOf(ssymbol);
			int asi = arrays.indexOf(asymbol);
			if (ssi == -1 && asi == -1){
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2){ // scalar symbol
				scalars.get(ssi).value = num;
			} else { // array symbol
				asymbol = arrays.get(asi);
				asymbol.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()){
					String tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok," (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					asymbol.values[index] = val;              
				}
			}
		}
	}

	/**
	 * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
	 * subscript expressions.
	 * 
	 * @return Result of evaluation
	 */
	//EVALUATE IS HERE
	public float evaluate(){
		Stack<String> ReversedstTokens = new Stack<String>();
		Stack<String> stTokens = new Stack<String>();
		StringTokenizer st = new StringTokenizer(expr, delims, true);
		String token = "";
		while (st.hasMoreElements()){
			token = st.nextToken();
			if ( token.equals(" " ) || token.equals("\t") )
				continue;

			ReversedstTokens.push(token);
		}
		while (!ReversedstTokens.isEmpty()){
			stTokens.push(ReversedstTokens.pop());
		}
		return evalExprStack(stTokens);
	}

	private void checkEvalMultiDiv(Stack<String> operators, Stack<Integer> values){
		if (!operators.isEmpty()){
			String lastOperator = operators.peek();
			if (lastOperator.equals("*") || lastOperator.equals("/")){
				evalStack(operators, values, true);
			}   				
		}
	}//END EvalMultiDivi 
	private void evalStack(Stack<String> operators, Stack<Integer> values, boolean isInOrder){

		String lastOperator = operators.pop();
		int temp2 = 0;
		int temp1 = 0;
		int result = 0;
		if (isInOrder){
			temp2 = values.pop();
			temp1 = values.pop();
		}
		else{
			temp1 = values.pop();
			temp2 = values.pop();
		}

		if (lastOperator.equals("*")){
			result = temp1 * temp2;
		}
		else if (lastOperator.equals("/")){
			result = temp1 / temp2;
		}
		else if (lastOperator.equals("+")){
			result = temp1 + temp2;
		}
		else if (lastOperator.equals("-")){	
			result = temp1 - temp2;
		}
		values.push(result);
	}//END evalStack

	private int evalExprStack(Stack<String> stTokens){
		Stack<String> operators = new Stack<String>();
		Stack<Integer> values = new Stack<Integer>();

		ScalarSymbol sSymbol = null;
		ArraySymbol aSymbol = null;
		int ssi = -1;
		int asi = -1;
		int constant = 0;
		int numb = 0;
		int temp1 = 0;
		int temp2 = 0;
		int result = 0;
		String token = "";
		while ( !stTokens.isEmpty() ){
			token = stTokens.pop();

			//IF IT IS A PARENTHESES
			if(token.equals("(")){
				token = stTokens.pop();
				Stack<String> ReversedInsideParenStack = new Stack<String>();
				int parenCount = 1;
				while (true){
					if (token.equals("(")){
						parenCount++;
					}
					else if (token.equals(")")){
						parenCount--;
						if (parenCount == 0){
							break;
						}    					
					}
					ReversedInsideParenStack.push(token);
					token = stTokens.pop(); 				
				}
				Stack<String> insideParenStack = new Stack<String>();
				while (!ReversedInsideParenStack.isEmpty()){
					insideParenStack.push(ReversedInsideParenStack.pop());
				}
				result = evalExprStack(insideParenStack);
				values.push(result);
				checkEvalMultiDiv(operators, values);
			}

			//IF IT IS A BRACKET
			else if (token.equals("[")){
				token = stTokens.pop();
				Stack<String> ReversedInsideBracketStack = new Stack<String>();
				int bracketCount = 1;
				while (true){
					if (token.equals("[")){
						bracketCount++;
					}
					else if (token.equals("]")){
						bracketCount--;
						if (bracketCount == 0){
							break;
						}
					}
					ReversedInsideBracketStack.push(token);
					token = stTokens.pop();
				}
				Stack<String> insideBracketStack = new Stack<String>();
				while (!ReversedInsideBracketStack.isEmpty()){
					insideBracketStack.push(ReversedInsideBracketStack.pop());
				}
				result = evalExprStack(insideBracketStack);
				asi = values.pop();
				numb = arrays.get(asi).values[result];

				values.push(numb);
				checkEvalMultiDiv(operators, values);
			}

			//IF IT IS A SCALAR SYMBOL
			else if ((token.charAt(0) >= 'a' && token.charAt(0) <= 'z') || (token.charAt(0) >= 'A' && token.charAt(0) <= 'Z')){

				sSymbol = new ScalarSymbol(token);
				ssi = scalars.indexOf(sSymbol);
				if (ssi != -1){
					numb = scalars.get(ssi).value;
					values.push(numb);
					checkEvalMultiDiv(operators, values);
				}
				else {
					aSymbol = new ArraySymbol(token);
					asi = arrays.indexOf(aSymbol);
					values.push(asi);
				}
			}// END IF SCALAR SYMBOL 

			//ELSE IF IT IS AN OPERATOR
			else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")){
				operators.push(token);
			}

			//ELSE IF IT IS A CONSTANT
			else {
				constant = Integer.parseInt(token);
				values.push(constant);
				checkEvalMultiDiv(operators, values);
			}
		}//END WHILE LOOP

		if (operators.isEmpty()){
			return values.pop();
		}
		Stack<Integer> reverseValues = new Stack<Integer>();
		Stack<String> reverseOperators = new Stack<String>();
		while (!operators.isEmpty()){
			reverseOperators.push(operators.pop());
		}
		while(!values.isEmpty()){
			reverseValues.push(values.pop());
		}
		while (!reverseOperators.isEmpty()){
			evalStack(reverseOperators, reverseValues, false);
		}
		return reverseValues.pop();
	}

	/**
	 * Utility method, prints the symbols in the scalars list
	 */
	public void printScalars(){
		for (ScalarSymbol ss: scalars){
			System.out.println(ss);
		}
	}

	/**
	 * Utility method, prints the symbols in the arrays list
	 */
	public void printArrays(){
		for (ArraySymbol as: arrays){
			System.out.println(as);
		}
	}
}