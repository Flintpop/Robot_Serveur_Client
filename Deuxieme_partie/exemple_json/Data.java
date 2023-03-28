package exemple_json;

public class Data {
	
	String cmd = null;
	String param1 = null;
	String param2 = null;
	
	public String toString() {
		return "{cmd : " + cmd + ", param1 : "+ param1 + ", param2 : " + param2 + "}";
	}
	
	// ajouter les accesseurs (méthodes get et set)
	// sous Eclipse clic droit puis
	// "Source" et "Generate Getters and Setters"
	

	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}

	
}
