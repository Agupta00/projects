
public class Pair implements Comparable<Pair>{
	
	public String key;
	public int value;

	public Pair(String key, int value){
		this.key=key;
		this.value=value;
		
	}

	public int compareTo(Pair p) {
		// TODO Auto-generated method stub
		if(this.value > p.value){
			return -1;
		}else if(this.value < p.value){
			return 1;
		}else if(this.value == p.value){
			return (this.key.compareTo(p.key));
		}
		return 0;
	}

	public String toString(){
		return key + " " +  Integer.toString(value);
	}
	
}
