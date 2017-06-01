package br.usp.nlp.chunk.rule.extraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RuleInfo implements Serializable, Comparable<RuleInfo> {

	private static final long serialVersionUID = 1L;

	private String rule;
	private List<String> statements = Collections.synchronizedList(new ArrayList<>());
	private AtomicInteger occurrences = new AtomicInteger(1);
	private GenericRuleInfo genericRuleInfo;

	public RuleInfo(String rule, String statement) {
		super();

		this.rule = rule;
		this.statements.add(statement);
	}
	
	public void setGenericRuleInfo(GenericRuleInfo genericRuleInfo) {
		this.genericRuleInfo = genericRuleInfo;
	}
	
	public GenericRuleInfo getGenericRuleInfo() {
		return genericRuleInfo;
	}

	public void count() {
		occurrences.incrementAndGet();
	}

	public String getRule() {
		return rule;
	}
	
	public void addStatement(String statement){
		statements.add(statement);
	}

	public List<String> getStatements() {
		return statements;
	}

	public int getOccurrences() {
		return occurrences.get();
	}
	
	public double getFrequence(){
		Double occurrences = Double.valueOf(String.valueOf(this.occurrences.get()));
		Double total = Double.valueOf(String.valueOf(genericRuleInfo.getOccurrences()));
		
		return  100 * (occurrences / total);
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
		if (!(obj instanceof RuleInfo)) {
			return false;
		}
		RuleInfo other = (RuleInfo) obj;
		if (rule == null) {
			if (other.rule != null) {
				return false;
			}
		} else if (!rule.equals(other.rule)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(RuleInfo o) {
		Double o1 = getFrequence();
		Double o2 = o.getFrequence();
		
		int result = o2.compareTo(o1);
		
		if (result == 0){
			return o.getRule().compareTo(getRule());
		}
		
		return result;
	}

}
