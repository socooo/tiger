package parser;

import ast.Ast;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;
import slp.Slp;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;

public class Parser {
    Lexer lexer;
    Token current;
    Token lastToken = null;
    boolean isField = false;

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
            System.out.println("Expects: " + kind.toString());
            System.out.println("But got: " + current.kind.toString());
            System.out.println("in line: " + current.lineNum);
            System.exit(1);
        }
    }

    private void error() {
        System.out.println("Syntax error: compilation aborting...\n");
        System.out.println("Error token: " + current.kind);
        System.out.println("in line: " + current.lineNum);
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
    private LinkedList<Ast.Exp.T> parseExpList() {
        LinkedList<Ast.Exp.T> explist = new LinkedList<>();
        Ast.Exp.T exp = null;
        if (current.kind == Kind.TOKEN_RPAREN)
            return null;
        exp = parseExp();
        explist.add(exp);
        while (current.kind == Kind.TOKEN_COMMER) {
            advance();
            exp = parseExp();
            explist.add(exp);
        }
        return explist;
    }

    // AtomExp -> (exp)
    // -> INTEGER_LITERAL
    // -> true
    // -> false
    // -> this
    // -> id
    // -> new int [exp]
    // -> new id ()
    private Ast.Exp.T parseAtomExp() {
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        switch (current.kind) {
            case TOKEN_LPAREN:
                advance();
                Ast.Exp.T exp = parseExp();
                eatToken(Kind.TOKEN_RPAREN);
                return exp;
            case TOKEN_NUM:
                int num = Integer.parseInt(current.lexeme);
                advance();
                return new Ast.Exp.Num(num, current.lineNum);
            case TOKEN_TRUE:
                advance();
                return new Ast.Exp.True(current.lineNum);
            case TOKEN_FALSE:
                advance();
                return new Ast.Exp.False(current.lineNum);
            case TOKEN_THIS:
                advance();
                return new Ast.Exp.This(current.lineNum);
            case TOKEN_ID:
                String idStr = current.lexeme;
                advance();
                return new Ast.Exp.Id(idStr, false, current.lineNum);
            case TOKEN_NEW: {
                advance();
                switch (current.kind) {
                    case TOKEN_INT:
                        advance();
                        eatToken(Kind.TOKEN_LBRACK);
                        Ast.Exp.T index = parseExp();
                        eatToken(Kind.TOKEN_RBRACK);
                        return new Ast.Exp.NewIntArray(index, current.lineNum);
                    case TOKEN_ID:
                        String idNew = current.lexeme;
                        advance();
                        eatToken(Kind.TOKEN_LPAREN);
                        eatToken(Kind.TOKEN_RPAREN);
                        return new Ast.Exp.NewObject(idNew, current.lineNum);
                    default:
                        error();
                        return null;
                }
            }
            default:
                error();
                return null;
        }
    }

    // NotExp -> AtomExp
    // -> AtomExp .id (expList)
    // -> AtomExp [exp]
    // -> AtomExp .length
    private Ast.Exp.T parseNotExp() {
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        Ast.Exp.T exp = parseAtomExp();
        while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {
            if (current.kind == Kind.TOKEN_DOT) {
                advance();
                if (current.kind == Kind.TOKEN_LENGTH) {
                    advance();
                    return new Ast.Exp.Length(exp, current.lineNum);
                }
                String id = current.lexeme;
                eatToken(Kind.TOKEN_ID);
                eatToken(Kind.TOKEN_LPAREN);
                LinkedList<Ast.Exp.T> callExps = parseExpList();
                eatToken(Kind.TOKEN_RPAREN);
                return new Ast.Exp.Call(exp, id, callExps, current.lineNum);
            } else {
                advance();
                Ast.Exp.T index = parseExp();
                eatToken(Kind.TOKEN_RBRACK);
                return new Ast.Exp.ArraySelect(exp, index, current.lineNum);
            }
        }
        return exp;
    }

    // TimesExp -> ! TimesExp
    // -> NotExp
    private Ast.Exp.T parseTimesExp() {
        if (current.kind == Kind.TOKEN_NOT) {
            advance();
            Ast.Exp.T exp = parseNotExp();
            return new Ast.Exp.Not(exp, current.lineNum);
        }
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        Ast.Exp.T exp = parseAtomExp();
        while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {
            if (current.kind == Kind.TOKEN_DOT) {
                advance();
                if (current.kind == Kind.TOKEN_LENGTH) {
                    advance();
                    return new Ast.Exp.Length(exp, current.lineNum);
                }
                String id = current.lexeme;
                eatToken(Kind.TOKEN_ID);
                eatToken(Kind.TOKEN_LPAREN);
                LinkedList<Ast.Exp.T> callExps = parseExpList();
                eatToken(Kind.TOKEN_RPAREN);
                return new Ast.Exp.Call(exp, id, callExps, current.lineNum);
            } else {
                advance();
                Ast.Exp.T index = parseExp();
                eatToken(Kind.TOKEN_RBRACK);
                return new Ast.Exp.ArraySelect(exp, index, current.lineNum);
            }
        }
        return exp;
    }

    // AddSubExp -> TimesExp * TimesExp
    // -> TimesExp
    private Ast.Exp.T parseAddSubExp() {
        Ast.Exp.T left = parseTimesExp();
        Ast.Exp.T right = null;
        if (current.kind == Kind.TOKEN_TIMES) {
            advance();
            right = parseTimesExp();
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
            return new Ast.Exp.Times(left, right, current.lineNum);
        }
        return left;
    }

    // LtExp -> AddSubExp + AddSubExp
    // -> AddSubExp - AddSubExp
    // -> AddSubExp
    private Ast.Exp.T parseLtExp() {
        Ast.Exp.T left = parseAddSubExp();
        Ast.Exp.T right = null;
        if (current.kind == Kind.TOKEN_ADD) {
            advance();
            right = parseAddSubExp();
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
            return new Ast.Exp.Add(left, right, current.lineNum);
        }
        if (current.kind == Kind.TOKEN_SUB) {
            advance();
            right = parseAddSubExp();
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
            return new Ast.Exp.Sub(left, right, current.lineNum);
        }
        return left;
    }

    // AndExp -> LtExp < LtExp
    // -> LtExp
    private Ast.Exp.T parseAndExp() {
        Ast.Exp.T left = parseLtExp();
        Ast.Exp.T right = null;
//        while (current.kind == Kind.TOKEN_LT) {
        if (current.kind == Kind.TOKEN_LT) {
            advance();
            right = parseLtExp();
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
            return new Ast.Exp.Lt(left, right, current.lineNum);
        }
        return left;
    }

    // Exp -> AndExp && AndExp
    // -> AndExp
    private Ast.Exp.T parseExp() {
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        Ast.Exp.T left = parseAndExp();
        Ast.Exp.T right = null;
//        while (current.kind == Kind.TOKEN_AND) {      Can't Handle the condition about A && B && C.
        if (current.kind == Kind.TOKEN_AND) {
            advance();
            right = parseAndExp();
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
            return new Ast.Exp.And(left, right, current.lineNum);
        }
        return left;
    }

    // Statement -> { Statement* }
    // -> if ( Exp ) Statement else Statement
    // -> while ( Exp ) Statement
    // -> System.out.println ( Exp ) ;
    // -> id = Exp ;
    // -> id [ Exp ]= Exp ;
    private Ast.Stm.T parseStatement() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a statement.
        String id = null;
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        if (lastToken != null) {
            Ast.Exp.T exp = null;
            id = lastToken.lexeme;
            lastToken = null;
            if (current.kind == Kind.TOKEN_LBRACK) {
                advance();
                Ast.Exp.T index = parseExp();
                eatToken(Kind.TOKEN_RBRACK);
                eatToken(Kind.TOKEN_ASSIGN);
                exp = parseExp();
                eatToken(Kind.TOKEN_SEMI);
                return new Ast.Stm.AssignArray(new Ast.Exp.Id(id, current.lineNum), index, exp, current.lineNum);
            }
            eatToken(Kind.TOKEN_ASSIGN);
            exp = parseExp();
            eatToken(Kind.TOKEN_SEMI);
            return new Ast.Stm.Assign(new Ast.Exp.Id(id, current.lineNum), exp, current.lineNum);
        }
        switch (current.kind) {
            case TOKEN_LBRACE:
                advance();
                LinkedList<Ast.Stm.T> stms = parseStatements();
                eatToken(Kind.TOKEN_RBRACE);
                return new Ast.Stm.Block(stms, current.lineNum);
            case TOKEN_IF:
                advance();
                eatToken(Kind.TOKEN_LPAREN);
                Ast.Exp.T conditionIf = parseExp();
                eatToken(Kind.TOKEN_RPAREN);
                Ast.Stm.T thenn = parseStatement();
                eatToken(Kind.TOKEN_ELSE);
                Ast.Stm.T elsee = parseStatement();
                return new Ast.Stm.If(conditionIf, thenn, elsee, current.lineNum);
            case TOKEN_WHILE:
                advance();
                eatToken(Kind.TOKEN_LPAREN);
                Ast.Exp.T conditionWhile = parseExp();
                eatToken(Kind.TOKEN_RPAREN);
                Ast.Stm.T bodyWhile = parseStatement();
                return new Ast.Stm.While(conditionWhile, bodyWhile, current.lineNum);
            case TOKEN_SYSTEM:
                advance();
                eatToken(Kind.TOKEN_DOT);
                eatToken(Kind.TOKEN_OUT);
                eatToken(Kind.TOKEN_DOT);
                eatToken(Kind.TOKEN_PRINTLN);
                eatToken(Kind.TOKEN_LPAREN);
                Ast.Exp.T printExp = parseExp();
                eatToken(Kind.TOKEN_RPAREN);
                eatToken(Kind.TOKEN_SEMI);
                return new Ast.Stm.Print(printExp, current.lineNum);
            case TOKEN_ID:
                String idStr = current.lexeme;
                advance();
                Ast.Exp.T exp = null;
                if (current.kind == Kind.TOKEN_LBRACK) {
                    advance();
                    Ast.Exp.T index = parseExp();
                    eatToken(Kind.TOKEN_RBRACK);
                    eatToken(Kind.TOKEN_ASSIGN);
                    exp = parseExp();
                    eatToken(Kind.TOKEN_SEMI);
                    return new Ast.Stm.AssignArray(new Ast.Exp.Id(idStr, current.lineNum), index, exp, current.lineNum);
                }
                eatToken(Kind.TOKEN_ASSIGN);
                exp = parseExp();
                eatToken(Kind.TOKEN_SEMI);
                return new Ast.Stm.Assign(new Ast.Exp.Id(idStr, current.lineNum), exp, current.lineNum);
            default:
                error();
                return null;
        }
    }

    // Statements -> Statement Statements
    // ->
    private LinkedList<Ast.Stm.T> parseStatements() {
        LinkedList<Ast.Stm.T> stms = new LinkedList<>();
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
                || current.kind == Kind.TOKEN_WHILE
                || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID
                || (lastToken != null && lastToken.kind == Kind.TOKEN_ID)) {
            Ast.Stm.T stm = parseStatement();
            stms.add(stm);
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
        }
        return stms;
    }

    // Type -> int []
    // -> boolean
    // -> int
    // -> id
    private Ast.Type.T parseType() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a type.
        if (lastToken != null) {
            String id = lastToken.lexeme;
            lastToken = null;
            return new Ast.Type.ClassType(id, current.lineNum);
        }
        switch (current.kind) {
            case TOKEN_INT:
                advance();
                if (current.kind == Kind.TOKEN_LBRACK) {
                    advance();
                    eatToken(Kind.TOKEN_RBRACK);
                    Ast.Type.IntArray intArrayType = new Ast.Type.IntArray(current.lineNum);
                    return intArrayType;
                }
                return new Ast.Type.Int(current.lineNum);
            case TOKEN_BOOLEAN:
                advance();
                return new Ast.Type.Boolean(current.lineNum);
            case TOKEN_ID:
                String id = current.lexeme;
                advance();
                return new Ast.Type.ClassType(id, current.lineNum);
            default:
                error();
                return null;
        }
    }

    // VarDecl -> Type id ;
    private Ast.Dec.T parseVarDecl() {
        // to parse the "Type" nonterminal in this method, instead of writing
        // a fresh one.
        String id = null;
        Ast.Type.T type = null;
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        type = parseType();
        if (current.kind == Kind.TOKEN_ID) {
            id = current.lexeme;
            parseExp();
        } else
            error();
        eatToken(Kind.TOKEN_SEMI);
        Ast.Dec.T dec = new Ast.Dec.DecSingle(type, id, this.isField, current.lineNum);
        return dec;
    }

    // VarDecls -> VarDecl VarDecls
    // ->
    private LinkedList<Ast.Dec.T> parseVarDecls() {
        LinkedList<Ast.Dec.T> decs = new LinkedList<>();
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
                || current.kind == Kind.TOKEN_ID) {
            if (current.kind == Kind.TOKEN_ID) {
                lastToken = current;
                advance();
                if (current.kind != Kind.TOKEN_ID)
                    return decs;
            }
            Ast.Dec.T dec = parseVarDecl();
            decs.add(dec);
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
        }
        return decs;
    }

    // FormalList -> Type id FormalRest*
    // ->
    // FormalRest -> , Type id
    private LinkedList<Ast.Dec.T> parseFormalList() {
        LinkedList<Ast.Dec.T> decs = new LinkedList<>();
        Ast.Type.T type = null;
        if (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
                || current.kind == Kind.TOKEN_ID) {
            String id = null;
            type = parseType();
            if (current.kind == Kind.TOKEN_ID) {
                id = current.lexeme;
                advance();
            } else
                error();
            Ast.Dec.T dec = new Ast.Dec.DecSingle(type, id, this.isField, current.lineNum);
            decs.add(dec);
            while (current.kind == Kind.TOKEN_COMMER) {
                advance();
                type = parseType();
                if (current.kind == Kind.TOKEN_ID) {
                    id = current.lexeme;
                    advance();
                    Ast.Dec.T dectmp = new Ast.Dec.DecSingle(type, id, this.isField, current.lineNum);
                    decs.add(dectmp);
                } else
                    error();
            }
        }
        return decs;
    }

    // Method -> public Type id ( FormalList )
    // { VarDecl* Statement* return Exp ;}
    private Ast.Method.T parseMethod() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a method.
        String id = null;
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        eatToken(Kind.TOKEN_PUBLIC);
        Ast.Type.T retType = parseType();
        if (current.kind == Kind.TOKEN_ID) {
            id = current.lexeme;
            advance();
        } else
            error();
        eatToken(Kind.TOKEN_LPAREN);
        LinkedList<Ast.Dec.T> formals = parseFormalList();
        eatToken(Kind.TOKEN_RPAREN);
        eatToken(Kind.TOKEN_LBRACE);
        LinkedList<Ast.Dec.T> locals = parseVarDecls();
        LinkedList<Ast.Stm.T> stmts = parseStatements();
        eatToken(Kind.TOKEN_RETURN);
        Ast.Exp.T retExp = parseExp();
        eatToken(Kind.TOKEN_SEMI);
        eatToken(Kind.TOKEN_RBRACE);
        Ast.Method.T methodSingle = new Ast.Method.MethodSingle(
                retType, id, formals, locals, stmts, retExp, current.lineNum);
        return methodSingle;
    }

    // MethodDecls -> MethodDecl MethodDecls
    // ->
    private LinkedList<Ast.Method.T> parseMethodDecls() {
        LinkedList<Ast.Method.T> methodList = new LinkedList<>();
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        while (current.kind == Kind.TOKEN_PUBLIC) {
            Ast.Method.T methodSingle = parseMethod();
            methodList.add(methodSingle);
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
        }
        return methodList;
    }

    // ClassDecl -> class id { VarDecl* MethodDecl* }
    // -> class id extends id { VarDecl* MethodDecl* }
    private Ast.Class.ClassSingle parseClassDecl() {
        String id = null;
        String extendsStr = null;
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        eatToken(Kind.TOKEN_CLASS);
        if (current.kind == Kind.TOKEN_ID) {
            id = current.lexeme;
            advance();
        } else
            error();
        if (current.kind == Kind.TOKEN_EXTENDS) {
            eatToken(Kind.TOKEN_EXTENDS);
            if (current.kind == Kind.TOKEN_ID) {
                extendsStr = current.lexeme;
                advance();
            } else
                error();
        }
        eatToken(Kind.TOKEN_LBRACE);
        this.isField = true;
        LinkedList<Ast.Dec.T> decSingleLinkedList = parseVarDecls();
        this.isField = false;
        LinkedList<Ast.Method.T> methodSingleLinkedList = parseMethodDecls();
        eatToken(Kind.TOKEN_RBRACE);
        Ast.Class.ClassSingle classSingle = new Ast.Class.ClassSingle(id, extendsStr,
                decSingleLinkedList, methodSingleLinkedList, current.lineNum);
        return classSingle;
    }

    // ClassDecls -> ClassDecl ClassDecls
    // ->
    private LinkedList<Ast.Class.T> parseClassDecls() {
        LinkedList<Ast.Class.T> classSingleLinkedList = new LinkedList<>();
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        while (current.kind == Kind.TOKEN_CLASS) {
            Ast.Class.ClassSingle classDecl = parseClassDecl();
            classSingleLinkedList.add(classDecl);
            while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
                advance();
        }
        return classSingleLinkedList;
    }

    // MainClass -> class id
    // {
    // public static void main ( String [] id )
    // {
    // Statement
    // }
    // }
    private Ast.MainClass.MainClassSingle parseMainClass() {
        // Lab1. Exercise 4: Fill in the missing code
        // to parse a main class as described by the
        // grammar above.
        String id = null;
        String arg = null;

        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        eatToken(Kind.TOKEN_CLASS);
        if (current.kind == Kind.TOKEN_ID) {
            id = current.lexeme;
            advance();
        } else
            error();
        eatToken(Kind.TOKEN_LBRACE);
        eatToken(Kind.TOKEN_PUBLIC);
        eatToken(Kind.TOKEN_STATIC);
        eatToken(Kind.TOKEN_VOID);
        eatToken(Kind.TOKEN_MAIN);
        eatToken(Kind.TOKEN_LPAREN);
        eatToken(Kind.TOKEN_STRING);
        eatToken(Kind.TOKEN_LBRACK);
        eatToken(Kind.TOKEN_RBRACK);
        if (current.kind == Kind.TOKEN_ID) {
            arg = current.lexeme;
            advance();
        } else
            error();
        eatToken(Kind.TOKEN_RPAREN);
        eatToken(Kind.TOKEN_LBRACE);
        Ast.Stm.T stm = parseStatement();
        while (current.kind == Kind.TOKEN_ANNOTATION_LINE)
            advance();
        eatToken(Kind.TOKEN_RBRACE);
        eatToken(Kind.TOKEN_RBRACE);
        Ast.MainClass.MainClassSingle mainClassSingle = new Ast.MainClass.MainClassSingle(id, arg, stm, current.lineNum);
        return mainClassSingle;
    }

    // Program -> MainClass ClassDecl*
    private ast.Ast.Program.ProgramSingle parseProgram() {
        Ast.MainClass.T mainClass = parseMainClass();
        LinkedList<Ast.Class.T> classLinkedList = parseClassDecls();
        eatToken(Kind.TOKEN_EOF);
        Ast.Program.ProgramSingle programSingle = new Ast.Program.ProgramSingle(mainClass, classLinkedList, current.lineNum);
        return programSingle;
    }

    public ast.Ast.Program.T parse() {
        ast.Ast.Program.ProgramSingle prog = parseProgram();
        return prog;
    }
}
