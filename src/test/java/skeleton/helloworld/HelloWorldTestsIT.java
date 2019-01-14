package skeleton.helloworld;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import skeleton.helloworld.HelloWorld;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Integration test for the HelloWorld.java.
 * <p>
 * An integration test verifies the workings of a complete program, a module, or a set of dependant classes.
 */
public class HelloWorldTestsIT {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void before() {
        // By putting our own PrintStream in the place of the normal System.out,
        // the output produced by the application can be verified.
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    public void cleanUp() {
        // Restore the original System.out to prevent weirdness in any following tests.
        System.setOut(originalOut);
    }

    @Test
    public void doesItSayHelloTest() {
        String[] args = {};
        HelloWorld.main(args);

        assertThat(out.toString(), containsString("Alberto"));
    }

    @Test
    public void doesItSayHelloTest2() {
        HelloWorld hw = new HelloWorld();
        System.out.println(hw.sayHiTo("IntegrationTest"));
        
        assertThat(out.toString(), containsString("Hello IntegrationTest"));
    }
}
