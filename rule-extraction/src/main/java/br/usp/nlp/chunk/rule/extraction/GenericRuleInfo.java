package br.usp.nlp.chunk.rule.extraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GenericRuleInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String rule;
	
	private Map<String, RuleInfo> rules = Collections.synchronizedMap(new TreeMap<>());

	public GenericRuleInfo(String rule) {
		super();
		this.rule = rule;
	}
	
	public String getRule() {
		return rule;
	}
	
	public void addRule(String rule, String statement){
		if (rules.containsKey(rule)){
			RuleInfo stored = rules.get(rule);
			
			stored.addStatement(statement);
			stored.count();
			return;
		}
		
		RuleInfo newRule = new RuleInfo(rule, statement);
		newRule.setGenericRuleInfo(this);
		
		rules.put(rule, newRule);
	}
	
	public List<RuleInfo> getRules() {
		return Collections.unmodifiableList(new ArrayList<>(rules.values()));
	}
	
	public List<RuleInfo> getOrderedRules() {
		List<RuleInfo> rules = new ArrayList<>(this.rules.values());
		
		Collections.sort(rules);
		
		return Collections.unmodifiableList(rules);
	}
	
	public int getOccurrences(){
		int total = 0;
		
		for(RuleInfo rule : getRules()){
			total += rule.getOccurrences();
		}
		
		return total;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GenericRuleInfo)) {
			return false;
		}
		GenericRuleInfo other = (GenericRuleInfo) obj;
		if (rule == null) {
			if (other.rule != null) {
				return false;
			}
		} else if (!rule.equals(other.rule)) {
			return false;
		}
		return true;
	}
}