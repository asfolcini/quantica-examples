package skeleton.helloworld;


import skeleton.helloworld.HelloWorld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;



/**
 * Unit test for HelloWorld.java
 * <p/>
 * A unit test aims to test all code and code paths of a specific class.
 */
@ExtendWith(MockitoExtension.class)
public class HelloWorldTest {


	    @Test
	    public void testDefaultArgument() {
	        // Passing no arguments should work.
	        String[] args = {};	        
	        HelloWorld.main(args);
	    }

	    
	    @Test
	    public void testSayHiTo() {	        
	    	HelloWorld hw = new HelloWorld();	    		    		
	    	assertThat(hw.sayHiTo("Tester"), is(equalTo("Hello Tester")));	    	
	    }

	
	
}
