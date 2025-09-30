fr.umontpellier.hai913.analyzer/
├── Main.java                                    // Point d'entrée
│
├── core/                                        // Composants centraux
│   ├── ASTProcessor.java                        // Processeur principal (Orchestrateur)
│   ├── ProjectExplorer.java                     // Exploration des fichiers
│   └── AnalysisResult.java                      // Résultat consolidé
│
├── parser/                                      // Couche parsing AST
│   ├── ASTParserFacade.java                     // Pattern Facade pour JDT
│   ├── JavaFileParser.java                      // Parser de fichiers individuels
│   └── ParseConfiguration.java                  // Configuration du parsing
│
├── visitors/                                    // Pattern Visiteur (Extracteurs)
│   ├── base/
│   │   ├── BaseASTVisitor.java                  // Visiteur de base
│   │   └── VisitorResult.java                   // Résultat d'un visiteur
│   │
│   ├── structural/                              // Analyse structurelle
│   │   ├── ClassStructureVisitor.java           // Extraction classes/méthodes/attributs
│   │   ├── PackageStructureVisitor.java         // Extraction packages
│   │   └── InheritanceVisitor.java              // Analyse héritage
│   │
│   └── metrics/                                 // Visiteurs métriques
│       ├── LinesOfCodeVisitor.java              // Comptage lignes
│       ├── ComplexityVisitor.java               // Complexité cyclomatique
│       └── CouplingVisitor.java                 // Couplage entre classes
│
├── model/                                       // Modèle de données (Domain Objects)
│   ├── project/
│   │   ├── JavaProject.java                     // Représentation du projet
│   │   └── PackageInfo.java                     // Information package
│   │
│   ├── structural/                              // Entités structurelles
│   │   ├── ClassInfo.java                       // Informations classe
│   │   ├── MethodInfo.java                      // Informations méthode
│   │   ├── FieldInfo.java                       // Informations attribut
│   │   └── MethodCall.java                      // Appel de méthode (pour graphe)
│   │
│   └── metrics/                                 // Métriques calculées
│       ├── ClassMetrics.java                    // Métriques par classe
│       ├── ProjectMetrics.java                  // Métriques globales
│       └── StatisticalData.java                 // Données statistiques
│
├── extractors/                                  // Pattern Strategy (Calculateurs)
│   ├── MetricExtractor.java                     // Interface extracteur
│   ├── MetricExtractorFactory.java              // Factory des extracteurs
│   │
│   ├── basic/                                   // Métriques de base
│   │   ├── ClassCountExtractor.java             // Q1.1 - Point 1
│   │   ├── MethodCountExtractor.java            // Q1.1 - Point 3
│   │   ├── LinesOfCodeExtractor.java            // Q1.1 - Point 2
│   │   └── PackageCountExtractor.java           // Q1.1 - Point 4
│   │
│   ├── averages/                                // Métriques moyennes
│   │   ├── AverageMethodsPerClassExtractor.java // Q1.1 - Point 5
│   │   ├── AverageLOCPerMethodExtractor.java    // Q1.1 - Point 6
│   │   └── AverageFieldsPerClassExtractor.java  // Q1.1 - Point 7
│   │
│   ├── ranking/                                 // Classements top 10%
│   │   ├── TopMethodCountClassesExtractor.java  // Q1.1 - Point 8
│   │   ├── TopFieldCountClassesExtractor.java   // Q1.1 - Point 9
│   │   ├── IntersectionExtractor.java           // Q1.1 - Point 10
│   │   └── TopMethodLOCExtractor.java           // Q1.1 - Point 12
│   │
│   └── advanced/                                // Métriques avancées
│       ├── ThresholdMethodCountExtractor.java   // Q1.1 - Point 11
│       └── MaxParametersExtractor.java          // Q1.1 - Point 13
│
├── callgraph/                                   // Exercice 2 - Graphe d'appels
│   ├── CallGraphBuilder.java                    // Construction du graphe
│   ├── CallGraphVisitor.java                    // Visiteur spécialisé
│   ├── MethodCallResolver.java                  // Résolution des appels
│   └── CallGraphAnalyzer.java                   // Analyse du graphe
│
├── statistics/                                  // Calculs statistiques
│   ├── StatisticsCalculator.java                // Calculateur principal
│   ├── RankingCalculator.java                   // Calculs de classements
│   └── ThresholdCalculator.java                 // Calculs de seuils
│
├── ui/                                          // Interface utilisateur (Question optionnelle)
│   ├── cli/
│   │   ├── CLIInterface.java                    // Interface ligne de commande
│   │   └── CommandParser.java                   // Parser des commandes
│   │
│   └── gui/                                     // Interface graphique (optionnel)
│       ├── MainWindow.java                      // Fenêtre principale
│       ├── MetricsPanel.java                    // Affichage métriques
│       └── CallGraphPanel.java                  // Affichage graphe d'appels
│
├── utils/                                       // Utilitaires
│   ├── FileUtils.java                           // Utilitaires fichiers
│   ├── StringUtils.java                         // Utilitaires chaînes
│   └── CollectionUtils.java                     // Utilitaires collections
│
└── config/                                      // Configuration
    ├── AnalysisConfiguration.java               // Configuration d'analyse
    └── Constants.java                            // Constantes globales