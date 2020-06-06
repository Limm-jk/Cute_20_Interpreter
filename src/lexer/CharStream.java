package lexer;

import java.io.*;

class CharStream {
	private final Reader reader;
	private Character cache;
	private int reader_index = 0;
	
	static CharStream from(String file) throws FileNotFoundException {
		return new CharStream(new StringReader(file));
	}
	
	CharStream(Reader reader) {
		this.reader = reader;
		this.cache = null;
	}
	
	Char nextChar() {
		if ( cache != null ) {
			char ch = cache;
			cache = null;

			return Char.of(ch);
		}
		else {
			try {
				int ch = reader.read();
				if ( ch == -1 ) {
					return Char.end();
				}
				else {
					return Char.of((char)ch);
				}
			}
			catch ( IOException e ) {
				throw new ScannerException("" + e);
			}
		}
	}

//	static char[] Str2Arr(String str){
//		String[] answer = null;
//		char[] ret = new char[10000];
//		int ret_index = 0;
//		answer = str.split("");
//		for(String i : answer){
//			if (i != " ") {
//				ret[ret_index] = i.charAt(0);
//				ret_index++;
//			}
//		}
//		return ret;
//	}

	void pushBack(char ch) {
		cache = ch;
	}
}
