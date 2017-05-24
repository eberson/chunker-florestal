package br.usp.nlp.chunk.rule.extraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {

	private static final long serialVersionUID = 1L;

	private Node parent;
	private List<Node> children = new ArrayList<>();

	private String value;
	private int level;

	public Node(String value, int level) {
		super();
		this.value = value;
		this.level = level;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void addChild(Node node) {
		node.setParent(this);
		children.add(node);
	}

	public String getValue() {
		return value;
	}

	public int getLevel() {
		return level;
	}

	public boolean isPunctuation() {
		return value.matches("\\[.\\]");
	}

	public Node back(int level) {
		Node node = this;

		while (node.getLevel() > level) {
			node = node.getParent();
		}

		return node;
	}
	
	public boolean hasChildren(){
		return !children.isEmpty();
	}

	public String generateRule() {
		if (children.isEmpty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		builder.append(getValue()).append(" --> ");

		for (int i = 0; i < children.size(); i++) {
			Node node = children.get(i);
			builder.append(node.getValue());

			if (i < children.size() - 1) {
				builder.append(", ");
			}
		}

		builder.append(".").append(Constants.LINE_SEPARATOR);

		for (Node node : children) {
			builder.append(node.generateRule());
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return generateRule();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		if (level != other.level) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
