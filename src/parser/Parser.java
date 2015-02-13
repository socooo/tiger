package parser;

import java.util.LinkedList;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser {
	Lexer lexer;
	Token current;
	String place = null;
	Token preToken = new Token();
	String preId;
	boolean isPreTokenUsed = true;

	public Parser(String fname, java.io.InputStream fstream) {
		lexer = new Lexer(fname, fstream);
		current = lexer.nextToken();
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser.

	private void advance() {
		current = lexer.nextToken();
	}

	private void eatToken(Kind kind) {
		if (kind == current.kind)
			advance();
		else {
			System.out.println("In parser\nExpects: " + kind.toString()
					+ " line num" + current.lineNum);
			System.out.println("But got: " + current.kind.toString());
			System.exit(1);
		}
	}

	private void error() {
		System.out
				.println("Syntax error: compilation aborting...\nline number: "
						+ current.lineNum + "\nplace: " + place
						+ "\ncurrent kind: " + current.kind.toString());
		place = null;
		System.exit(1);
		return;
	}

	// ////////////////////////////////////////////////////////////
	// below are method for parsing.

	// A bunch of parsing methods to parse expressions. The messy
	// parts are to deal with precedence and associativity.

	// ExpList -> Exp ExpRest*
	// ->
	// ExpRest -> , Exp
	private LinkedList<ast.Ast.Exp.T> parseExpList() {
		LinkedList<ast.Ast.Exp.T> args = new LinkedList<ast.Ast.Exp.T>();

		if (current.kind == Kind.TOKEN_RPAREN)
			return args;
		args.add(parseExp());
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			args.add(parseExp());
		}
		return args;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int [exp]
	// -> new id ()
	private ast.Ast.Exp.T parseAtomExp() {
		int lineNum;
		switch (current.kind) {
		case TOKEN_LPAREN:
			advance();
			ast.Ast.Exp.T parenExp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			return parenExp;
		case TOKEN_NUM:
			int num = Integer.parseInt(current.lexeme);
			lineNum = current.lineNum;
			advance();
			return new ast.Ast.Exp.Num(num,lineNum);
		case TOKEN_FALSE:
			lineNum = current.lineNum;
			advance();
			return new ast.Ast.Exp.False(lineNum);
		case TOKEN_TRUE:
			lineNum = current.lineNum;
			advance();
			return new ast.Ast.Exp.True(lineNum);
		case TOKEN_THIS:
			lineNum = current.lineNum;
			advance();
			return new ast.Ast.Exp.This(lineNum);
		case TOKEN_ID:
			String id = current.lexeme;
			lineNum = current.lineNum;
			advance();
			return new ast.Ast.Exp.Id(id,lineNum);
		case TOKEN_NEW: {
			ast.Ast.Exp.T newExp;
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				newExp = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				lineNum = current.lineNum;
				return new ast.Ast.Exp.NewIntArray(newExp,lineNum);
			case TOKEN_ID:
				String newID = current.lexeme;
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				lineNum = current.lineNum;
				return new ast.Ast.Exp.NewObject(newID,lineNum);
			default:
				place = "atomExp token new";
				error();
				return null;
			}
		}
		default:
			place = "atomExp";
			error();
			return null;
		}
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList)
	// -> AtomExp [exp]
	// -> AtomExp .length
	private ast.Ast.Exp.T parseNotExp() {
		int lineNum;
		String callId = null;
		ast.Ast.Exp.T atom = parseAtomExp();
		while (current.kind == Kind.TOKEN_DOT
				|| current.kind == Kind.TOKEN_LBRACK) {
			if (current.kind == Kind.TOKEN_DOT) {
				advance();
				if (current.kind == Kind.TOKEN_LENGTH) {
					lineNum = current.lineNum;
					advance();
					return new ast.Ast.Exp.Length(atom);
				}
				callId = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				LinkedList<ast.Ast.Exp.T> args = parseExpList();
				lineNum = current.lineNum;
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.Ast.Exp.Call(atom, callId, args);
			} else {
				advance();
				ast.Ast.Exp.T index = parseExp();
				lineNum = current.lineNum;
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.Ast.Exp.ArraySelect(atom, index,lineNum);
			}
		}
		return atom;
	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private ast.Ast.Exp.T parseTimesExp() {
		if (current.kind == Kind.TOKEN_NOT) {
			while (current.kind == Kind.TOKEN_NOT) {
				advance();
			}
			ast.Ast.Exp.T exp = parseNotExp();
			return new ast.Ast.Exp.Not(exp);
		} else {
			ast.Ast.Exp.T exp = parseNotExp();
			return exp;
		}
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.Ast.Exp.T parseAddSubExp() {
		ast.Ast.Exp.T left = parseTimesExp();
		if (current.kind == Kind.TOKEN_TIMES) {
			advance();
			return new ast.Ast.Exp.Times(left, parseTimesExp());
		}
		return left;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.Ast.Exp.T parseLtExp() {
		ast.Ast.Exp.T left = parseAddSubExp();
		if (current.kind == Kind.TOKEN_ADD) {
			advance();
			return new ast.Ast.Exp.Add(left, parseAddSubExp());
		}else if(current.kind == Kind.TOKEN_SUB){
			advance();
			return new ast.Ast.Exp.Sub(left, parseAddSubExp());
		}
		return left;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.Ast.Exp.T parseAndExp() {
		ast.Ast.Exp.T left = parseLtExp();
		if (current.kind == Kind.TOKEN_LT) {
			advance();
			return new ast.Ast.Exp.Lt(left, parseLtExp());
		}
		return left;
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.Ast.Exp.T parseExp() {
		ast.Ast.Exp.T left = parseAndExp();
		if (current.kind == Kind.TOKEN_AND) {
			advance();
			return new ast.Ast.Exp.And(left, parseAndExp());
		}
		return left;
	}

	// Statement -> { Statement* }
	// -> if ( Exp ) Statement else Statement
	// -> while ( Exp ) Statement
	// -> System.out.println ( Exp ) ;
	// -> id = Exp ;
	// -> id [ Exp ]= Exp ;
	private ast.Ast.Stm.T parseStatement() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.
		if (isPreTokenUsed == false) {
			isPreTokenUsed = true;
			eatToken(Kind.TOKEN_ASSIGN);
			ast.Ast.Exp.T assignStmExp = parseExp();
			eatToken(Kind.TOKEN_SEMI);
			return new ast.Ast.Stm.Assign(this.preId, assignStmExp);
		} else {
			switch (current.kind) {
			case TOKEN_LBRACE:
				advance();
				LinkedList<ast.Ast.Stm.T> childStmList = parseStatements();
				eatToken(Kind.TOKEN_RBRACE);
				return new ast.Ast.Stm.Block(childStmList);
			case TOKEN_IF:
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				ast.Ast.Exp.T ifCondition = parseExp();
				eatToken(Kind.TOKEN_RPAREN);
				ast.Ast.Stm.T thenn = parseStatement();
				eatToken(Kind.TOKEN_ELSE);
				ast.Ast.Stm.T elsee = parseStatement();
				return new ast.Ast.Stm.If(ifCondition, thenn, elsee);
			case TOKEN_WHILE:
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				ast.Ast.Exp.T whileCondition = parseExp();
				eatToken(Kind.TOKEN_RPAREN);
				ast.Ast.Stm.T body = parseStatement();
				return new ast.Ast.Stm.While(whileCondition, body);
			case TOKEN_SYSTEM:
				advance();
				eatToken(Kind.TOKEN_DOT);
				eatToken(Kind.TOKEN_OUT);
				eatToken(Kind.TOKEN_DOT);
				eatToken(Kind.TOKEN_PRINTLN);
				eatToken(Kind.TOKEN_LPAREN);
				ast.Ast.Exp.T printExp = parseExp();
				eatToken(Kind.TOKEN_RPAREN);
				eatToken(Kind.TOKEN_SEMI);
				return new ast.Ast.Stm.Print(printExp);
				// -> id = Exp ;
				// -> id [ Exp ]= Exp ;
			case TOKEN_ID:
				String assID = current.lexeme;
				advance();
				if (current.kind == Kind.TOKEN_ASSIGN) {
					eatToken(Kind.TOKEN_ASSIGN);
					ast.Ast.Exp.T assExp = parseExp();
					eatToken(Kind.TOKEN_SEMI);
					return new ast.Ast.Stm.Assign(assID, assExp);
				} else if (current.kind == Kind.TOKEN_LBRACK) {
					advance();
					ast.Ast.Exp.T assIndex = parseExp();
					eatToken(Kind.TOKEN_RBRACK);
					eatToken(Kind.TOKEN_ASSIGN);
					ast.Ast.Exp.T assArrExp = parseExp();
					eatToken(Kind.TOKEN_SEMI);
					return new ast.Ast.Stm.AssignArray(assID, assIndex,
							assArrExp);
				} else {
					place = "statement";
					error();
					return null;
				}
			default:
				place = "atomExp";
				error();
				return null;
			}
		}

	}

	// Statements -> Statement Statements
	// ->
	private LinkedList<ast.Ast.Stm.T> parseStatements() {
		LinkedList<ast.Ast.Stm.T> stmList = new LinkedList<ast.Ast.Stm.T>();
		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID
				|| (preToken.kind == Kind.TOKEN_ID && isPreTokenUsed == false)) {
			stmList.add(parseStatement());
		}
		return stmList;
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private ast.Ast.Type.T parseType() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
		int i;//line number;
		
		if(isPreTokenUsed == false){
				String typeID = preToken.lexeme;
				i = preToken.lineNum;
				isPreTokenUsed = true;
				return new ast.Ast.Type.ClassType(typeID,i);
		}
		else{
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				i = current.lineNum;
				if (current.kind == Kind.TOKEN_LBRACK) {
					advance();
					
					eatToken(Kind.TOKEN_RBRACK);
					return new ast.Ast.Type.IntArray(i);
				}
				return new ast.Ast.Type.Int(i);
			case TOKEN_BOOLEAN:
				i = current.lineNum;
				advance();
				return new ast.Ast.Type.Boolean(i);
			case TOKEN_ID:
				String typeID = current.lexeme;
				i = current.lineNum;
				advance();
				return new ast.Ast.Type.ClassType(typeID,i);
			default:
				error();
				return null;
			}
		}
		
	}

	// VarDecl -> Type id ;
	private ast.Ast.Dec.DecSingle parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		ast.Ast.Type.T type = parseType();
		String varDecID = current.lexeme;
		int i = current.lineNum;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_SEMI);
		return new ast.Ast.Dec.DecSingle(type, varDecID,i);
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private LinkedList<ast.Ast.Dec.T> parseVarDecls() {
		LinkedList<ast.Ast.Dec.T> declList = new LinkedList<ast.Ast.Dec.T>();
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			if (current.kind == Kind.TOKEN_ID) {
				preToken = current;
				preId = current.lexeme;
				isPreTokenUsed = false;
				advance();
				if (current.kind == Kind.TOKEN_ASSIGN) {
					return declList;
				}
			}
			ast.Ast.Dec.DecSingle single = parseVarDecl();
			declList.add(single);
		}
		return declList;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private LinkedList<ast.Ast.Dec.T> parseFormalList() {
		LinkedList<ast.Ast.Dec.T> formalList = new LinkedList<ast.Ast.Dec.T>();
		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			formalList.add(new ast.Ast.Dec.DecSingle(parseType(),
					current.lexeme,current.lineNum));
			eatToken(Kind.TOKEN_ID);
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				formalList.add(new ast.Ast.Dec.DecSingle(parseType(),
						current.lexeme,current.lineNum));
				eatToken(Kind.TOKEN_ID);
			}
		}
		return formalList;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.Ast.Method.T parseMethod() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		eatToken(Kind.TOKEN_PUBLIC);
		ast.Ast.Type.T retType = parseType();
		String methodId = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN);
		LinkedList<ast.Ast.Dec.T> formals = parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		LinkedList<ast.Ast.Dec.T> locals = parseVarDecls();
		LinkedList<ast.Ast.Stm.T> stms = parseStatements();
		eatToken(Kind.TOKEN_RETURN);
		ast.Ast.Exp.T retExp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.Ast.Method.MethodSingle(retType, methodId, formals,
				locals, stms, retExp);
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private LinkedList<ast.Ast.Method.T> parseMethodDecls() {
		LinkedList<ast.Ast.Method.T> methodList = new LinkedList<ast.Ast.Method.T>();
		while (current.kind == Kind.TOKEN_PUBLIC) {
			methodList.add(parseMethod());
		}
		return methodList;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.Ast.Class.T parseClassDecl() {
		String extendss = null;
		eatToken(Kind.TOKEN_CLASS);
		String classID = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		if (current.kind == Kind.TOKEN_EXTENDS) {
			eatToken(Kind.TOKEN_EXTENDS);
			extendss = current.lexeme;
			eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);
		LinkedList<ast.Ast.Dec.T> decs = parseVarDecls();
		LinkedList<ast.Ast.Method.T> methods = parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.Ast.Class.ClassSingle(classID, extendss, decs, methods);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private LinkedList<ast.Ast.Class.T> parseClassDecls() {
		LinkedList<ast.Ast.Class.T> classList = new LinkedList<ast.Ast.Class.T>();
		while (current.kind == Kind.TOKEN_CLASS) {
			classList.add(parseClassDecl());
		}
		return classList;
	}

	// MainClass -> class id
	// {
	// public static void main ( String [] id )
	// {
	// Statement
	// }
	// }
	private ast.Ast.MainClass.T parseMainClass() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.
		String mainId = null, arg = null;
		eatToken(Kind.TOKEN_CLASS);
		if (current.kind == Kind.TOKEN_ID) {
			mainId = current.lexeme;
		}
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LBRACE);
		eatToken(Kind.TOKEN_PUBLIC);
		eatToken(Kind.TOKEN_STATIC);
		eatToken(Kind.TOKEN_VOID);
		eatToken(Kind.TOKEN_MAIN);
		eatToken(Kind.TOKEN_LPAREN);
		eatToken(Kind.TOKEN_STRING);
		eatToken(Kind.TOKEN_LBRACK);
		eatToken(Kind.TOKEN_RBRACK);
		if (current.kind == Kind.TOKEN_ID)
			arg = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		ast.Ast.Stm.T mainStm = parseStatement();
		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.Ast.MainClass.MainClassSingle(mainId, arg, mainStm);
	}

	public ast.Ast.Program.T parse() {
		return new ast.Ast.Program.ProgramSingle(parseMainClass(),
				parseClassDecls());
	}

	// // Program -> MainClass ClassDecl*
	// private void parseProgram() {
	// parseMainClass();
	// // while(current.kind == Kind.TOKEN_ANNOTATION){
	// // eatToken(Kind.TOKEN_ANNOTATION);
	// // }
	// parseClassDecls();
	// eatToken(Kind.TOKEN_EOF);
	// return;
	// }

}
