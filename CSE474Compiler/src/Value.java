
public class Value {
	private String type;
	private String strValue;
	private int intValue;
	
	public Value(int inValue){
		this.type = "INT";
		this.intValue = inValue;
	}
	
	public Value(String inValue){
		this.type = "STRING";
		this.strValue = inValue;
	}
	public Value(String inType, String inValue){
		this.type = inType;
		this.strValue = inValue;
	}

	public String getType() {
		return type;
	}
	
	public void setValue(int inValue){
		this.intValue = inValue;
	}
	
	public void setValue(String inValue){
		this.strValue = inValue;
	}
	
	public int getIntValue(){
		return this.intValue;
	}
	public String getStringValue(){
		return this.strValue;
	}

}
