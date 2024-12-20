# Code of your exercise

Put here all the code created for this exercise

# Answer:
## Cyclomatic Complexity avec JavaParser
Le code ci dessous est déjà implémenté dans src/main/java/fr;istic.vv
## Code

Voici le code pour calculer la complexité cyclomatique:

```java
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
```

---

## Exécution

### Compilation

```bash
mvn clean compile
```

### Pour Lancer le programme dans mon cas:

```bash
java -cp target/classes:/home/diallo/.m2/repository/com/github/javaparser/javaparser-core/3.16.2/javaparser-core-3.16.2.jar fr.istic.vv.CyclomaticComplexityCalculator src/main/java rapport_cc.txt
```

- **`src/main/java`** : Le dossier contenant les fichiers source à analyser.
- **`rapport_cc.txt`** : Fichier contenant les résultats du calcul de complexité cyclomatique.

---

## Résultats

### Rapport généré
Le fichier `rapport_cc.txt` contient une liste de méthodes avec leurs complexités cyclomatiques. Voici le contenu :

```
Class,Package,Method,Parameters,Cyclomatic Complexity
Person,fr.istic.vv,getName,,1
CyclomaticComplexityCalculator,fr.istic.vv,main,String[],4
CyclomaticComplexityCalculator,fr.istic.vv,findJavaFiles,File,1
CyclomaticComplexityCalculator,fr.istic.vv,calculateCyclomaticComplexity,MethodDeclaration,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,IfStmt, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,ForStmt, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,ForEachStmt, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,WhileStmt, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,DoStmt, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,SwitchEntry, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,visit,CatchClause, Void,1
CyclomaticComplexityCalculator,fr.istic.vv,getComplexity,,1
NoGetterDetector,fr.istic.vv,main,String[],5
NoGetterDetector,fr.istic.vv,findJavaFiles,File,1
NoGetterDetector,fr.istic.vv,isPublicGetter,MethodDeclaration,1
NoGetterDetector,fr.istic.vv,capitalize,String,1
Main,fr.istic.vv,main,String[],3
PublicElementsPrinter,fr.istic.vv,visit,CompilationUnit, Void,2
PublicElementsPrinter,fr.istic.vv,visitTypeDeclaration,TypeDeclaration<?>, Void,5
PublicElementsPrinter,fr.istic.vv,visit,ClassOrInterfaceDeclaration, Void,1
PublicElementsPrinter,fr.istic.vv,visit,EnumDeclaration, Void,1
PublicElementsPrinter,fr.istic.vv,visit,MethodDeclaration, Void,2

```

### Histogramme
En plus, un histogramme des complexités est affiché dans le terminal :

```
Cyclomatic Complexity Histogram:
Complexity 1: 16 method(s)
Complexity 2: 2 method(s)
Complexity 3: 1 method(s)
Complexity 4: 1 method(s)
Complexity 5: 2 method(s)
```

---

## Conclusion

Le programme calcule bien la complexité cyclomatique des méthodes dans un projet Java. Le rapport CSV et l’histogramme permettent d’analyser les zones de code qui peuvent être simplifiées ou optimisées pour une meilleure maintenabilité.

