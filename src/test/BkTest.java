package test;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * Simple testing framework modeled upon a subset of jUnit, the standard
 * Java unit testing framework.
 *
 * USAGE
 * Create a test suite containing any number of test methods, annotated
 * with the @Test annotation. Each test should perform some actions with
 * an expected result, and then compare the actual result to the
 * expected result using assert methods such as assertEquals.
 *
 * To run the tests, call the runSuites method containing the name of
 * one or more test suite classes. A (somewhat) helpful error message
 * will be printed for any failed assertions.
 */
public class BkTest {
    /** Assert that two integers are equal. */
    public static void assertEquals(int expected, int result) {
        if (expected != result)
            throw new AssertionError("Expected %d, got %d.".formatted(expected, result));
    }

    /** Assert that two strings are equal. */
    public static void assertEquals(String expected, String result) {
        if (!expected.equals(result))
            throw new AssertionError("Expected %s, got %s.".formatted(expected, result));
    }

    /** Assert that two doubles are equal. */
    public static void assertEquals(double expected, double result) {
        if (expected != result)
            throw new AssertionError("Expected %f, got %f.".formatted(expected, result));
    }

    /** Assert that a condition is false. */
    public static void assertFalse(boolean result) {
        if (result)
            throw new AssertionError("Expected false, got true.");
    }

    /** Run all tests in one or more classes and print the results. */
    public static void runSuites(String ... clsNames) {
        testResults results = new testResults(0, 0, 0);
        for (String s: clsNames) {
            results = results.plus(runSuite(s));
        }
        results.print();
    }

    private static testResults runSuite(String clsName) {
        int passed = 0, failed = 0, error = 0;
        Class<?> cls;

        try {
            cls = Class.forName(clsName);
        } catch(ClassNotFoundException e) {
            System.out.printf("Error loading %s: %s%n", clsName, e.getMessage());
            return new testResults(0, 0, 0);
        }

        for (Method m: cls.getMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                System.out.printf("%-60s",
                        "%s.%s ".formatted(clsName, m.getName()));
                try {
                    Object testInstance = getInstance(cls);
                    m.invoke(testInstance);
                    System.out.printf("Success%n");
                    passed++;
                } catch(Throwable e) {
                    System.out.printf("Failure%n  %s%n", e.getCause());
                    e.printStackTrace(System.out);
                    failed++;
                }
            }
        }

        return new testResults(passed, failed, error);
    }

    private static Object getInstance(Class<?> cls) throws ReflectiveOperationException {
        Constructor<?> ctor = cls.getDeclaredConstructor();
        return ctor.newInstance();
    }
}

class testResults {
    int passed;
    int failed;
    int error;

    public testResults(int passed, int failed, int error) {
        this.passed = passed;
        this.failed = failed;
        this.error = error;
    }

    public testResults plus(testResults other) {
        return new testResults(passed + other.passed, failed +
                other.failed, error + other.error);
    }

    public void print() {
        System.out.printf("%n%nTests run:    %3d%nTests passed: %3d%nFailed:     %3d%n",
                failed + passed, passed, failed, error);
    }
}