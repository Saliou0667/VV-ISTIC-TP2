# Code of your exercise

Put here all the code created for this exercise

## Answer:
## Implementation

J'ai implémenté la classe `NoGetterDetector` pour analyser les fichiers Java dans javaparser-starter/src/main/java. Voici le code complet :

```java
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
```

---

## Pour exécuter le programme

### Compilation

Pour compiler le programme, exécutez la commande suivante après avoir configuré le pom.xml correctement:

```bash
mvn clean compile
```

### Exécution

Exécutez le programme en fournissant les arguments requis. Pour mon cas:

```bash
java -cp target/classes:/home/diallo/.m2/repository/com/github/javaparser/javaparser-core/3.16.2/javaparser-core-3.16.2.jar fr.istic.vv.NoGetterDetector src/main/java rapport.csv
```

- **`src/main/java`** : Dossier contenant les fichiers source à analyser.
- **`rapport.csv`** : Fichier CSV où sera généré le rapport.

---

## Results

### Rapport Généré

Lors de l'exécution sur les fichiers actuels (`src/main/java`), aucun champ privé sans getter public n'a été trouvé. Le fichier `rapport.txt` généré contenait uniquement :

```plaintext
Class,Package,Field
```

### Test avec un Exemple

Pour vérifier que le programme fonctionne correctement, un test a été réalisé avec le fichier suivant :

```java
package test;

public class Person {
    private String name;
    private int age;

    public String getName() {
        return name;
    }
}
```

Le champ `age` n'ayant pas de getter public, le fichier `rapport.csv` généré contenait :

```plaintext
Class,Package,Field
Person,test,age
```
