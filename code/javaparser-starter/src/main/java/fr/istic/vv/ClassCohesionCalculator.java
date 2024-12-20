package fr.istic.vv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ClassCohesionCalculator {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java ClassCohesionCalculator <source-folder> <output-report>");
            return;
        }

        String sourceFolder = args[0];
        String outputReport = args[1];

        File projectDir = new File(sourceFolder);
        FileWriter reportWriter = new FileWriter(outputReport);

        reportWriter.write("Class,Package,TCC\n");

        List<File> javaFiles = findJavaFiles(projectDir);
        Map<String, Double> cohesionHistogram = new HashMap<>();

        for (File file : javaFiles) {
            CompilationUnit cu = StaticJavaParser.parse(file);

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                double tcc = calculateTCC(clazz);
                String className = clazz.getNameAsString();
                String packageName = cu.getPackageDeclaration()
                        .map(pd -> pd.getNameAsString()).orElse("DefaultPackage");

                try {
                    reportWriter.write(String.format("%s,%s,%.2f\n",
                            className, packageName, tcc));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cohesionHistogram.put(className, tcc);
            });
        }

        reportWriter.close();
        System.out.println("Report generated at: " + outputReport);

        // Generate the histogram as text
        System.out.println("\nClass Cohesion Histogram:");
        cohesionHistogram.forEach((className, tcc) ->
                System.out.println("Class " + className + " TCC: " + tcc));
    }

    private static List<File> findJavaFiles(File dir) throws IOException {
        return java.nio.file.Files.walk(dir.toPath())
                .filter(path -> path.toString().endsWith(".java"))
                .map(java.nio.file.Path::toFile)
                .collect(Collectors.toList());
    }

    private static double calculateTCC(ClassOrInterfaceDeclaration clazz) {
        List<MethodDeclaration> methods = clazz.getMethods();
        List<String> fields = clazz.findAll(VariableDeclarator.class).stream()
                .map(VariableDeclarator::getNameAsString)
                .collect(Collectors.toList());

        int connectedPairs = 0;
        int totalPairs = 0;

        for (int i = 0; i < methods.size(); i++) {
            for (int j = i + 1; j < methods.size(); j++) {
                totalPairs++;
                if (areMethodsConnected(methods.get(i), methods.get(j), fields)) {
                    connectedPairs++;
                }
            }
        }

        return totalPairs == 0 ? 1 : (double) connectedPairs / totalPairs;
    }

    private static boolean areMethodsConnected(MethodDeclaration m1, MethodDeclaration m2, List<String> fields) {
        Set<String> m1Fields = getAccessedFields(m1, fields);
        Set<String> m2Fields = getAccessedFields(m2, fields);

        // Check if the two methods share at least one field
        m1Fields.retainAll(m2Fields);
        return !m1Fields.isEmpty();
    }

    private static Set<String> getAccessedFields(MethodDeclaration method, List<String> fields) {
        Set<String> accessedFields = new HashSet<>();
        method.findAll(com.github.javaparser.ast.expr.NameExpr.class).forEach(name -> {
            if (fields.contains(name.getNameAsString())) {
                accessedFields.add(name.getNameAsString());
            }
        });
        return accessedFields;
    }
}
