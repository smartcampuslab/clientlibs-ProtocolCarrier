package eu.trentorise.smartcampus.protocolcarrier.custom;

import java.util.Map;

public class ObjectRequestParam extends RequestParam {
	private Map<String, Object> vars;

	public Map<String, Object> getVars() {
		return vars;
	}

	public void setVars(Map<String, Object> vars) {
		this.vars = vars;
	}

}
