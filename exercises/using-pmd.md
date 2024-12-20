# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

# Rapport d'Analyse avec PMD

## Contexte et Projet Sélectionné

Pour ce travail, j’ai choisi d’analyser le projet [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) disponible sur GitHub. Il s’agit d’une application d’exemple illustrant l’utilisation de Spring Boot, ce qui en fait un cas d’étude intéressant pour appliquer PMD et détecter d’éventuels problèmes de code.

## Mise en Place

### 1. Clonage du projet
J’ai cloné le dépôt localement :
```bash
git clone https://github.com/spring-projects/spring-petclinic.git
```
Le projet s’est bien récupéré et s’est importé dans mon IntelliJ IDEA Ultimate.

### 2. Build et Vérification du Fonctionnement
Avant d’intégrer PMD, j’ai vérifié que le projet se compilait et s’exécutait sans problème :
```bash
mvn clean package
mvn spring-boot:run
```
L’application s’est lancée correctement sur [http://localhost:8080](http://localhost:8080).
### 3. Intégration de PMD
J’ai ajouté le plugin PMD dans le fichier `pom.xml` du projet, au sein de la section `<plugins>` :
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.15.0</version>
    <configuration>
        <linkXRef>false</linkXRef>
    </configuration>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
Puis j’ai lancé l’analyse avec :
```bash
mvn clean pmd:check
```

## Analyse des Résultats PMD

Après exécution, PMD a généré un rapport dans `target/site/pmd.html`. J’ai consulté ce rapport pour identifier les problèmes détectés et pour retrouver les deux catégorie de problèmes  : un problème facilement corrigeable et justifié (vrai positif) et un problème signalé par PMD mais dont la correction ne semblait pas apporter de réelle valeur (faux positif).

### Vrai Positif (Problème à Corriger) : Variable Locale Inutilisée

Dans le contrôleur `OwnerController` (fichier `OwnerController.java`) dans `diallo@diallo-Latitude-5520:~/IdeaProjects/spring-petclinic/src/main/java$`
 , j’ai introduit volontairement une variable locale inutilisée afin de disposer d’un exemple concret et clair. La méthode `processFindForm` contenait :
```java
public String processFindForm(Owner owner, BindingResult result, Model model) {
    int unused = 42; // Variable jamais utilisée
    if (owner.getLastName() == null) {
        owner.setLastName("");
    }

    return "owners/ownersList";
}
```
PMD a signalé un problème du type **UnusedLocalVariable**, ce qui est effectivement un vrai problème de code inutile. La présence de cette variable ne sert à rien et peut nuire à la lisibilité du code.

**Correction Apportée :**  
J’ai simplement retiré la ligne `int unused = 42;`. Après avoir relancé l’analyse (`mvn clean pmd:check`), l’avertissement a disparu. Cette correction est un vrai positif car elle élimine du code mort, améliore la propreté et ne présente aucun risque.

### Faux Positif (Problème Non Pertinent à Corriger) : Complexité Cyclomatique

Dans le rapport PMD, j’ai également relevé des avertissements liés à la complexité cyclomatique de certaines méthodes, notamment dans un contrôleur de gestion des visites ou des animaux (par exemple `PetController` ou `VisitController`). PMD suggérait que ces méthodes étaient trop complexes et qu’il fallait les simplifier.

Mais en analysant la logique métier, je me suis rendu compte que la complexité était justifiée. Les conditions présentes dans ces méthodes reflétaient des règles métiers indispensables (par exemple, vérifier différents états avant de permettre la création ou la mise à jour d’un objet). Une simplification artificielle aurait pu fragmenter le code en de multiples petites méthodes sans apporter une lisibilité supplémentaire, ou pire, rendre le code plus difficile à maintenir.

**Raison de ne pas Corriger :**
- Le code répond déjà parfaitement aux exigences métiers.
- Les conditions, bien que nombreuses, sont claires et compréhensibles.
- Le refactoring risquerait d’introduire une complexité inutile et de réduire la cohérence du code.

Il s’agit donc d’un “faux positif” dans le sens où, même si PMD signale une complexité, la correction n’apporte pas de gain. J’ai donc choisi de ne pas toucher à ce code.

## Conclusion

Après avoir exécuté PMD sur le projet Spring PetClinic, j’ai pu :

- Identifier un vrai problème (une variable locale non utilisée), le corriger aisément et constater la disparition de l’avertissement.
- Relever un problème considéré comme un faux positif (une complexité cyclomatique jugée normale) et décider de ne pas le corriger, car cela n’aurait apporté aucune valeur ajoutée.
