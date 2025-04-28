import java.util.HashMap;
import java.util.Map;

public class Myszkowski{
	private String text;
	private String key;
	
	public Myszkowski(String text, String key) {
		this.text = text;
		this.key = key;
	}
	
	
	public StringBuilder encode() {
		Map<Character, Integer> keyM = new HashMap<>();
		for(int i=0; i<key.length(); i++) {
			keyM.put(key.charAt(i), i);
		}
		StringBuilder result = new StringBuilder();
		int row;
		int len = text.length();
		int column = key.length();
	    row = (int)Math.ceil((double) text.length() / column);
		char[][] table = new char[row][column];
		for(int i=0, tmp=0; i<row; i++) {
			for(int j=0; j<column; ) {
				if(tmp < len) {
					char c = text.charAt(tmp);
					if(Character.isLetter(c) || c == ' ') {
						table[i][j] = c;
						j++;
					}
					tmp++;
				} else {
					table[i][j] = ' ';
					j++;
				}
			}
		}
		for(Map.Entry<Character, Integer> e : keyM.entrySet()) {
			int index = e.getValue();
			for(int i=0; i<row; i++) {
				if(Character.isLetter(table[i][index]) || table[i][index] == ' ') {
					result.append(table[i][index]);
				}
			}
		}
		return result;
	}
 }
