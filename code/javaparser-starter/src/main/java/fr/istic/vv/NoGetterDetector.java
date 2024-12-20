package fr.istic.vv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class NoGetterDetector {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java NoGetterDetector <source-folder> <output-report>");
            return;
        }

        String sourceFolder = args[0];
        String outputReport = args[1];

        File projectDir = new File(sourceFolder);
        FileWriter reportWriter = new FileWriter(outputReport);

        reportWriter.write("Class,Package,Field\n");

        List<File> javaFiles = findJavaFiles(projectDir);
        for (File file : javaFiles) {
            CompilationUnit cu = StaticJavaParser.parse(file);

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                List<String> getters = clazz.getMethods().stream()
                        .filter(NoGetterDetector::isPublicGetter)
                        .map(MethodDeclaration::getNameAsString)
                        .collect(Collectors.toList());

                clazz.getFields().forEach(field -> {
                    String fieldName = field.getVariable(0).getNameAsString();
                    if (field.isPrivate() && !getters.contains("get" + capitalize(fieldName))) {
                        try {
                            reportWriter.write(String.format(
                                    "%s,%s,%s\n",
                                    clazz.getNameAsString(),
                                    cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse(""),
                                    fieldName
                            ));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        }

        reportWriter.close();
        System.out.println("Report generated at: " + outputReport);
    }

    private static List<File> findJavaFiles(File dir) throws IOException {
        return java.nio.file.Files.walk(dir.toPath())
                .filter(path -> {
                    System.out.println("Analyzing file: " + path.toFile().getName());
                    return path.toString().endsWith(".java");
                })
                .map(java.nio.file.Path::toFile)
                .collect(Collectors.toList());
    }


    private static boolean isPublicGetter(MethodDeclaration method) {
        return method.isPublic() &&
                method.getNameAsString().startsWith("get") &&
                method.getParameters().isEmpty() &&
                method.getType() != null;
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
