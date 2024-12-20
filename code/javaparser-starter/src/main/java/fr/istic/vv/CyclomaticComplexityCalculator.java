package fr.istic.vv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CyclomaticComplexityCalculator {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java CyclomaticComplexityCalculator <source-folder> <output-report>");
            return;
        }

        String sourceFolder = args[0];
        String outputReport = args[1];

        File projectDir = new File(sourceFolder);
        FileWriter reportWriter = new FileWriter(outputReport);

        reportWriter.write("Class,Package,Method,Parameters,Cyclomatic Complexity\n");

        List<File> javaFiles = findJavaFiles(projectDir);
        Map<Integer, Integer> complexityHistogram = new HashMap<>();

        for (File file : javaFiles) {
            CompilationUnit cu = StaticJavaParser.parse(file);

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int complexity = calculateCyclomaticComplexity(method);
                String className = cu.findFirst(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)
                        .map(c -> c.getNameAsString()).orElse("UnknownClass");
                String packageName = cu.getPackageDeclaration()
                        .map(pd -> pd.getNameAsString()).orElse("DefaultPackage");
                String parameters = method.getParameters().stream()
                        .map(p -> p.getType().toString())
                        .collect(Collectors.joining(", "));

                try {
                    reportWriter.write(String.format("%s,%s,%s,%s,%d\n",
                            className, packageName, method.getNameAsString(), parameters, complexity));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                complexityHistogram.put(complexity, complexityHistogram.getOrDefault(complexity, 0) + 1);
            });
        }

        reportWriter.close();
        System.out.println("Report generated at: " + outputReport);

        // Generate the histogram as text
        System.out.println("\nCyclomatic Complexity Histogram:");
        complexityHistogram.forEach((cc, count) ->
                System.out.println("Complexity " + cc + ": " + count + " method(s)"));
    }

    private static List<File> findJavaFiles(File dir) throws IOException {
        return java.nio.file.Files.walk(dir.toPath())
                .filter(path -> path.toString().endsWith(".java"))
                .map(java.nio.file.Path::toFile)
                .collect(Collectors.toList());
    }

    private static int calculateCyclomaticComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        visitor.visit(method, null);
        return visitor.getComplexity();
    }

    private static class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Void> {
        private int complexity = 1; // Start at 1 (the method itself)

        @Override
        public void visit(IfStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        @Override
        public void visit(ForStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        @Override
        public void visit(ForEachStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        @Override
        public void visit(WhileStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        @Override
        public void visit(DoStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        @Override
        public void visit(SwitchEntry n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        @Override
        public void visit(CatchClause n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }

        public int getComplexity() {
            return complexity;
        }
    }
}
