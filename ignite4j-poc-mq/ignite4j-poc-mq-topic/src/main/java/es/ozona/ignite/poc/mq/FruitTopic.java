package es.ozona.ignite.poc.mq;

public enum FruitTopic {
	redFruit("red.*"), 
	yellowFruit("yellow.*"), 
	appleFruit("*.apple");
	
	private final String name;
	
	FruitTopic(String name) {
		this.name = name;
	}
	
	public String getName( ) {
		return name;
	}
}
