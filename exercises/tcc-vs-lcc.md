# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

Rappels théoriques :

TCC (Tight Class Cohesion) mesure la cohésion en ne considérant que les méthodes directement connectées par le partage d'attributs. Deux méthodes sont connectées si elles accèdent au même attribut d'instance.
LCC (Loose Class Cohesion) est plus permissif car il considère également les connections indirectes (c’est-à-dire que si une méthode M1 partage un attribut avec M2, et M2 avec M3, alors M1 et M3 sont considérées connectées indirectement pour LCC).
TCC et LCC seront égaux lorsqu’il n’y a pas de connexions « indirectes » supplémentaires par rapport aux connexions directes. Cela signifie que toutes les paires de méthodes connectées dans la classe le sont déjà directement (via un attribut partagé), donc LCC = TCC.

Exemple de classe :

java
Copier le code
public class Account {
private double balance;
private String owner;

    // Méthode 1 : utilise balance
    public void deposit(double amount) {
        balance += amount;
    }

    // Méthode 2 : utilise balance
    public void withdraw(double amount) {
        balance -= amount;
    }

    // Méthode 3 : utilise owner
    public String getOwner() {
        return owner;
    }

    // Méthode 4 : utilise owner
    public void setOwner(String name) {
        this.owner = name;
    }
}
Ici, nous avons deux groupes de méthodes :

{deposit, withdraw} connectées par l’attribut balance.
{getOwner, setOwner} connectées par l’attribut owner.
Il n’y a aucune connexion indirecte supplémentaire. Chaque paire de méthodes connectées l’est déjà directement, donc TCC = LCC.

LCC peut-il être plus faible que TCC ?
LCC est généralement supérieur ou égal à TCC, jamais inférieur, car LCC inclut toutes les connexions directes (celles de TCC) plus des connexions indirectes. Donc LCC >= TCC. Il n’est pas possible que LCC < TCC.
