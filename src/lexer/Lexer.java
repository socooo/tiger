package lexer;

import static control.Control.ConLexer.dump;

import java.io.InputStream;
import java.io.PushbackInputStream;

import lexer.Token.Kind;
import util.Bug;

public class Lexer {
	String fname; // the input file name to be compiled
	PushbackInputStream pushbackStream; // input stream for the above file
	int lineNum = 1;

	public Lexer(String fname, InputStream fstream) {
		this.fname = fname;
		pushbackStream = new PushbackInputStream(fstream);
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		StringBuffer stringToken = new StringBuffer();
		int c = this.pushbackStream.read();
		if (-1 == c)
			// The value for "lineNum" is now "null",
			// you should modify this to an appropriate
			// line number for the "EOF" token.
			return new Token(Kind.TOKEN_EOF, null);

		// skip all kinds of "blanks"
		while (32 == c || 9 == c || 13 == c || 10 == c) {
			if (13 == c) {
				c = this.pushbackStream.read();
				if (10 == c)
					lineNum++;
				else
					new Bug();
			} else if (10 == c) {
				lineNum++;
			}
			c = this.pushbackStream.read();
		}
		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, lineNum);

		if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
			char tmp = (char) c;
			stringToken.append(tmp);
			c = this.pushbackStream.read();
			while ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)
					|| (c >= 48 && c <= 57) || c == '_') {
				stringToken.append((char) c);
				c = this.pushbackStream.read();
			}
			pushbackStream.unread(c);
			String token = stringToken.toString();
			switch (token) {
			case "boolean":
				return new Token(Kind.TOKEN_BOOLEAN, lineNum);
			case "class":
				return new Token(Kind.TOKEN_CLASS, lineNum);
			case "else":
				return new Token(Kind.TOKEN_ELSE, lineNum);
			case "extends":
				return new Token(Kind.TOKEN_EXTENDS, lineNum);
			case "false":
				return new Token(Kind.TOKEN_FALSE, lineNum);
			case "if":
				return new Token(Kind.TOKEN_IF, lineNum);
			case "int":
				return new Token(Kind.TOKEN_INT, lineNum);
			case "length":
				return new Token(Kind.TOKEN_LENGTH, lineNum);
			case "main":
				return new Token(Kind.TOKEN_MAIN, lineNum);
			case "new":
				return new Token(Kind.TOKEN_NEW, lineNum);
			case "out":
				return new Token(Kind.TOKEN_OUT, lineNum);
			case "println":
				return new Token(Kind.TOKEN_PRINTLN, lineNum);
			case "public":
				return new Token(Kind.TOKEN_PUBLIC, lineNum);
			case "return":
				return new Token(Kind.TOKEN_RETURN, lineNum);
			case "static":
				return new Token(Kind.TOKEN_STATIC, lineNum);
			case "String":
				return new Token(Kind.TOKEN_STRING, lineNum);
			case "System":
				return new Token(Kind.TOKEN_SYSTEM, lineNum);
			case "this":
				return new Token(Kind.TOKEN_THIS, lineNum);
			case "true":
				return new Token(Kind.TOKEN_TRUE, lineNum);
			case "void":
				return new Token(Kind.TOKEN_VOID, lineNum);
			case "while":
				return new Token(Kind.TOKEN_WHILE, lineNum);
			default:
				return new Token(Kind.TOKEN_ID, lineNum, token);
			}
		} else if (c >= 48 && c <= 57) {
			int a = c - 48;
			c = this.pushbackStream.read();
			while (c >= 48 && c <= 57) {
				a = a * 10 + (c - 48);
				c = this.pushbackStream.read();
			}
			pushbackStream.unread(c);
			String num = Integer.toString(a);
			return new Token(Kind.TOKEN_NUM, lineNum, num);
		}

		switch ((char) c) {
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, lineNum);
		case '&':
			if ('&' == (char) pushbackStream.read()) {
				return new Token(Kind.TOKEN_AND, lineNum);
			} else
				new Bug();
		case '+':
			return new Token(Kind.TOKEN_ADD, lineNum);
		case '-':
			return new Token(Kind.TOKEN_SUB, lineNum);
		case ',':
			return new Token(Kind.TOKEN_COMMER, lineNum);
		case '/':
			char next = (char) pushbackStream.read();
			if ('/' == next) {
				char tmp = (char) pushbackStream.read();
				while (tmp != '\n') {
					stringToken.append(tmp);
					tmp = (char) pushbackStream.read();
				}
				int annoLineNum = lineNum++;
				return new Token(Kind.TOKEN_ANNOTATION, annoLineNum, stringToken.toString());
			}else{
				pushbackStream.unread(next);
				return new Token(Kind.TOKEN_DIVIDE, lineNum);
			}
		case '.':
			return new Token(Kind.TOKEN_DOT, lineNum);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, lineNum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, lineNum);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, lineNum);
		case '<':
			return new Token(Kind.TOKEN_LT, lineNum);
		case '!':
			return new Token(Kind.TOKEN_NOT, lineNum);
		case '}':
			return new Token(Kind.TOKEN_RBRACE, lineNum);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, lineNum);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, lineNum);
		case '>':
			return new Token(Kind.TOKEN_GT, lineNum);
		case ';':
			return new Token(Kind.TOKEN_SEMI, lineNum);
		case '*':
			return new Token(Kind.TOKEN_TIMES, lineNum);
		default:
			// Lab 1, exercise 2: supply missing code to
			// lex other kinds of tokens.
			// Hint: think carefully about the basic
			// data structure and algorithms. The code
			// is not that much and may be less than 50 lines. If you
			// find you are writing a lot of code, you
			// are on the wrong way.
			new Bug();
			return null;
		}
	}

	public Token nextToken() {
		Token t = null;

		try {
			t = this.nextTokenInternal();
			while(t.kind == Kind.TOKEN_ANNOTATION){
				t = this.nextTokenInternal();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (dump)
			System.out.println(t.toString());
		return t;
	}
}
