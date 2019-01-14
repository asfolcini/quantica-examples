/**
 * 
 */
package skeleton.helloworld;

/**
 * @author alberto.sfolcini
 *
 */
public class HelloWorld {

	
	/**
	 * Constructor
	 */
	public HelloWorld() {
		System.out.println("Hello World!");
	}
	
	/**
	 * Say Hi to ...
	 * @param name
	 */
	public String sayHiTo(String name) {
		return "Hello "+name;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		HelloWorld hw = new HelloWorld();
		System.out.println(hw.sayHiTo("Alberto !!"));

	}

}
