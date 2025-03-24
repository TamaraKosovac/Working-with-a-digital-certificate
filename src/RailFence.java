
public class RailFence {

	private String text1;
	private int key;
	
	public RailFence(String text1, int key) {
		this.text1 = text1;
		this.key = key;
	}
	
	public StringBuilder encode() {
		int r=0, tmp=0;
		String text = text1.replaceAll("\\s+", "");
		char[][] table = new char[key][text.length()];
		StringBuilder result = new StringBuilder();
		
		for(int i=0; i<key; i++) {
			for(int j=0; j<text.length(); j++) {
				table[i][j] = '/';
			}
		}
		
		for(int i=0; i<text.length(); i++) {
			if(tmp == 0) {
				table[r][i] = text.charAt(i);
				r++;
				if(r == key) {
					r--;
					tmp = 1;
				}
			} else if(tmp == 1) {
				r--;
				table[r][i] = text.charAt(i);
				if(r == 0) {
					r = 1;
					tmp = 0;
				}
			}
		}
		
		for(int i=0; i<key; i++) {
			for(int j=0; j<text.length(); j++) {
				if(table[i][j] != '/') {
					result.append(table[i][j]);
				}
			}
		}
		return result;
	}
	
}
