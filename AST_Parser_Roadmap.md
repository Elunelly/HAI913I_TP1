# STATIC ANALYSIS SYSTEM - PROJECT ROADMAP

**Project Type:** Java Static Analysis Tool with AST Parsing
**Target:** Complete system for code comprehension, metrics extraction, and call graph analysis
**Duration:** 8-12 weeks
**Current Status:** Phase 2 - Project Structure (In Progress)

---

## PHASE 1: FOUNDATIONAL INFRASTRUCTURE (Weeks 1-2)

**Status:** Completed
**Objective:** Establish core parsing capabilities and base visitor architecture

### Milestone 1.1: AST Parsing Infrastructure
**Status:** Complete
**Duration:** 2-3 days

**Components Implemented:**
- ParseConfiguration: Configures JDT parser with JLS version, binding resolution, and classpath settings
- ASTParserFacade: Facade pattern implementation for Eclipse JDT parser, supporting String and File input
- JavaFileParser: Utility for batch file operations

**Technical Objectives Achieved:**
- Abstract away Eclipse JDT complexity through clean API
- Support multiple parsing modes (fast syntax-only vs full semantic analysis)
- Enable flexible parser configuration for different analysis scenarios

**Validation Criteria:**
- Parse single Java file from String
- Parse Java file from disk
- Configure parser for different JLS versions

**Git Commit:** `feat: add AST parsing infrastructure`

---

### Milestone 1.2: Structural Domain Model
**Status:** Complete
**Duration:** 2-3 days

**Components Implemented:**
- NodeInfo: Abstract base class for all AST node representations
- NodeVisibility: Enumeration for Java visibility modifiers (public, protected, private, package)
- ClassInfo: Encapsulates class metadata including fields, methods, inheritance relationships
- MethodInfo: Represents method signatures, parameters, return types, visibility
- FieldInfo: Captures field declarations with type and visibility information
- MethodCall: Models method invocation with caller, callee, and receiver type

**Technical Objectives Achieved:**
- Create immutable value objects where appropriate
- Implement defensive copying for collections
- Establish clear ownership and lifecycle semantics
- Support both simple names and fully qualified names

**Design Principles Applied:**
- Information Expert: Each class manages its own data
- Single Responsibility: Each class has one clear purpose
- Open/Closed: Extensible through inheritance without modification

**Validation Criteria:**
- Instantiate all model objects with valid data
- Test getter/setter contracts
- Verify collection immutability guarantees
- Validate qualified name generation

**Git Commit:** `feat: complete structural model (ClassInfo, MethodInfo, FieldInfo)`

---

### Milestone 1.3: Visitor Infrastructure
**Status:** Complete
**Duration:** 2-3 days

**Components Implemented:**
- BaseASTVisitor: Abstract visitor implementing Eclipse JDT's ASTVisitor
- VisitorResult: Container for visitor extraction results with metadata

**Technical Objectives Achieved:**
- Implement Visitor pattern for AST traversal
- Provide template method pattern for common visitor operations
- Support pre/post visit hooks for flexible processing
- Enable visitor chaining and composition

**Architecture:**
```
BaseASTVisitor (abstract)
    |
    +-- defines visit(JavaProject) template method
    +-- provides preVisit2() / postVisit() hooks
    +-- manages VisitorResult lifecycle
    |
    v produces
VisitorResult
    |
    +-- contains extracted data (List<ClassInfo>, etc.)
    +-- provides metadata (visitor name, execution time)
```

**Validation Criteria:**
- Traverse simple AST without errors
- Collect nodes during traversal
- Verify pre/post visit hook execution order
- Test visitor result aggregation

**Git Commit:** `feat: add visitor infrastructure (BaseASTVisitor, VisitorResult)`

---

### Milestone 1.4: Initial Concrete Visitor
**Status:** Complete
**Duration:** 2-3 days

**Components Implemented:**
- ClassStructureVisitor: Extracts classes, methods, fields, and visibility information

**Technical Objectives Achieved:**
- Implement complete visitor for structural extraction
- Handle nested classes and anonymous inner classes
- Extract method signatures including parameters and return types
- Capture field declarations with initialization expressions
- Resolve visibility modifiers correctly
- Associate elements with their containing package

**Processing Flow:**
```
ClassStructureVisitor
    |
    v visits
CompilationUnit (root AST node)
    |
    +-- extracts package declaration
    +-- visits TypeDeclaration nodes
        |
        +-- creates ClassInfo
        +-- visits MethodDeclaration nodes -> MethodInfo
        +-- visits FieldDeclaration nodes -> FieldInfo
    |
    v returns
VisitorResult containing List<ClassInfo>
```

**Validation Criteria:**
- Parse real Java class with multiple methods and fields
- Verify all methods extracted with correct signatures
- Confirm field types and visibility captured accurately
- Test nested class handling
- Validate package association

**Git Commit:** `feat: implement ClassStructureVisitor`

---

## PHASE 2: PROJECT STRUCTURE (Weeks 3-4)

**Status:** In Progress (40% complete)
**Objective:** Build project-level abstractions and filesystem integration

### Milestone 2.1: Project Domain Model
**Status:** In Progress
**Duration:** 2-3 days

**Components to Complete:**

#### JavaProject
**Current Status:** Partial implementation
**Remaining Work:**
- Complete all query methods (filtering, searching)
- Implement project statistics aggregation
- Add project-level caching mechanisms
- Support incremental updates

**Technical Objectives:**
- Serve as aggregate root for entire project domain
- Maintain multiple views of same data (by package, by class, flat list)
- Provide O(1) lookup performance for class resolution
- Support lazy loading for large projects
- Enable project comparison and diff operations

**Key Responsibilities:**
- Store and index all CompilationUnits from parsing
- Organize ClassInfo instances by package hierarchy
- Maintain bidirectional relationships (package-class, class-method)
- Provide query interface for analysis components

**Data Structures:**
```
JavaProject
    |
    +-- packages: Map<String, PackageInfo>        [O(1) package lookup]
    +-- classes: List<ClassInfo>                   [Ordered collection]
    +-- classIndex: Map<String, ClassInfo>         [O(1) class lookup by qualified name]
    +-- compilationUnits: List<CompilationUnit>    [AST cache for multi-pass analysis]
```

#### PackageInfo
**Current Status:** Partial implementation
**Remaining Work:**
- Implement parent-child hierarchy navigation
- Add package-level metrics aggregation
- Support sub-package queries
- Enable package dependency analysis

**Technical Objectives:**
- Mirror Java package namespace structure
- Support hierarchical queries (all classes in package tree)
- Enable package-level statistics (total classes, LOC, complexity)
- Facilitate package dependency graph construction

**Key Responsibilities:**
- Maintain parent reference for upward navigation
- Store child packages for downward traversal
- Aggregate classes directly in this package (not descendants)
- Calculate package depth and path components

**Hierarchical Structure:**
```
PackageInfo: "com.example.util"
    |
    +-- parent: PackageInfo("com.example")
    +-- subPackages: Map<String, PackageInfo>
    |       |
    |       +-- "collections" -> PackageInfo("com.example.util.collections")
    |       +-- "io" -> PackageInfo("com.example.util.io")
    |
    +-- classes: List<ClassInfo>
            |
            +-- StringUtils
            +-- FileUtils
```

**Validation Criteria:**
- Create multi-level package hierarchy
- Navigate from leaf to root package
- Query all classes in package subtree
- Calculate package metrics

**Git Commit:** `feat: implement JavaProject and PackageInfo with hierarchy`

---

### Milestone 2.2: Filesystem Discovery
**Status:** Not Started
**Duration:** 2-3 days

**Components to Implement:**

#### ProjectExplorer
**Primary Responsibilities:**
- Recursively discover all .java files in directory tree
- Filter files by package patterns
- Build package hierarchy from filesystem structure
- Handle filesystem errors gracefully
- Support exclusion patterns (test directories, generated code)

**Technical Objectives:**
- Use Java NIO.2 Files.walk() for efficient traversal
- Support symbolic link handling
- Implement configurable depth limits
- Enable parallel directory scanning for large projects
- Provide progress callbacks for UI feedback

**Key Algorithms:**
```
exploreDirectory(Path rootPath):
    1. Walk file tree starting at rootPath
    2. Filter for .java extensions
    3. Group files by derived package name
    4. Build PackageInfo hierarchy
    5. Associate files with packages
    6. Return populated JavaProject

buildPackageStructure(Map<String, List<File>>):
    1. Sort package names by depth
    2. Create PackageInfo for each unique package
    3. Establish parent-child relationships
    4. Link packages to JavaProject
```

**Edge Cases to Handle:**
- Files without package declaration (default package)
- Non-UTF-8 encoded source files
- Inaccessible directories (permission denied)
- Broken symbolic links
- Very deep package hierarchies (>20 levels)

**Configuration Options:**
- Include/exclude patterns (regex or glob)
- Maximum directory depth
- Follow symbolic links (boolean)
- Parallel scanning threshold

**Validation Criteria:**
- Explore standard Maven project structure (src/main/java)
- Handle project with 1000+ classes
- Correctly build package hierarchy for multi-level packages
- Gracefully skip non-Java files
- Report errors without halting entire exploration

**Git Commit:** `feat: add ProjectExplorer for filesystem discovery`

---

### Milestone 2.3: Parsing Integration
**Status:** Not Started
**Duration:** 1-2 days

**Components to Enhance:**

#### ASTParserFacade Extensions
**New Methods to Add:**
- parseProject(List<File>): Batch parse multiple files
- parseProjectDirectory(Path): Combined explore + parse pipeline
- parseIncremental(JavaProject, List<File>): Update existing project

**Technical Objectives:**
- Implement efficient batch parsing with error recovery
- Provide parsing progress callbacks
- Support parallel parsing for performance
- Cache parsed CompilationUnits
- Handle parsing errors without failing entire batch

**Error Handling Strategy:**
- Collect all parsing errors rather than fail-fast
- Associate errors with source files
- Provide detailed error reporting (line number, error message)
- Continue parsing remaining files after error

**Performance Optimizations:**
- Use fastConfig() for pure structural analysis
- Enable parallel parsing with ForkJoinPool
- Implement parsing result cache
- Support incremental re-parsing of modified files

**Integration Flow:**
```
Client
    |
    v calls
ProjectExplorer.exploreDirectory(path)
    |
    v discovers
List<File> javaFiles
    |
    v passes to
ASTParserFacade.parseProject(files)
    |
    v parses each file
List<CompilationUnit> units
    |
    v stores in
JavaProject.addCompilationUnits(units)
```

**Validation Criteria:**
- Parse complete Maven project (src/main/java)
- Handle mix of valid and invalid Java files
- Verify all CompilationUnits stored in JavaProject
- Test parsing performance on 500+ file project
- Confirm error collection mechanism

**Git Commit:** `feat: integrate parsing with project structure`

---

### Milestone 2.4: Additional Structural Visitors
**Status:** Not Started
**Duration:** 3-4 days

**Components to Implement:**

#### InheritanceVisitor
**Primary Responsibilities:**
- Extract class inheritance relationships (extends)
- Capture interface implementations (implements)
- Build inheritance hierarchy graph
- Detect multiple interface inheritance

**Technical Objectives:**
- Resolve super class qualified names
- Handle generic type parameters in inheritance
- Support both class and interface inheritance
- Enable inheritance tree queries (ancestors, descendants)

**Extraction Strategy:**
```
visit(TypeDeclaration node):
    1. Get super class type from node.getSuperclassType()
    2. Resolve to fully qualified name
    3. Store in ClassInfo.setSuperClass()
    4. Iterate node.superInterfaceTypes()
    5. Resolve each interface to qualified name
    6. Add to ClassInfo.addInterface()
```

**Data Captured:**
- Direct super class (if any)
- All directly implemented interfaces
- Generic type arguments in inheritance
- Abstract class vs concrete class distinction

#### PackageStructureVisitor
**Primary Responsibilities:**
- Extract package declarations from CompilationUnits
- Build PackageInfo objects
- Associate classes with packages
- Construct package hierarchy

**Technical Objectives:**
- Parse package declaration statements
- Handle default (unnamed) package
- Support package-info.java files
- Extract package-level annotations

**Extraction Strategy:**
```
visit(CompilationUnit node):
    1. Extract PackageDeclaration if present
    2. Get fully qualified package name
    3. Create or retrieve PackageInfo
    4. Associate all types in CU with package
    5. Build parent-child package relationships
```

**Validation Criteria:**
- Detect all inheritance relationships in test project
- Build complete inheritance graph
- Query ancestors for any class
- Extract package structure matching filesystem
- Handle default package correctly

**Git Commit:** `feat: add InheritanceVisitor and PackageStructureVisitor`

---

## PHASE 3: ANALYSIS ORCHESTRATION (Week 5)

**Status:** Not Started
**Objective:** Build central processing pipeline coordinating all analysis components

### Milestone 3.1: Central Processor
**Status:** Not Started
**Duration:** 3-4 days

**Components to Implement:**

#### ASTProcessor
**Primary Responsibilities:**
- Coordinate entire analysis workflow
- Manage visitor lifecycle and execution order
- Aggregate results from multiple visitors
- Provide configurable analysis pipeline

**Technical Objectives:**
- Implement Pipeline pattern for analysis stages
- Support visitor registration and ordering
- Enable conditional visitor execution
- Provide progress monitoring
- Handle partial failures gracefully

**Processing Pipeline:**
```
ASTProcessor.processProject(projectPath):
    1. EXPLORATION STAGE
       - Use ProjectExplorer to discover files
       - Build initial JavaProject structure
    
    2. PARSING STAGE
       - Use ASTParserFacade to parse files
       - Store CompilationUnits in project
       - Collect parsing errors
    
    3. VISITOR EXECUTION STAGE
       - Apply each registered visitor in order
       - Pass JavaProject to visitor
       - Collect VisitorResults
    
    4. AGGREGATION STAGE
       - Merge results from all visitors
       - Calculate cross-cutting metrics
       - Build unified AnalysisResult
    
    5. EXTRACTION STAGE (optional)
       - Apply metric extractors
       - Generate statistical summaries
    
    Return: AnalysisResult
```

**Visitor Management:**
- Support visitor registration: addVisitor(BaseASTVisitor)
- Enable visitor ordering: setVisitorOrder(List<Class>)
- Allow conditional execution: visitor.isApplicable(project)
- Provide visitor dependencies: visitor.getRequiredVisitors()

**Error Handling:**
- Collect errors from each stage
- Continue execution despite failures
- Provide detailed error context
- Support fail-fast vs error-tolerant modes

#### AnalysisResult
**Primary Responsibilities:**
- Aggregate all analysis outputs
- Provide unified query interface
- Store project reference
- Contain metrics and statistics

**Technical Objectives:**
- Serve as central result repository
- Enable result serialization (JSON, XML)
- Support incremental result building
- Provide type-safe metric access

**Data Structure:**
```
AnalysisResult
    |
    +-- project: JavaProject
    +-- metrics: Map<String, Object>
    +-- classes: List<ClassInfo> (flattened from project)
    +-- methodCalls: List<MethodCall>
    +-- inheritanceGraph: Graph<ClassInfo>
    +-- callGraph: Graph<MethodInfo>
    +-- errors: List<AnalysisError>
    +-- executionTime: Duration
```

**Query Methods:**
- getMetric(String name): Object
- getClasses(): List<ClassInfo>
- getClassesByPackage(String): List<ClassInfo>
- getMethodCalls(): List<MethodCall>
- hasErrors(): boolean

**Validation Criteria:**
- Process complete project through full pipeline
- Verify all visitors executed
- Confirm results aggregated correctly
- Test error collection mechanism
- Validate analysis result completeness

**Git Commit:** `feat: implement ASTProcessor orchestration`

---

## PHASE 4: ADVANCED ANALYSIS (Weeks 6-7)

**Status:** Not Started
**Objective:** Implement sophisticated analysis capabilities for call graphs, metrics, and code quality assessment

### Milestone 4.1: Method Call Graph Analysis
**Status:** Not Started
**Duration:** 5-7 days

**Components to Implement:**

#### Model Extensions
**MethodCall Enhancements:**
- Caller method (MethodInfo)
- Callee method (MethodInfo)
- Receiver type (resolved type)
- Call site location (line number)
- Call type (static, instance, super, constructor)
- Arguments (optional: expressions)

#### CallGraphVisitor
**Primary Responsibilities:**
- Detect all method invocations in source code
- Resolve method call targets
- Determine receiver types
- Build MethodCall objects

**Technical Objectives:**
- Handle different invocation types (static, instance, super, constructor)
- Resolve overloaded methods
- Support method references (lambdas)
- Handle dynamic dispatch scenarios

**Detection Strategy:**
```
visit(MethodInvocation node):
    1. Identify caller method (current method context)
    2. Extract method name from invocation
    3. Resolve receiver expression
    4. Determine receiver type
    5. Look up callee method in resolved type
    6. Create MethodCall(caller, callee, receiverType)
    7. Store in result collection

visit(SuperMethodInvocation node):
    Handle super.method() calls specially

visit(ConstructorInvocation node):
    Handle this() and super() constructor calls
```

**Challenges:**
- Polymorphism: receiver runtime type vs compile-time type
- Method overloading: selecting correct overload based on arguments
- Generics: type parameter substitution
- Anonymous classes: synthetic method generation
- Lambda expressions: functional interface method mapping

#### MethodCallResolver
**Primary Responsibilities:**
- Resolve method call ambiguities
- Handle overloading resolution
- Support inheritance method lookup
- Resolve interface method implementations

**Technical Objectives:**
- Implement JLS method resolution algorithm
- Support multi-level inheritance lookups
- Handle default interface methods
- Resolve bridge methods for generics

**Resolution Algorithm:**
```
resolveMethodCall(receiverType, methodName, argTypes):
    1. Search receiverType for matching method
    2. If not found, search super classes
    3. If not found, search implemented interfaces
    4. Apply overload resolution rules
    5. Consider generic type substitution
    6. Return resolved MethodInfo or null
```

#### CallGraphBuilder
**Primary Responsibilities:**
- Construct directed graph of method calls
- Provide graph queries (callers, callees, paths)
- Support graph analysis algorithms
- Enable graph visualization export

**Technical Objectives:**
- Implement efficient graph representation (adjacency list)
- Support transitive queries (all reachable methods)
- Calculate graph metrics (in-degree, out-degree, centrality)
- Detect cycles (recursive call chains)

**Graph Structure:**
```
CallGraph
    |
    +-- nodes: Set<MethodInfo>
    +-- edges: Map<MethodInfo, Set<MethodInfo>>
    |
    +-- methods:
        - getCallers(method): Set<MethodInfo>
        - getCallees(method): Set<MethodInfo>
        - getCallChain(from, to): List<MethodInfo>
        - findCycles(): List<List<MethodInfo>>
        - getRootMethods(): Set<MethodInfo> (entry points)
        - getLeafMethods(): Set<MethodInfo> (no callees)
```

**Validation Criteria:**
- Detect all method calls in test project
- Resolve overloaded method calls correctly
- Build complete call graph
- Query caller/callee relationships
- Identify recursive methods
- Handle polymorphic calls appropriately

**Git Commit:** `feat: implement call graph analysis`

---

### Milestone 4.2: Code Metrics Collection
**Status:** Not Started
**Duration:** 5-6 days

**Components to Implement:**

#### Metrics Model
**Classes to Create:**
- ProjectMetrics: System-wide aggregates
- PackageMetrics: Per-package statistics
- ClassMetrics: Class-level measurements
- MethodMetrics: Method-level measurements
- StatisticalData: Min, max, mean, median, std dev containers

**Metrics Categories:**
1. Size Metrics: LOC, number of classes, methods, fields
2. Complexity Metrics: Cyclomatic complexity, nesting depth
3. Coupling Metrics: Afferent/efferent coupling, instability
4. Cohesion Metrics: LCOM (Lack of Cohesion of Methods)
5. Inheritance Metrics: Depth of inheritance, number of children

#### LinesOfCodeVisitor
**Metrics Collected:**
- Physical LOC (including blank lines and comments)
- Logical LOC (executable statements only)
- Comment lines
- Blank lines
- LOC per method, class, package, project

**Counting Strategy:**
```
visit(MethodDeclaration node):
    1. Get source range from AST node
    2. Extract source text for method
    3. Count total lines (physical LOC)
    4. Count executable statements (logical LOC)
    5. Count comment lines
    6. Store in MethodInfo/ClassMetrics
```

**Challenges:**
- Distinguish comments from code
- Handle multi-line statements
- Count inline comments correctly
- Exclude generated code (lombok, etc.)

#### ComplexityVisitor
**Metrics Collected:**
- Cyclomatic Complexity (McCabe): Number of decision points + 1
- Nesting Depth: Maximum nested block level
- Number of Parameters: Method parameter count

**Cyclomatic Complexity Algorithm:**
```
visit(MethodDeclaration node):
    complexity = 1
    Count each:
        - if statement: +1
        - else if: +1
        - for loop: +1
        - while loop: +1
        - do-while: +1
        - case label: +1
        - catch clause: +1
        - conditional expression (? :): +1
        - logical AND/OR in conditions: +1
    Store complexity in MethodMetrics
```

**Complexity Thresholds:**
- 1-10: Simple method
- 11-20: Moderate complexity
- 21-50: Complex method
- 50+: Very complex, refactoring recommended

#### CouplingVisitor
**Metrics Collected:**
- Afferent Coupling (Ca): Number of classes depending on this class
- Efferent Coupling (Ce): Number of classes this class depends on
- Instability (I): Ce / (Ca + Ce)
- Abstractness (A): Abstract classes / Total classes

**Coupling Detection:**
```
visit(TypeDeclaration node):
    For class C:
        Scan all field types -> add to Ce
        Scan all method parameter types -> add to Ce
        Scan all method return types -> add to Ce
        Scan all local variable types -> add to Ce
        Scan all instantiations (new X()) -> add to Ce
    
    Build reverse map for Ca:
        For each class X that C depends on:
            Increment Ca(X)
```

**Coupling Analysis:**
- Detect highly coupled classes (Ce > 20)
- Identify unstable classes (I > 0.7)
- Find stable classes (I < 0.3)
- Calculate package-level coupling

**Validation Criteria:**
- Count LOC accurately for various code styles
- Calculate cyclomatic complexity correctly
- Detect all coupling relationships
- Compute metrics for entire project
- Generate statistical summaries

**Git Commit:** `feat: add metrics visitors (LOC, complexity, coupling)`

---

### Milestone 4.3: Metric Extraction and Aggregation
**Status:** Not Started
**Duration:** 3-4 days

**Components to Implement:**

#### MetricExtractor Interface
**Contract:**
```java
interface MetricExtractor {
    String getMetricName();
    MetricType getMetricType();
    boolean isApplicable(AnalysisResult result);
    Object extract(AnalysisResult result);
}
```

**Extractor Types:**
- Count Extractors: Simple counts (classes, methods, etc.)
- Average Extractors: Mean values across project
- Ranking Extractors: Top/bottom N elements
- Distribution Extractors: Histograms and percentiles
- Threshold Extractors: Items exceeding limits

#### Concrete Extractors
**To Implement:**
1. ClassCountExtractor: Total classes in project
2. MethodCountExtractor: Total methods
3. AvgMethodsPerClassExtractor: Mean methods per class
4. AvgLOCPerMethodExtractor: Mean lines per method
5. TopClassesByLOCExtractor: Largest classes (top 10)
6. TopMethodsByComplexityExtractor: Most complex methods (top 10)
7. MostCoupledClassesExtractor: Highest coupling (top 10)
8. PackageMetricsExtractor: Per-package aggregates

**Extraction Example:**
```
TopClassesByLOCExtractor.extract(result):
    1. Get all classes from result
    2. Sort by LOC (descending)
    3. Take top 10
    4. Format as RankedResult<ClassInfo>
    5. Return result
```

#### MetricExtractorFactory
**Responsibilities:**
- Create extractors by type
- Provide predefined extractor sets
- Support custom extractor registration

**Factory Methods:**
```java
createAllExtractors(): List<MetricExtractor>
createBasicExtractors(): List<MetricExtractor>
createRankingExtractors(): List<MetricExtractor>
createExtractor(MetricType): MetricExtractor
```

**Validation Criteria:**
- Extract all basic counts
- Calculate all averages correctly
- Generate top 10 rankings
- Aggregate package-level metrics
- Create factory-produced extractors

**Git Commit:** `feat: implement metric extractors with factory`

---

### Milestone 4.4: Statistical Analysis
**Status:** Not Started
**Duration:** 2-3 days

**Components to Implement:**

#### StatisticsCalculator
**Calculations Provided:**
- Minimum value in collection
- Maximum value in collection
- Mean (arithmetic average)
- Median (50th percentile)
- Standard deviation
- Variance
- Quartiles (25th, 50th, 75th percentiles)
- Interquartile range

**Algorithm Implementation:**
```
calculateStatistics(values: List<Double>): StatisticalData
    1. Sort values
    2. Calculate min = values[0]
    3. Calculate max = values[n-1]
    4. Calculate mean = sum / n
    5. Calculate median = middle value
    6. Calculate variance = sum((x - mean)^2) / n
    7. Calculate stddev = sqrt(variance)
    8. Calculate quartiles
    9. Return StatisticalData object
```

#### RankingCalculator
**Ranking Operations:**
- Sort elements by metric value
- Extract top N elements
- Extract bottom N elements
- Calculate percentile ranks
- Group by metric ranges

**Ranking Example:**
```
rankByComplexity(methods: List<MethodInfo>, topN: int):
    1. Extract complexity from each method
    2. Create (method, complexity) pairs
    3. Sort by complexity descending
    4. Take first topN elements
    5. Return ranked list
```

**Validation Criteria:**
- Compute statistics on sample data
- Verify median calculation
- Test standard deviation accuracy
- Generate rankings correctly
- Handle edge cases (empty lists, single element)

**Git Commit:** `feat: add statistics and ranking calculators`

---

## PHASE 5: USER INTERFACES (Weeks 8-9)

**Status:** Not Started
**Objective:** Provide accessible interfaces for system interaction

### Milestone 5.1: Command-Line Interface
**Status:** Not Started
**Duration:** 3-4 days

**Components to Implement:**

#### CLIInterface
**Command Structure:**
```
java -jar ast-analyzer.jar [OPTIONS] <project-path>

Options:
  --analysis <type>       Type of analysis (structure, metrics, callgraph, all)
  --output <file>         Output file (default: stdout)
  --format <fmt>          Output format (text, json, xml, csv)
  --filter <pattern>      Filter packages/classes (regex)
  --top <n>               Show top N results (default: 10)
  --threshold <value>     Complexity threshold for warnings
  --verbose               Enable verbose logging
  --help                  Show help message
```

**Analysis Types:**
- structure: Extract project structure only
- metrics: Calculate all code metrics
- callgraph: Build method call graph
- coupling: Analyze class dependencies
- all: Run complete analysis suite

#### CommandParser
**Parsing Strategy:**
- Use Apache Commons CLI or Picocli library
- Define option specifications
- Validate argument combinations
- Provide meaningful error messages

**Validation Rules:**
- Project path must exist and be readable
- Analysis type must be valid
- Output format must be supported
- Numeric arguments must be positive

#### OutputFormatter
**Format Implementations:**
- TextFormatter: Human-readable console output
- JSONFormatter: Machine-parseable JSON
- XMLFormatter: Structured XML
- CSVFormatter: Tabular data for spreadsheets

**Text Output Example:**
```
===== PROJECT ANALYSIS RESULTS =====
Project: example-project
Path: /home/user/projects/example

=== STRUCTURE ===
Total Packages: 15
Total Classes: 87
Total Methods: 423
Total LOC: 12,450

=== TOP 10 CLASSES BY LOC ===
1. UserController (543 LOC)
2. DataService (421 LOC)
3. ValidationEngine (389 LOC)
...

=== COMPLEXITY WARNINGS ===
High complexity methods (>20):
- UserService.processRequest() (CC: 35)
- Parser.parseExpression() (CC: 28)
...
```

**Validation Criteria:**
- Parse all command-line options correctly
- Execute requested analysis type
- Format output in requested format
- Write to file or stdout as specified
- Handle invalid arguments gracefully

**Git Commit:** `feat: implement CLI interface`

---

### Milestone 5.2: Graphical User Interface (Optional)
**Status:** Not Started
**Duration:** 5-7 days (if implemented)

**Components to Implement:**

#### MainWindow
**UI Layout:**
```
+------------------------------------------+
| Menu Bar: File | Analysis | View | Help |
+------------------------------------------+
| Toolbar: [Open] [Run] [Export] [Config] |
+------------------------------------------+
| +------------------+--------------------+
| | Project Tree     | Analysis Results  |
| |                  |                    |
| | - src/           | Metrics Summary    |
| |   - com.example  | ...                |
| |     - model      |                    |
| |     - service    | Top Classes        |
| |     - util       | ...                |
| |                  |                    |
| +------------------+--------------------+
| Status Bar: Ready | Project: xyz       |
+------------------------------------------+
```

#### MetricsPanel
**Display Elements:**
- Summary statistics table
- Metric distribution charts (histograms)
- Trend visualization
- Top/bottom rankings

#### CallGraphPanel
**Visualization:**
- Interactive node-link graph
- Hierarchical tree layout
- Method node details on hover
- Call path highlighting

**Technology Choices:**
- JavaFX for modern UI
- JFreeChart for visualizations
- GraphStream for graph rendering

**Validation Criteria:**
- Load project through file chooser
- Display project structure in tree
- Show analysis results in panels
- Export visualizations as images

**Git Commit:** `feat: implement GUI interface with JavaFX`

---

## PHASE 6: UTILITIES AND POLISH (Week 10)

**Status:** Not Started
**Objective:** Add supporting utilities and improve system robustness

### Milestone 6.1: Utility Classes
**Status:** Not Started
**Duration:** 2-3 days

**Components to Implement:**

#### FileUtils
**Operations:**
- readTextFile(Path): String
- writeTextFile(Path, String): void
- listJavaFiles(Path): List<File>
- getRelativePath(Path, Path): Path
- ensureDirectoryExists(Path): void

#### StringUtils
**Operations:**
- isBlank(String): boolean
- toQualifiedName(String...): String
- extractSimpleName(String): String
- formatNumber(int): String (with thousand separators)
- truncate(String, int): String

#### CollectionUtils
**Operations:**
- partition(List, int): List<List>
- groupBy(List, Function): Map

```
- filterBy(List, Predicate): List
- sortBy(List, Comparator): List
- findFirst(List, Predicate): Optional
```

#### ExportUtils
**Export Formats:**
- exportToJSON(AnalysisResult, Path): void
- exportToXML(AnalysisResult, Path): void
- exportToCSV(List<Metrics>, Path): void
- exportCallGraphDOT(CallGraph, Path): void (Graphviz format)

**JSON Structure Example:**
```json
{
  "project": {
    "name": "example-project",
    "packageCount": 15,
    "classCount": 87,
    "totalLOC": 12450
  },
  "metrics": {
    "avgMethodsPerClass": 4.86,
    "avgLOCPerMethod": 29.43,
    "avgComplexity": 6.2
  },
  "topClasses": [
    {
      "name": "UserController",
      "package": "com.example.web",
      "loc": 543,
      "methods": 28,
      "complexity": 145
    }
  ]
}
```

**Validation Criteria:**
- Read and write files correctly
- Handle file I/O errors gracefully
- Export complete analysis results
- Generate valid JSON/XML/CSV
- Create importable DOT files

**Git Commit:** `feat: add utility classes`

---

### Milestone 6.2: Error Handling and Logging
**Status:** Not Started
**Duration:** 2 days

**Components to Implement:**

#### Custom Exceptions
**Exception Hierarchy:**
```
AnalysisException (base)
    |
    +-- ParsingException: AST parsing failures
    +-- VisitorException: Visitor execution errors
    +-- FileSystemException: File access errors
    +-- ConfigurationException: Invalid configuration
    +-- MetricCalculationException: Metric computation errors
```

**Exception Design:**
- Include error context (file, line number, class)
- Support error chaining (cause)
- Provide actionable error messages
- Enable error recovery when possible

#### Logging Infrastructure
**Logging Configuration:**
- Use SLF4J API with Logback implementation
- Define log levels: TRACE, DEBUG, INFO, WARN, ERROR
- Configure file and console appenders
- Support log rotation and archiving

**Logging Strategy:**
```
Level Guidelines:
- ERROR: System failures, unrecoverable errors
- WARN: Recoverable errors, deprecation warnings
- INFO: Major workflow steps, summary statistics
- DEBUG: Detailed execution flow, decision points
- TRACE: Fine-grained debugging, loop iterations
```

**Log Output Example:**
```
2024-01-15 10:23:15.432 INFO  [main] ProjectExplorer - Starting project exploration
2024-01-15 10:23:15.445 DEBUG [main] ProjectExplorer - Scanning directory: /src/main/java
2024-01-15 10:23:15.523 INFO  [main] ProjectExplorer - Found 87 Java files
2024-01-15 10:23:15.524 INFO  [main] ASTParserFacade - Parsing 87 files
2024-01-15 10:23:16.234 WARN  [main] ASTParserFacade - Parse error in UserService.java:45
2024-01-15 10:23:17.890 INFO  [main] ASTProcessor - Analysis complete in 2.458s
```

**Validation Criteria:**
- All exceptions provide meaningful messages
- Log files created and rotated correctly
- Appropriate log levels used throughout
- Stack traces captured for debugging
- Performance impact minimal

**Git Commit:** `feat: improve error handling and logging`

---

### Milestone 6.3: External Configuration
**Status:** Not Started
**Duration:** 1-2 days

**Components to Implement:**

#### ConfigurationManager
**Configuration Sources:**
- Default built-in configuration
- YAML configuration file (analyzer.yml)
- Java properties file (analyzer.properties)
- Command-line overrides (highest priority)

**Configuration Structure (YAML):**
```yaml
project:
  name: "My Project"
  sourcePaths:
    - "src/main/java"
    - "src/test/java"
  excludePatterns:
    - "**/generated/**"
    - "**/*Test.java"

parsing:
  javaVersion: 17
  resolveBindings: true
  parallelParsing: true
  maxParallelThreads: 4

analysis:
  enabledVisitors:
    - ClassStructure
    - Inheritance
    - CallGraph
    - Complexity
    - Coupling
  
metrics:
  complexityThreshold: 20
  locThreshold: 300
  couplingThreshold: 15

output:
  format: "json"
  destination: "analysis-results.json"
  includeSourceCode: false
  prettyPrint: true

logging:
  level: "INFO"
  file: "analyzer.log"
  maxFileSize: "10MB"
  maxBackupFiles: 5
```

**Configuration Loading:**
```
ConfigurationManager.load():
    1. Load default configuration
    2. Search for analyzer.yml in:
       - Current directory
       - User home directory
       - Classpath
    3. Merge found configuration with defaults
    4. Apply command-line overrides
    5. Validate final configuration
    6. Return Configuration object
```

**Validation Rules:**
- Source paths must exist
- Thresholds must be positive
- Thread count must be reasonable (1-16)
- Output format must be supported
- Log level must be valid

**Validation Criteria:**
- Load configuration from multiple sources
- Apply correct precedence rules
- Validate all configuration values
- Provide helpful error messages for invalid config
- Support configuration hot-reload (optional)

**Git Commit:** `feat: add external configuration support`

---

## PHASE 7: TESTING AND DOCUMENTATION (Weeks 11-12)

**Status:** Not Started
**Objective:** Ensure code quality through comprehensive testing and clear documentation

### Milestone 7.1: Unit Testing
**Status:** Not Started
**Duration:** 5-7 days

**Testing Framework:**
- JUnit 5 (Jupiter) for test execution
- Mockito for mocking dependencies
- AssertJ for fluent assertions
- JaCoCo for code coverage reporting

**Test Categories:**

#### Parser Tests
**Test Cases:**
- Parse valid Java file successfully
- Handle parsing errors gracefully
- Parse file with various Java features (generics, lambdas, annotations)
- Verify CompilationUnit structure
- Test different Java versions (8, 11, 17, 21)

#### Visitor Tests
**Test Cases:**
- ClassStructureVisitor extracts all classes
- InheritanceVisitor captures inheritance correctly
- CallGraphVisitor detects all method calls
- ComplexityVisitor calculates correct cyclomatic complexity
- LinesOfCodeVisitor counts LOC accurately
- CouplingVisitor identifies dependencies

**Test Structure Example:**
```java
@Test
@DisplayName("ClassStructureVisitor should extract all methods from class")
void testMethodExtraction() {
    // Given
    String sourceCode = """
        public class TestClass {
            public void method1() {}
            private int method2(String arg) { return 0; }
        }
    """;
    CompilationUnit cu = parser.parseSource(sourceCode);
    ClassStructureVisitor visitor = new ClassStructureVisitor();
    
    // When
    VisitorResult result = visitor.visit(cu);
    
    // Then
    assertThat(result.getClasses())
        .hasSize(1);
    ClassInfo classInfo = result.getClasses().get(0);
    assertThat(classInfo.getMethods())
        .hasSize(2)
        .extracting(MethodInfo::getName)
        .containsExactlyInAnyOrder("method1", "method2");
}
```

#### Model Tests
**Test Cases:**
- JavaProject maintains correct package hierarchy
- PackageInfo parent-child relationships
- ClassInfo qualified name generation
- MethodInfo signature formatting
- Defensive copying of collections works

#### Processor Tests
**Test Cases:**
- ASTProcessor executes visitors in order
- AnalysisResult aggregates all data
- ProjectExplorer discovers all Java files
- Error collection works correctly
- Pipeline handles partial failures

#### Metrics Tests
**Test Cases:**
- MetricExtractors calculate correct values
- StatisticsCalculator computes accurate statistics
- RankingCalculator sorts correctly
- Threshold filtering works
- Edge cases handled (empty lists, null values)

#### Utility Tests
**Test Cases:**
- FileUtils handles file operations correctly
- StringUtils manipulates strings as expected
- CollectionUtils operations are correct
- ExportUtils generates valid output

**Test Coverage Targets:**
- Overall coverage: Greater than 70%
- Core business logic: Greater than 85%
- Utility classes: Greater than 80%
- Exception paths: Greater than 60%

**Test Organization:**
```
src/test/java/
    ├── parser/
    │   ├── ASTParserFacadeTest.java
    │   └── ParseConfigurationTest.java
    ├── visitors/
    │   ├── base/
    │   │   └── BaseASTVisitorTest.java
    │   ├── structural/
    │   │   ├── ClassStructureVisitorTest.java
    │   │   └── InheritanceVisitorTest.java
    │   └── metrics/
    │       ├── ComplexityVisitorTest.java
    │       └── LinesOfCodeVisitorTest.java
    ├── model/
    │   ├── project/
    │   │   ├── JavaProjectTest.java
    │   │   └── PackageInfoTest.java
    │   └── structural/
    │       └── ClassInfoTest.java
    ├── core/
    │   ├── ASTProcessorTest.java
    │   └── ProjectExplorerTest.java
    └── integration/
        ├── FullAnalysisIntegrationTest.java
        └── RealProjectAnalysisTest.java
```

**Integration Tests:**
- Full pipeline test on sample project
- Performance test on large project (1000+ classes)
- Real-world project analysis (open-source projects)
- Concurrent analysis test
- Error recovery test

**Validation Criteria:**
- All tests pass consistently
- Coverage targets met
- No flaky tests
- Fast test execution (under 5 minutes total)
- Integration tests verify end-to-end functionality

**Git Commit:** `test: add comprehensive unit tests`

---

### Milestone 7.2: Documentation
**Status:** Not Started
**Duration:** 3-4 days

**Documentation Components:**

#### JavaDoc Documentation
**Coverage Requirements:**
- All public classes: Full JavaDoc with class-level description
- All public methods: Parameter descriptions, return values, exceptions
- Complex algorithms: Implementation notes and examples
- Design patterns: Pattern name and rationale

**JavaDoc Standards:**
```java
/**
 * Analyzes Java source code to extract structural information and metrics.
 * 
 * <p>This processor orchestrates the entire analysis workflow including:
 * <ul>
 *   <li>Project exploration and file discovery</li>
 *   <li>AST parsing using Eclipse JDT</li>
 *   <li>Visitor execution for property extraction</li>
 *   <li>Metric calculation and aggregation</li>
 * </ul>
 * 
 * <p><strong>Example Usage:</strong>
 * <pre>{@code
 * ASTProcessor processor = new ASTProcessor();
 * processor.addVisitor(new ClassStructureVisitor());
 * processor.addVisitor(new ComplexityVisitor());
 * AnalysisResult result = processor.processProject("/path/to/project");
 * }</pre>
 * 
 * <p><strong>Thread Safety:</strong> This class is not thread-safe. Each thread
 * should use its own instance.
 * 
 * @see BaseASTVisitor
 * @see AnalysisResult
 * @since 1.0
 * @author Your Name
 */
public class ASTProcessor {
    // ...
}
```

#### README.md
**Sections:**
```markdown
# Java Static Analysis Tool

## Overview
Brief description of the tool's purpose and capabilities.

## Features
- AST-based code parsing
- Structural analysis (classes, methods, inheritance)
- Code metrics (LOC, complexity, coupling)
- Method call graph construction
- Multiple output formats (JSON, XML, CSV)

## Prerequisites
- Java 17 or higher
- Maven 3.8+ or Gradle 7+

## Installation
### From Source
git clone https://github.com/username/ast-analyzer.git
cd ast-analyzer
mvn clean package

### Using Maven Dependency
<dependency>
    <groupId>com.example</groupId>
    <artifactId>ast-analyzer</artifactId>
    <version>1.0.0</version>
</dependency>

## Quick Start
### Command Line
java -jar ast-analyzer.jar --analysis all /path/to/project

### Programmatic Usage
ASTProcessor processor = new ASTProcessor();
AnalysisResult result = processor.processProject("/path/to/project");
System.out.println("Total classes: " + result.getProject().getClassCount());

## Configuration
See analyzer.yml for configuration options.

## Architecture
High-level architecture diagram and component descriptions.

## Contributing
Guidelines for contributions (code style, tests, etc.)

## License
MIT License
```

#### User Guide
**Contents:**
1. Introduction and Motivation
2. Installation Instructions
3. Command-Line Interface Guide
4. Configuration Options
5. Analysis Types and Reports
6. Interpreting Results
7. Troubleshooting Common Issues
8. FAQ

**Example Section:**
```markdown
## Interpreting Metrics

### Cyclomatic Complexity
Cyclomatic complexity measures the number of linearly independent paths
through a method's source code.

Interpretation:
- 1-10: Simple method, low risk
- 11-20: Moderate complexity, moderate risk
- 21-50: Complex method, high risk
- 50+: Very complex, very high risk

Recommendation: Keep methods below 20. Methods exceeding 50 should be
refactored into smaller units.

### Coupling Metrics
Coupling measures the degree of interdependence between classes.

Afferent Coupling (Ca): Number of classes that depend on this class.
High Ca indicates a widely-used class that should be stable.

Efferent Coupling (Ce): Number of classes this class depends on.
High Ce indicates a class with many dependencies, harder to maintain.

Instability (I = Ce / (Ca + Ce)): Ranges from 0 (stable) to 1 (unstable).
- I < 0.3: Stable class, resistant to change
- 0.3 < I < 0.7: Moderately stable
- I > 0.7: Unstable class, frequently changing
```

#### Developer Guide
**Contents:**
1. Architecture Overview
2. Design Patterns Used
3. Class Responsibilities
4. Extension Points
5. Adding New Visitors
6. Adding New Metrics
7. Testing Guidelines
8. Build and Release Process

**Example Section:**
```markdown
## Adding a New Visitor

To add a custom visitor for extracting new properties:

1. Create a class extending BaseASTVisitor:

public class MyCustomVisitor extends BaseASTVisitor {
    @Override
    public String getVisitorName() {
        return "MyCustomVisitor";
    }
    
    @Override
    protected void processCompilationUnit(CompilationUnit cu) {
        // Your extraction logic
    }
}

2. Override visit methods for specific AST node types:

@Override
public boolean visit(MethodDeclaration node) {
    // Extract information from method
    return super.visit(node);
}

3. Store results in VisitorResult:

MyData data = extractData();
result.addCustomData("myData", data);

4. Register visitor with processor:

ASTProcessor processor = new ASTProcessor();
processor.addVisitor(new MyCustomVisitor());
```

#### UML Diagrams
**Diagrams to Create:**
1. Overall System Architecture (component diagram)
2. Core Domain Model (class diagram)
3. Visitor Pattern Implementation (class diagram)
4. Analysis Workflow (sequence diagram)
5. Package Dependencies (package diagram)

**Tool Suggestions:**
- PlantUML for text-based diagrams
- Draw.io for visual editing
- IntelliJ IDEA's built-in UML generator

**Validation Criteria:**
- All public APIs documented with JavaDoc
- README provides clear getting started guide
- User guide covers all major features
- Developer guide enables contributions
- UML diagrams accurately reflect design
- All documentation builds without errors

**Git Commit:** `docs: add comprehensive documentation`

---

### Milestone 7.3: Example Projects and Demos
**Status:** Not Started
**Duration:** 2 days

**Example Projects to Create:**

#### 1. Simple Calculator Project
**Purpose:** Minimal example for testing basic features
**Contents:**
- 3-5 classes (Calculator, Operations, Main)
- Basic inheritance (Operation interface, Add, Subtract, Multiply, Divide)
- Simple method calls
- Around 200 LOC total

**Analysis Demonstrates:**
- Class structure extraction
- Method counting
- Simple inheritance relationships
- Basic metrics calculation

#### 2. Design Pattern Showcase
**Purpose:** Demonstrate pattern detection capabilities
**Contents:**
- Singleton pattern implementation
- Factory pattern implementation
- Observer pattern implementation
- Strategy pattern implementation
- Around 500 LOC total

**Analysis Demonstrates:**
- Complex inheritance hierarchies
- Interface implementations
- Method call graphs with callbacks
- Higher complexity metrics

#### 3. Mini Web Application
**Purpose:** Real-world complexity example
**Contents:**
- Controller layer (5 classes)
- Service layer (8 classes)
- Repository layer (5 classes)
- Model layer (10 classes)
- Utility classes (3 classes)
- Around 2000 LOC total

**Analysis Demonstrates:**
- Package organization
- Layered architecture
- High coupling scenarios
- Realistic complexity distribution

#### Demo Scripts
**Command-Line Demos:**
```bash
# Demo 1: Basic structure analysis
./demo-1-structure.sh
java -jar ast-analyzer.jar --analysis structure examples/calculator

# Demo 2: Metrics analysis with filtering
./demo-2-metrics.sh
java -jar ast-analyzer.jar --analysis metrics --top 5 examples/patterns

# Demo 3: Full analysis with JSON output
./demo-3-full.sh
java -jar ast-analyzer.jar --analysis all --format json --output results.json examples/webapp

# Demo 4: Call graph visualization
./demo-4-callgraph.sh
java -jar ast-analyzer.jar --analysis callgraph --format dot --output graph.dot examples/webapp
dot -Tpng graph.dot -o callgraph.png
```

**Programmatic Demo:**
```java
public class AnalyzerDemo {
    public static void main(String[] args) {
        // Demo: Analyze a project programmatically
        System.out.println("=== AST Analyzer Demo ===\n");
        
        // Step 1: Create processor
        ASTProcessor processor = new ASTProcessor();
        processor.addVisitor(new ClassStructureVisitor());
        processor.addVisitor(new ComplexityVisitor());
        
        // Step 2: Analyze project
        System.out.println("Analyzing project...");
        AnalysisResult result = processor.processProject("examples/calculator");
        
        // Step 3: Display results
        System.out.println("\nResults:");
        System.out.println("Total Classes: " + result.getProject().getClassCount());
        System.out.println("Total Methods: " + result.getClasses().stream()
            .mapToInt(c -> c.getMethods().size()).sum());
        
        // Step 4: Show top complex methods
        System.out.println("\nMost Complex Methods:");
        result.getClasses().stream()
            .flatMap(c -> c.getMethods().stream())
            .sorted(Comparator.comparing(MethodInfo::getComplexity).reversed())
            .limit(5)
            .forEach(m -> System.out.println(
                "  " + m.getName() + ": " + m.getComplexity()
            ));
    }
}
```

**Expected Demo Outputs:**
- Create sample output files showing typical results
- Include screenshots for GUI demo
- Provide annotated examples explaining metrics

**Validation Criteria:**
- All example projects parse without errors
- Demo scripts execute successfully
- Output matches expected results
- Examples cover diverse scenarios
- Documentation references examples

**Git Commit:** `docs: add examples and demo projects`

---

## PHASE 8: BUILD AND DEPLOYMENT (Week 12)

**Status:** Not Started
**Objective:** Finalize build configuration and prepare production release

### Milestone 8.1: Build Configuration
**Status:** Not Started
**Duration:** 2-3 days

**Build Tool Configuration:**

#### Maven POM Structure
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>ast-analyzer</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <jdt.version>3.35.0</jdt.version>
        <junit.version>5.10.0</junit.version>
    </properties>
    
    <dependencies>
        <!-- Eclipse JDT Core -->
        <dependency>
            <groupId>org.eclipse.jdt</groupId>
            <artifactId>org.eclipse.jdt.core</artifactId>
            <version>${jdt.version}</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        
        <!-- Logging -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>
        
        <!-- CLI Parsing -->
        <dependency>
            <groupId>info.picocli</groupId>
            <artifactId>picocli</artifactId>
            <version>4.7.5</version>
        </dependency>
        
        <!-- JSON/XML Processing -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            
            <!-- JAR Plugin with Main Class -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.example.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            
            <!-- Shade Plugin for Uber JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <finalName>ast-analyzer</finalName>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.example.Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            
            <!-- Test Coverage -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.10</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
            <!-- JavaDoc Generation -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Launch Scripts
**Unix/Linux Script (analyzer.sh):**
```bash
#!/bin/bash
JAVA_OPTS="-Xmx2g -Xms512m"
JAR_FILE="ast-analyzer.jar"
java $JAVA_OPTS -jar $JAR_FILE "$@"
```

**Windows Script (analyzer.bat):**
```batch
@echo off
set JAVA_OPTS=-Xmx2g -Xms512m
set JAR_FILE=ast-analyzer.jar
java %JAVA_OPTS% -jar %JAR_FILE% %*
```

**Build Profiles:**
- development: Fast builds, no optimization
- testing: Full tests, coverage reports
- production: Optimized, stripped debug symbols

**Validation Criteria:**
- Project builds successfully with Maven
- Executable JAR created with all dependencies
- Launch scripts work on Windows and Unix
- Tests execute during build
- Coverage reports generated
- JavaDoc builds without warnings

**Git Commit:** `build: finalize build configuration and packaging`

---

### Milestone 8.2: Release Preparation
**Status:** Not Started
**Duration:** 1 day

**Release Checklist:**

#### Version Management
- Update version numbers in POM
- Tag release in Git: `git tag -a v1.0.0 -m "Release 1.0.0"`
- Update CHANGELOG.md with release notes

#### Release Artifacts
- Compile final JAR: `mvn clean package`
- Generate JavaDoc: `mvn javadoc:jar`
- Create source archive: `mvn source:jar`
- Generate checksums (MD5, SHA256)

#### Release Notes
```markdown
# Release 1.0.0 (2024-01-15)

## Features
- Complete AST parsing for Java 17
- Structural analysis (classes, methods, fields, inheritance)
- Code metrics (LOC, complexity, coupling)
- Method call graph construction
- Multiple output formats (text, JSON, XML, CSV)
- Command-line interface
- Configurable analysis pipeline

## Metrics Supported
- Lines of Code (physical and logical)
- Cyclomatic Complexity (McCabe)
- Afferent/Efferent Coupling
- Class/Method counts
- Package-level aggregations

## Known Limitations
- No support for Kotlin or other JVM languages
- Limited lambda expression analysis
- No incremental analysis mode

## Requirements
- Java 17 or higher
- 2GB RAM minimum (4GB recommended for large projects)

## Installation
Download ast-analyzer.jar and run:
java -jar ast-analyzer.jar --help
```

#### Distribution Package
**Contents:**
```
ast-analyzer-1.0.0/
├── ast-analyzer.jar
├── README.md
├── LICENSE
├── CHANGELOG.md
├── examples/
│   ├── calculator/
│   ├── patterns/
│   └── webapp/
├── docs/
│   ├── user-guide.md
│   ├── developer-guide.md
│   └── api/  (JavaDoc)
├── scripts/
│   ├── analyzer.sh
│   └── analyzer.bat
└── config/
    └── analyzer.yml (sample configuration)
```

**Validation Criteria:**
- All release artifacts generated
- Version numbers consistent across files
- Distribution package complete
- Installation instructions verified
- Release notes accurate and complete

**Git Commit:** `release: version 1.0.0`

---

## DEPENDENCY GRAPH

```
PHASE 1: Foundations
    |
    +-- Parsing Infrastructure (no dependencies)
    +-- Domain Model (no dependencies)
    +-- Visitor Infrastructure (depends on: Domain Model)
    +-- ClassStructureVisitor (depends on: Visitor Infrastructure, Domain Model)
        |
        v
PHASE 2: Project Structure
    |
    +-- JavaProject & PackageInfo (depends on: Domain Model)
    +-- ProjectExplorer (depends on: JavaProject, PackageInfo)
    +-- Parsing Integration (depends on: Parsing Infrastructure, ProjectExplorer, JavaProject)
    +-- Additional Visitors (depends on: Visitor Infrastructure, Domain Model)
        |
        v
PHASE 3: Orchestration
    |
    +-- ASTProcessor (depends on: All Phase 1 & 2 components)
    +-- AnalysisResult (depends on: JavaProject, Domain Model)
        |
        v
PHASE 4: Advanced Analysis
    |
    +-- Call Graph (depends on: Visitor Infrastructure, Domain Model, ASTProcessor)
    +-- Metrics (depends on: Visitor Infrastructure, Domain Model, ASTProcessor)
    +-- Extractors (depends on: AnalysisResult, Metrics)
    +-- Statistics (depends on: Metrics, Extractors)
        |
        v
PHASE 5: User Interfaces
    |
    +-- CLI (depends on: ASTProcessor, AnalysisResult)
    +-- GUI (depends on: ASTProcessor, AnalysisResult, CLI)
        |
        v
PHASE 6: Utilities
    |
    +-- Utility Classes (minimal dependencies)
    +-- Error Handling (cross-cutting)
    +-- Configuration (minimal dependencies)
        |
        v
PHASE 7: Testing & Documentation
    |
    +-- Unit Tests (tests all components)
    +-- Documentation (documents all components)
    +-- Examples (uses all components)
        |
        v
PHASE 8: Build & Release
    |
    +-- Build Configuration (integrates everything)
    +-- Release Preparation (finalizes everything)
```

---

## CURRENT STATUS SUMMARY

**Completed Milestones:**
- Phase 1, Milestone 1.1: AST Parsing Infrastructure
- Phase 1, Milestone 1.2: Structural Domain Model
- Phase 1, Milestone 1.3: Visitor Infrastructure
- Phase 1, Milestone 1.4: ClassStructureVisitor

**In Progress:**
- Phase 2, Milestone 2.1: JavaProject and PackageInfo (40% complete)

**Next Immediate Steps:**
1. Complete JavaProject implementation with all methods
2. Enrich PackageInfo with hierarchical navigation
3. Implement ProjectExplorer for filesystem discovery
4. Integrate parsing with project structure
5. Add InheritanceVisitor and PackageStructureVisitor

**Estimated Completion:**
- Current phase (Phase 2): 1-2 weeks
- Remaining project: 7-10 weeks
- Total: 8-12 weeks from start

---

## SUCCESS CRITERIA

**Technical Criteria:**
- Parse 1000+ class projects in under 2 minutes
- Achieve greater than 70% test coverage
- Zero critical bugs in core functionality
- Support Java versions 8 through 21
- Handle projects up to 100k LOC

**Functional Criteria:**
- Extract complete class structure
- Calculate all defined metrics accurately
- Build correct call graphs
- Generate multiple output formats
- Provide usable CLI and optional GUI

**Quality Criteria:**
- All public APIs documented
- Clean, maintainable code following SOLID principles
- Comprehensive test suite
- Clear error messages
- Responsive performance

---

## RISK MITIGATION

**Technical Risks:**
- Eclipse JDT complexity: Mitigated by ASTParserFacade abstraction
- Performance on large projects: Mitigated by parallel parsing, caching
- Memory consumption: Mitigated by streaming where possible, configuration options

**Schedule Risks:**
- Scope creep: Mitigated by phased approach, clear milestone definitions
- Complexity underestimation: Mitigated by 20% time buffer in estimates
- Blocking dependencies: Mitigated by clear dependency graph, parallel work where possible

**Quality Risks:**
- Insufficient testing: Mitigated by test-first approach, coverage targets
- Poor documentation: Mitigated by continuous documentation, JavaDoc requirements
- Technical debt: Mitigated by regular refactoring, code review checkpoints

---

## PERFORMANCE TARGETS

**Parsing Performance:**
- Small project (10-50 classes): Under 5 seconds
- Medium project (50-500 classes): Under 30 seconds
- Large project (500-2000 classes): Under 2 minutes
- Very large project (2000-10000 classes): Under 10 minutes

**Memory Usage:**
- Small project: Under 512 MB
- Medium project: Under 1 GB
- Large project: Under 2 GB
- Very large project: Under 4 GB

**Analysis Performance:**
- Visitor execution: Under 1 second per 100 classes
- Metrics calculation: Under 5 seconds total for medium project
- Call graph construction: Under 30 seconds for 1000 methods
- Statistics aggregation: Near-instant (under 1 second)

**Optimization Strategies:**
- Use fastConfig for syntax-only parsing when binding resolution unnecessary
- Enable parallel parsing for projects with 100+ files
- Implement lazy loading for CompilationUnit cache
- Use indexed lookups (Maps) instead of linear searches
- Cache frequently accessed calculations
- Stream processing for large collections

---

## EXTENSIBILITY POINTS

**Future Enhancement Opportunities:**

### Additional Visitors
- **DesignPatternDetector:** Identify common design patterns automatically
- **CodeSmellDetector:** Detect anti-patterns and code smells
- **SecurityAnalyzer:** Find potential security vulnerabilities
- **PerformanceAnalyzer:** Identify performance bottlenecks
- **DocumentationCoverageVisitor:** Measure JavaDoc completeness

### Additional Metrics
- **Maintainability Index:** Composite metric combining LOC, complexity, and coupling
- **Technical Debt Ratio:** Estimate of refactoring effort needed
- **LCOM (Lack of Cohesion of Methods):** Class cohesion measurement
- **Response for Class (RFC):** Number of methods that can execute in response to message
- **Weighted Methods per Class (WMC):** Sum of complexities of all methods

### Additional Output Formats
- **HTML Report:** Interactive web-based report with charts
- **PDF Report:** Professional printable reports
- **Markdown:** Documentation-friendly format
- **SQL:** Database import for trend analysis
- **Prometheus Metrics:** Integration with monitoring systems

### Integration Capabilities
- **Maven Plugin:** Run analysis as part of Maven build
- **Gradle Plugin:** Integration with Gradle builds
- **IDE Plugin:** Eclipse/IntelliJ IDEA integration
- **CI/CD Integration:** Jenkins, GitLab CI, GitHub Actions
- **SonarQube Integration:** Export to SonarQube format
- **Quality Gate:** Fail builds based on metric thresholds

### Advanced Features
- **Incremental Analysis:** Only re-analyze changed files
- **Historical Tracking:** Track metrics over time across versions
- **Comparison Mode:** Compare two versions of same project
- **Batch Analysis:** Analyze multiple projects in single run
- **Distributed Processing:** Analyze very large codebases across multiple nodes
- **Machine Learning Integration:** Predict defect-prone classes

---

## ALTERNATIVE IMPLEMENTATIONS

**If Time Constrained, Consider:**

### Minimal Viable Product (4-6 weeks)
**Includes:**
- Phase 1: Foundations (complete)
- Phase 2: Project Structure (basic implementation)
- Phase 3: Orchestration (simplified)
- Phase 4: Only LOC and basic complexity metrics
- Phase 5: CLI only (no GUI)
- Phase 6: Minimal utilities
- Phase 7: Basic tests only
- Phase 8: Simple JAR packaging

**Excludes:**
- Call graph analysis
- Advanced coupling metrics
- GUI interface
- External configuration
- Comprehensive documentation

### Extended Implementation (16-20 weeks)
**Additional Features:**
- Machine learning-based defect prediction
- Real-time analysis in IDE
- Web-based dashboard with historical trends
- Multi-language support (Kotlin, Scala)
- Code transformation suggestions
- Automated refactoring recommendations
- Integration with issue tracking systems

---

## TECHNICAL DEBT MANAGEMENT

**Planned Technical Debt (Acceptable):**
- Simplified error handling in Phase 1-2 (refactored in Phase 6)
- Basic configuration in early phases (enhanced in Phase 6)
- Minimal documentation during development (completed in Phase 7)
- Performance optimization deferred until core features complete

**Unacceptable Technical Debt:**
- Skipping unit tests
- Ignoring SOLID principles
- Hard-coding configuration values
- Copying code instead of refactoring
- Leaving commented-out code
- Missing JavaDoc on public APIs

**Debt Tracking:**
- Use TODO comments with ticket references
- Maintain TECHNICAL_DEBT.md file
- Review and address debt before phase completion
- Allocate 15% of Phase 6 time to debt reduction

---

## CODE QUALITY STANDARDS

**Coding Conventions:**
- Follow Java naming conventions strictly
- Maximum method length: 50 lines
- Maximum class length: 500 lines
- Maximum cyclomatic complexity: 15
- Maximum parameter count: 5
- No magic numbers (use named constants)

**Documentation Standards:**
- All public classes require JavaDoc
- All public methods require parameter and return documentation
- Complex algorithms require implementation notes
- All exceptions thrown must be documented
- Examples for non-obvious usage

**Testing Standards:**
- Each public method requires at least one test
- Edge cases and error paths must be tested
- Integration tests for complete workflows
- Performance tests for critical paths
- No test should depend on another test

**Review Checklist:**
- SOLID principles followed
- No code duplication
- Error handling appropriate
- Tests comprehensive
- Documentation complete
- Performance acceptable

---

## VERSION CONTROL STRATEGY

**Branch Structure:**
```
main (protected)
    |
    +-- develop (integration branch)
        |
        +-- feature/phase-1-parsing
        +-- feature/phase-2-project-structure
        +-- feature/phase-3-orchestration
        +-- feature/phase-4-call-graph
        +-- feature/phase-4-metrics
        +-- feature/phase-5-cli
        +-- feature/phase-5-gui
        +-- bugfix/issue-123
        +-- docs/user-guide
```

**Commit Message Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- feat: New feature
- fix: Bug fix
- docs: Documentation changes
- test: Test additions or modifications
- refactor: Code refactoring
- perf: Performance improvements
- build: Build system changes
- ci: CI/CD changes

**Example:**
```
feat(visitors): add InheritanceVisitor for class hierarchy extraction

Implement visitor that extracts class inheritance relationships including
super classes and implemented interfaces. Supports generic type parameters
and handles both class and interface inheritance.

Resolves: #42
Related: #38, #41
```

**Milestone Tags:**
- v0.1.0-milestone-1.1: AST Parsing Infrastructure
- v0.2.0-milestone-1.4: Initial Visitor Implementation
- v0.5.0-milestone-3.1: Core Processor Complete
- v0.8.0-milestone-4.4: All Analysis Features Complete
- v1.0.0: Production Release

---

## CONTINUOUS INTEGRATION PIPELINE

**Build Stages:**

### Stage 1: Validation (2-3 minutes)
- Compile source code
- Run static analysis (Checkstyle, PMD, SpotBugs)
- Verify code formatting
- Check dependency vulnerabilities

### Stage 2: Testing (5-8 minutes)
- Run unit tests
- Generate coverage report
- Enforce coverage thresholds
- Run integration tests

### Stage 3: Analysis (2-3 minutes)
- Generate JavaDoc
- Run SonarQube analysis
- Check for code smells
- Verify technical debt limits

### Stage 4: Packaging (1-2 minutes)
- Build executable JAR
- Generate checksums
- Create distribution archive
- Upload artifacts

### Stage 5: Deployment (conditional)
- Deploy to staging environment
- Run smoke tests
- Deploy to production (manual approval)

**Quality Gates:**
- All tests pass
- Coverage greater than 70%
- No critical security vulnerabilities
- No blocker or critical code smells
- Technical debt ratio under 5%

---

## MONITORING AND METRICS

**Development Metrics to Track:**
- Lines of code written per phase
- Test coverage percentage
- Number of bugs found in testing
- Number of bugs found post-milestone
- Time spent per milestone vs estimate
- Code review feedback volume
- Refactoring frequency

**Application Metrics to Monitor:**
- Parsing time per file
- Memory consumption during analysis
- Visitor execution time
- Analysis success rate
- Error frequency by type
- User-reported issues

**Quality Metrics:**
- Code coverage trend
- Cyclomatic complexity distribution
- Technical debt ratio
- Documentation completeness
- API stability (breaking changes)

---

## LESSONS LEARNED DOCUMENTATION

**After Each Phase:**
- Document what went well
- Document what could be improved
- Note unexpected challenges
- Record solutions to difficult problems
- Update estimates for remaining work

**Template:**
```markdown
# Phase X Retrospective

## What Went Well
- Clear milestone definitions helped focus work
- Comprehensive testing caught bugs early

## What Could Be Improved
- Underestimated complexity of call graph resolution
- Should have created more example test cases earlier

## Technical Challenges
- Challenge: Resolving overloaded methods with generics
- Solution: Implemented type parameter substitution algorithm
- Reference: JLS Section 15.12.2.5

## Time Analysis
- Estimated: 5 days
- Actual: 7 days
- Variance: +40%
- Reason: Unexpected edge cases in lambda handling

## Recommendations for Next Phase
- Allocate more time for complex algorithm implementation
- Create comprehensive test cases before implementation
- Research JLS specifications thoroughly upfront
```

---

## COMMUNICATION PLAN

**Stakeholder Updates:**
- Weekly status reports (progress, blockers, next steps)
- Milestone completion announcements
- Risk and issue escalations as needed
- Demo sessions at phase completions

**Documentation Updates:**
- Update README.md with each feature addition
- Maintain CHANGELOG.md for all notable changes
- Keep API documentation current
- Update roadmap progress indicators

**Community Engagement (if open source):**
- Respond to GitHub issues within 48 hours
- Review pull requests within 1 week
- Publish release notes for each version
- Maintain active discussion forum or Discord

---

## PROJECT COMPLETION CRITERIA

**Functional Completeness:**
- All Phase 1-5 milestones completed
- All planned visitors implemented
- All planned metrics calculated
- CLI fully functional
- Export formats working

**Quality Completeness:**
- Greater than 70% test coverage achieved
- All public APIs documented
- User guide and developer guide complete
- Zero critical or high-priority bugs
- Performance targets met

**Deliverable Completeness:**
- Executable JAR built and tested
- Distribution package assembled
- Documentation published
- Examples and demos verified
- Release notes finalized

**Acceptance Criteria:**
- Successfully analyze 3 real-world open-source projects
- Generate accurate metrics matching manual verification
- Complete analysis pipeline executes without errors
- Export formats parse correctly in target tools
- Users can successfully follow installation guide

---

## POST-RELEASE ACTIVITIES

**Immediate Post-Release (Week 13):**
- Monitor for critical bugs
- Respond to user questions
- Create FAQ based on common issues
- Prepare patch release if needed

**Maintenance Phase (Ongoing):**
- Bug fixes for reported issues
- Performance optimizations
- Documentation improvements
- Dependency updates

**Future Enhancements (Version 2.0+):**
- Implement features from extensibility roadmap
- Add requested community features
- Expand language support
- Enhance visualization capabilities
- Improve performance for very large projects

---

## RESOURCE REQUIREMENTS

**Development Resources:**
- Primary developer time: 8-12 weeks full-time
- Code review: 4-6 hours per week
- Testing: Integrated into development time
- Documentation: 1-2 hours per day

**Infrastructure Resources:**
- Development machine: 16GB RAM, multi-core processor
- CI/CD server: GitHub Actions (free tier) or Jenkins
- Source control: GitHub (free for public, paid for private)
- Documentation hosting: GitHub Pages or Read the Docs

**Third-Party Dependencies:**
- Eclipse JDT Core (EPL/LGPL licensed)
- JUnit 5 (EPL licensed)
- SLF4J/Logback (MIT/EPL licensed)
- Jackson (Apache 2.0 licensed)
- Picocli (Apache 2.0 licensed)

**Optional Resources:**
- GUI framework: JavaFX (GPL with classpath exception)
- Graph visualization: GraphStream (LGPL/CeCILL)
- Static analysis: SonarQube (LGPL)

---

## FINAL RECOMMENDATIONS

**For Success:**
1. **Start with solid foundations** - Don't rush Phase 1
2. **Test continuously** - Write tests alongside code, not after
3. **Document as you go** - JavaDoc while context is fresh
4. **Commit frequently** - Small, focused commits with clear messages
5. **Refactor proactively** - Address technical debt before it compounds
6. **Seek feedback early** - Get code reviews, user feedback on demos
7. **Maintain flexibility** - Adjust roadmap based on discoveries
8. **Celebrate milestones** - Acknowledge progress at each phase completion

**Red Flags to Watch For:**
- Test coverage declining
- Commit messages becoming vague
- Classes growing beyond 500 lines
- Copy-paste code appearing
- Documentation falling behind
- Technical debt accumulating
- Performance degrading
- Scope expanding uncontrollably

**Success Indicators:**
- Steady progress through milestones
- High test coverage maintained
- Clean, understandable code
- Comprehensive documentation
- Positive user feedback
- Performance targets met
- Schedule adherence

---

## CONCLUSION

This roadmap provides a comprehensive, professional path from current state to production release. The phased approach ensures:

- **Incremental value delivery** through working milestones
- **Risk mitigation** through early validation of core concepts
- **Quality assurance** through integrated testing and documentation
- **Maintainability** through SOLID principles and clean architecture
- **Extensibility** through well-defined interfaces and patterns
- **Usability** through multiple interfaces and clear documentation

The 8-12 week timeline is realistic for a dedicated developer following this structured approach. Adjust timelines based on available hours per week and complexity discoveries during implementation.

**Current Status:** Phase 2, Milestone 2.1 (40% complete)  
**Next Immediate Action:** Complete JavaProject implementation with full method set  
**Estimated Completion:** 7-10 weeks remaining

This roadmap is a living document and should be updated as the project progresses to reflect actual progress, lessons learned, and necessary adjustments.