package com.example;

import org.python.util.PythonInterpreter;
import org.python.core.PyException; // Explicit import for PyException
import java.io.StringWriter;

public class JythonExecutor {

    public static class ExecutionResult {
        public final String stdout;
        public final String stderr;
        public final Exception exception; // To capture Python exceptions like syntax errors

        public ExecutionResult(String stdout, String stderr, Exception exception) {
            this.stdout = (stdout == null) ? "" : stdout;
            this.stderr = (stderr == null) ? "" : stderr;
            this.exception = exception;
        }

        public boolean hasError() {
            return !stderr.isEmpty() || exception != null;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!stdout.isEmpty()) {
                sb.append(stdout);
            }
            // Prepend STDERR prefix only if stderr is not empty
            if (!stderr.isEmpty()) {
                sb.append("\nSTDERR:\n").append(stderr);
            }
            if (exception != null) {
                // For PyException, e.g., syntax errors, toString() is often descriptive.
                // For other Java exceptions, getMessage() might be null, so toString() is safer.
                sb.append("\nEXECUTION EXCEPTION:\n").append(exception.toString());
            }
            return sb.toString().trim(); // Trim to remove leading/trailing newlines if only one part exists
        }
    }

    public JythonExecutor() {
        // Optional: Initialize PythonInterpreter properties if needed globally.
        // Using this static method can help avoid some startup issues on certain platforms or with classloaders.
        // However, it sets system-wide properties, which might not always be desirable.
        // For now, we'll rely on the default initialization within the PythonInterpreter constructor.
        // PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
    }

    public ExecutionResult executeScript(String pythonCode) {
        StringWriter stdoutCapture = new StringWriter();
        StringWriter stderrCapture = new StringWriter();
        PythonInterpreter interpreter = null; // Declare outside try to access in finally

        try {
            interpreter = new PythonInterpreter(); // Create a new interpreter for each execution for isolation
            interpreter.setOut(stdoutCapture);
            interpreter.setErr(stderrCapture);

            // Attempt to execute the code
            interpreter.exec(pythonCode);

            // If exec completes without throwing an exception, it's considered a "success" at this level
            // Actual Python runtime errors that don't throw PyException up to here (rare) would be in stderr.
            return new ExecutionResult(stdoutCapture.toString(), stderrCapture.toString(), null);

        } catch (PyException e) {
            // This is the primary way Jython signals Python-level errors (syntax, runtime like NameError, TypeError)
            // The error message from Jython (e.g., e.toString()) is often very informative.
            // stderr might also contain output leading up to the error.
            return new ExecutionResult(stdoutCapture.toString(), stderrCapture.toString(), e);
        } catch (Exception e) {
            // Catches other Java exceptions that might occur (e.g., during interpreter setup, unforeseen issues)
            // These are generally more severe or unexpected.
            return new ExecutionResult(stdoutCapture.toString(), stderrCapture.toString(), e);
        } finally {
            // Ensure the interpreter is closed to free up resources.
            // PythonInterpreter.close() is available since Jython 2.7.0.
            if (interpreter != null) {
                try {
                    interpreter.close();
                } catch (Exception e) {
                    // Log or handle the exception during close if necessary, though often just ignored
                    System.err.println("Error closing PythonInterpreter: " + e.getMessage());
                }
            }
        }
    }
}
