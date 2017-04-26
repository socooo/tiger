package lexer;

import static control.Control.ConLexer.dump;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import lexer.Token.Kind;
import util.Todo;

public class Lexer {
    String fname; // the input file name to be compiled
    InputStream fstream; // input stream for the above file
    PushbackInputStream backfstream;
    int lineNum = 1;
    String str = null;

    public Lexer(String fname, InputStream fstream) {
        this.fname = fname;
        this.fstream = fstream;
        this.backfstream = new PushbackInputStream(fstream);
    }

    // When called, return the next token (refer to the code "Token.java")
    // from the input stream.
    // Return TOKEN_EOF when reaching the end of the input stream.
    private Token nextTokenInternal() throws Exception {
        int c = this.backfstream.read();
        if (-1 == c)
            // The value for "lineNum" is now "null",
            // you should modify this to an appropriate
            // line number for the "EOF" token.
            return new Token(Kind.TOKEN_EOF, lineNum);

        // skip all kinds of "blanks"
        while (' ' == c || '\t' == c || '\n' == c) {
            if ('\n' == c)
                lineNum++;
            c = this.backfstream.read();
        }
        if (-1 == c)
            return new Token(Kind.TOKEN_EOF, lineNum);

        switch (c) {
            case '+':
                return new Token(Kind.TOKEN_ADD, lineNum);
            case '&':
                c = backfstream.read();
                if (c == '&')
                    return new Token(Kind.TOKEN_AND, lineNum);
                backfstream.unread(c);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '/':
                c = backfstream.read();
                if (c == '/'){
                    str = readLine();
                    return new Token(Kind.TOKEN_ANNOTATION_LINE, lineNum, str);
                }
                backfstream.unread(c);
                return new Token(Kind.TOKEN_DIVIDED, lineNum);
            case '=':
                c = backfstream.read();
                if (c == '=')
                    return new Token(Kind.TOKEN_EQUEL, lineNum);
                backfstream.unread(c);
                return new Token(Kind.TOKEN_ASSIGN, lineNum);
            case 'b':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("boolean") == 0)
                    return new Token(Kind.TOKEN_BOOLEAN, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'c':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("class") == 0)
                    return new Token(Kind.TOKEN_CLASS, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case ',':
                return new Token(Kind.TOKEN_COMMER, lineNum);
            case '.':
                return new Token(Kind.TOKEN_DOT, lineNum);
            case 'e':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("else") == 0)
                    return new Token(Kind.TOKEN_ELSE, lineNum);
                if (str.compareTo("extends") == 0)
                    return new Token(Kind.TOKEN_EXTENDS, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'f':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("false") == 0)
                    return new Token(Kind.TOKEN_FALSE, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '>':
                c = backfstream.read();
                if (c == '=')
                    return new Token(Kind.TOKEN_GE, lineNum);
                backfstream.unread(c);
                return new Token(Kind.TOKEN_GT, lineNum);
            case 'i':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("if") == 0)
                    return new Token(Kind.TOKEN_IF, lineNum);
                if (str.compareTo("int") == 0)
                    return new Token(Kind.TOKEN_INT, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '{':
                return new Token(Kind.TOKEN_LBRACE, lineNum);
            case '[':
                return new Token(Kind.TOKEN_LBRACK, lineNum);
            case '(':
                return new Token(Kind.TOKEN_LPAREN, lineNum);
            case '<':
                c = backfstream.read();
                if (c == '=')
                    return new Token(Kind.TOKEN_LE, lineNum);
                backfstream.unread(c);
                return new Token(Kind.TOKEN_LT, lineNum);
            case 'l':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("length") == 0)
                    return new Token(Kind.TOKEN_LENGTH, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'm':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("main") == 0)
                    return new Token(Kind.TOKEN_MAIN, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '!':
                c = backfstream.read();
                if (c == '=')
                    return new Token(Kind.TOKEN_NE, lineNum);
                backfstream.unread(c);
                return new Token(Kind.TOKEN_NOT, lineNum);
            case 'n':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("new") == 0)
                    return new Token(Kind.TOKEN_NEW, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'o':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("out") == 0)
                    return new Token(Kind.TOKEN_OUT, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'p':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("println") == 0)
                    return new Token(Kind.TOKEN_PRINTLN, lineNum);
                if (str.compareTo("public") == 0)
                    return new Token(Kind.TOKEN_PUBLIC, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '}':
                return new Token(Kind.TOKEN_RBRACE, lineNum);
            case ']':
                return new Token(Kind.TOKEN_RBRACK, lineNum);
            case ')':
                return new Token(Kind.TOKEN_RPAREN, lineNum);
            case 'r':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("return") == 0)
                    return new Token(Kind.TOKEN_RETURN, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case ';':
                return new Token(Kind.TOKEN_SEMI, lineNum);
            case 's':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("static") == 0)
                    return new Token(Kind.TOKEN_STATIC, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'S':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("String") == 0)
                    return new Token(Kind.TOKEN_STRING, lineNum);
                if (str.compareTo("System") == 0)
                    return new Token(Kind.TOKEN_SYSTEM, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '-':
                return new Token(Kind.TOKEN_SUB, lineNum);
            case 't':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("this") == 0)
                    return new Token(Kind.TOKEN_THIS, lineNum);
                if (str.compareTo("true") == 0)
                    return new Token(Kind.TOKEN_TRUE, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case '*':
                return new Token(Kind.TOKEN_TIMES, lineNum);
            case 'v':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("void") == 0)
                    return new Token(Kind.TOKEN_VOID, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            case 'w':
                backfstream.unread(c);
                str = readString();
                if (str.compareTo("while") == 0)
                    return new Token(Kind.TOKEN_WHILE, lineNum);
                if(isId(str))
                    return new Token(Kind.TOKEN_ID, lineNum, str);
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
            default:
                if (c >= 48 && c <= 57){
                    backfstream.unread(c);
                    str = readString();
                    if(isNum(str))
                        return new Token(Kind.TOKEN_NUM, lineNum, str);
                    return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
                }
                if((c >= 65 && c <= 90) || c == 95 || (c >= 97 && c <= 122)){
                    backfstream.unread(c);
                    str = readString();
                    if(isId(str))
                        return new Token(Kind.TOKEN_ID, lineNum, str);
                    return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
                }
                // Lab 1, exercise 2: supply missing code to
                // lex other kinds of tokens.
                // Hint: think carefully about the basic
                // data structure and algorithms. The code
                // is not that much and may be less than 50 lines. If you
                // find you are writing a lot of code, you
                // are on the wrong way.
                return new Token(Kind.TOKEN_ZZUNDEFINE, lineNum);
        }
    }

    public Token nextToken() {
        Token t = null;

        try {
            t = this.nextTokenInternal();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (dump)
            System.out.println(t.toString());
        return t;
    }

    private String readString() throws IOException {
        int c;
        StringBuffer strbuf = new StringBuffer();
        c = backfstream.read();
        while((c >= 48 && c <= 57) ||  (c >= 65 && c <= 90) ||  c == 95  ||(c >= 97 && c <= 122)){
            strbuf.append((char)c);
            c = backfstream.read();
        }
        backfstream.unread(c);
        String str = strbuf.toString();
        return str;
    }

    private String readLine() throws IOException {
        int c;
        StringBuffer strbuf = new StringBuffer();
        c = backfstream.read();
        while(c != '\n'){
            strbuf.append((char)c);
            c = backfstream.read();
        }
        backfstream.unread(c);
        String str = strbuf.toString();
        return str;
    }

    private boolean isId(String str){
        int length = str.length();
        int c;
        c = str.charAt(0);
        if(c < 65 || (c > 90 && c < 95) || c == 96 || c > 122)
            return false;
        for (int i = 1; i < length; i++){
            c = str.charAt(i);
            if(c < 48 || (c > 57 && c < 65) || (c > 90 && c < 95) || c == 96 || c > 122)
                return false;
        }
        return true;
    }

    private boolean isNum(String str){
        int length = str.length();
        int c;
        for (int i = 0; i < length; i++){
            c = str.charAt(i);
            if(c < 48 || c > 57)
                return false;
        }
        return true;
    }
}
