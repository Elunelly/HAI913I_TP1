# HAI913I_TP1

## État du projet

**Projet en cours de développement**

### Fonctionnalités implémentées
- Exploration récursive de projets Java
- Parsing AST avec Eclipse JDT
- Extraction de la structure des classes (champs, méthodes, modificateurs)
- Calcul des lignes de code (LOC)
- Modèle de données complet (Project, Package, Class, Method, Field)
- Quelques tests unitaires pour comprendre

### Non implémenté
- Relations d'héritage (InheritanceVisitor)
- Complexité cyclomatique (ComplexityVisitor)
- Analyse de couplage (CouplingVisitor)
- Call graph
- Interface CLI complète
- Interface GUI
- Export JSON/XML/CSV

## Installation

### Prérequis
- Java 17 ou supérieur
- Maven 3.8+

### Compilation

```bash
# Compiler
mvn clean compile

# Lancer les tests
mvn test
```


## Utilisation

### Exemple programmatique

```java
import com.core.ASTProcessor;
import com.core.AnalysisResult;
import com.visitors.structural.ClassStructureVisitor;

public class Example {
    public static void main(String[] args) {
        ASTProcessor processor = new ASTProcessor();
        processor.addVisitor(new ClassStructureVisitor());
        
        AnalysisResult result = processor.processProject("chemin/vers/projet");
        
        System.out.println("Classes: " + result.getTotalClassesCount());
        System.out.println("Méthodes: " + result.getTotalMethodsCount());
    }
}
```

### Exemple rapide

```bash
# Analyser le projet courant
mvn exec:java -Dexec.mainClass="com.mainApplication.Main"

# Analyser un projet spécifique
mvn exec:java -Dexec.mainClass="com.mainApplication.MainTemp" -Dlog.mod=DETAILED -Dlog.level=error -Dexec.args="/chemin/vers/projet"
```

## Structure du projet

```
src/main/java/com/
├── core/                   # Orchestration (ASTProcessor, ProjectExplorer)
├── parser/                 # Parsing AST (ASTParserFacade, ParseConfiguration)
├── visitors/               # Pattern Visitor pour extraction d'informations
│   ├── base/               # BaseASTVisitor, VisitorResult
│   ├── structural/         # ClassStructureVisitor
│   └── metrics/            # LOCVisitor
├── model/                  # Modèle de domaine
│   ├── project/            # ProjectInfo, PackageInfo
│   ├── structural/         # ClassInfo, MethodInfo, FieldInfo
│   └── metrics/            # ProjectMetrics, ClassMetrics
├── calculators/            # Calculs statistiques
├── extractors/             # Extracteurs de métriques
├── utils/                  # Utilitaires
└── ui/                     # Interfaces (non implémenté)
```

## Architecture

Le projet utilise le pattern Visitor pour parcourir l'AST:


Projet Java → ProjectExplorer (découverte fichiers)
           → ASTParserFacade (parsing Eclipse JDT)
           → Visitors (extraction d'infos)
           → AnalysisResult (agrégation)


## Configuration

### Logging
Variables système:
- `log.mode`: DETAILED | LOCATED | SIMPLIFIED
- `log.level`: trace | debug | info | warn | error (défaut: info)

### Parsing
```java
ParseConfiguration config = ParseConfiguration.defaultConfig()
    .withJavaVersion(17)
    .withBindingResolution(true);
```

## Dépendances principales

- Eclipse JDT Core 3.36.0
- SLF4J 2.0.9
- JUnit Jupiter 5.10.0

---

**Note**: Projet en cours de développement. Nombreuses fonctionnalités prévues non implémentées.