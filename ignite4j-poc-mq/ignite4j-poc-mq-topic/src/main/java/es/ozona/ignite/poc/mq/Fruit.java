package es.ozona.ignite.poc.mq;

public enum Fruit {
	redApple("red.apple"), 
	greenApple("green.apple"), 
	yellowBanana("yellow.banana"),
	redTomato("red.tomato");
	
	private final String name;
	
	Fruit(String name) {
		this.name = name;
	}
	
	public String getName( ) {
		return name;
	}
}
