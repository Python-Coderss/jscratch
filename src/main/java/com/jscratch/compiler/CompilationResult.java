package com.jscratch.compiler;

import java.util.ArrayList;
import java.util.List;

public class CompilationResult {
    public boolean success;
    public List<CompilationError> errors = new ArrayList<>();
    public String output;

    public static class CompilationError {
        public String fileName;
        public int line;
        public String message;
        
        public CompilationError(String fileName, int line, String message) {
            this.fileName = fileName;
            this.line = line;
            this.message = message;
        }
    }
}
