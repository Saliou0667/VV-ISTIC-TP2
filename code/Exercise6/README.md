# Code of your exercise

Put here all the code created for this exercise

# Answer:

## Class Cohesion avec JavaParser

## Implémentation

Voici le code pour calculer la TCC avec JavaParser :

```java
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
```

---

## Exécution

### Compilation

```bash
mvn clean compile
```

### Lancer le programme

```bash
java -cp target/classes:/home/diallo/.m2/repository/com/github/javaparser/javaparser-core/3.16.2/javaparser-core-3.16.2.jar fr.istic.vv.ClassCohesionCalculator src/main/java rapport_tcc.txt
```

- **`src/main/java`** : Le dossier contenant les fichiers source à analyser.
- **`rapport_tcc.txt`** : Fichier contenant les résultats des calculs TCC.

---

## Résultats

### Rapport généré
Le fichier `rapport_tcc.txt` contient une liste des classes avec leurs valeurs TCC. Voici un exemple :

```
Class,Package,TCC
Person,fr.istic.vv,1.00
CyclomaticComplexityCalculator,fr.istic.vv,0.00
CyclomaticComplexityVisitor,fr.istic.vv,1.00
NoGetterDetector,fr.istic.vv,0.00
Main,fr.istic.vv,1.00
ClassCohesionCalculator,fr.istic.vv,0.30
PublicElementsPrinter,fr.istic.vv,0.00

```

### Histogramme
En plus, un histogramme des valeurs TCC est affiché dans le terminal. Exemple :

```

Class Cohesion Histogram:
Class NoGetterDetector TCC: 0.0
Class CyclomaticComplexityVisitor TCC: 1.0
Class CyclomaticComplexityCalculator TCC: 0.0
Class ClassCohesionCalculator TCC: 0.3
Class PublicElementsPrinter TCC: 0.0
Class Person TCC: 1.0
Class Main TCC: 1.0
```

