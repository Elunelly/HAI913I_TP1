package com.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.calculators.StatisticsCalculator;
import com.config.ParseConfiguration;
import com.exceptions.ASTError;
import com.extractors.MetricExtractor;
import com.model.project.ProjectInfo;
import com.parser.ASTParserFacade;
import com.utils.table.TableUI;
import com.visitors.base.BaseASTVisitor;
import com.visitors.base.VisitorResult;
import com.visitors.structural.ClassStructureVisitor;

public class ASTProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTProcessor.class);
	
	private final List<BaseASTVisitor> visitors = new ArrayList<>();
	private final List<MetricExtractor<?>> extractors = new ArrayList<>();
	private ASTParserFacade parserFacade;
	private ProjectExplorer explorer;
	private StatisticsCalculator calculator;
	private final List<ASTError> errors = new ArrayList<>();
	
	public ASTProcessor() {
		this(null, null, null);
	}
	
	public ASTProcessor(ProjectExplorer explorer, ASTParserFacade parserFacade, StatisticsCalculator calculator) {
		setASTParserFacade(parserFacade);
		setProjectExplorer(explorer);
		setCalculator(calculator);
	}
	
	public void reset() {
		clearVisitors();
		clearExtractors();
		clearErrors();
	}
	
	public List<BaseASTVisitor> getVisitors() {return Collections.unmodifiableList(visitors);}
	
	public List<BaseASTVisitor> copyVisitors() {return new ArrayList<>(visitors);}
	
	public void addVisitor(BaseASTVisitor visitor) {
		if (visitor!=null && !visitors.contains(visitor) && visitors.add(visitor))
			logger.debug("Visitor added: %s".formatted(visitor.getResult()));
	}
	
	public boolean removeVisitor(BaseASTVisitor visitor) {
		boolean result = visitors.remove(visitor);
		if (result)
			logger.debug("Visitor removed: %s".formatted(visitor.getResult()));
		return result;
	}
	
	private void clearVisitors() {
		visitors.clear();
		logger.debug("Cleared visitors List");
	}
	
	public List<MetricExtractor<?>> getExtractors() {return Collections.unmodifiableList(extractors);}
	
	public List<MetricExtractor<?>> copyExtractors() {return new ArrayList<>(extractors);}
	
	public void addExtractor(MetricExtractor<?> extractor) {
		if (extractor!=null && !extractors.contains(extractor) && extractors.add(extractor))
			logger.debug("Extractor added: %s".formatted(extractor));
	}
	
	public boolean removeExtractor(MetricExtractor<?> extractor) {
		boolean result = extractors.remove(extractor);
		if (result)
			logger.debug("Extractor removed: %s".formatted(extractor));
		return result;
	}
	
	private void clearExtractors() {
		extractors.clear();
		logger.debug("Cleared extractors List");
	}
	
	public ASTParserFacade getASTParserFacade() {return parserFacade;}
	
	private void setASTParserFacade(ASTParserFacade parserFacade) {
		ASTParserFacade old = getASTParserFacade();
		this.parserFacade = parserFacade != null ? parserFacade : ASTParserFacade.createDefault();
		logger.debug("Change value of 'parserFacade': %s -> %s".formatted(old,this.parserFacade));
	}
	
	public ProjectExplorer getProjectExplorer() {return explorer;}
	
	private void setProjectExplorer(ProjectExplorer explorer) {
		ProjectExplorer old = getProjectExplorer();
		this.explorer = explorer != null ? explorer : new ProjectExplorer().withDefaultExclusions();
		logger.debug("Change value of 'explorer': %s -> %s".formatted(old,this.explorer));
	}
	
	public StatisticsCalculator getCalculator() {return calculator;}
	
	private void setCalculator(StatisticsCalculator calculator) {
		StatisticsCalculator old = getCalculator();
		this.calculator = calculator != null ? calculator : new StatisticsCalculator();
		logger.debug("Change value of 'calculator': %s -> %s".formatted(old,this.calculator));
	}
	
	public List<ASTError> getErrors() {return Collections.unmodifiableList(errors);}
	
	public List<ASTError> copyErrors() {return new ArrayList<>(errors);}
	
	public void addError(ASTError error) {
		if (errors.add(error))
			logger.debug("Error added: %s".formatted(error));
	}
	
	private void clearErrors() {
		errors.clear();
		logger.debug("Cleared errors List");
	}
	
	public AnalysisResult processProject(String projectName, Path rootPath) {
		clearErrors();
		
		ParseConfiguration config = parserFacade.getConfig();

		try {
			rootPath = explorer.setupCurrentProject(rootPath);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		// STEP 0: Configuration
		logger.info(
			TableUI.titledTable(
				"STARTING PROJECT ANALYSIS",
				List.of(
					new String[] {"Project Name", projectName},
					new String[] {"Starting at", rootPath.toString()}
				),
				":"
			)+
			TableUI.tableTitle("WITH PARSING CONFIGURATION AS BELOW",2)+
			TableUI.tableDataRows(
				List.of(
					new String[] {"JRE's version", config.getJLStoString()},
					new String[] {"Resolve Bindings", String.valueOf(config.isResolveBindings())},
					new String[] {"Default Unit name", config.getDefaultUnitName()},
					new String[] {"Class Paths", config.getClassPaths().toString()},
					new String[] {"Source Paths", config.getSourcePaths().toString()}
				),
				":",
				2
			)
		);
		// STEP 1: Exploration
		List<File> javaFiles = new ArrayList<>();
		try {
			logger.info("[1/5] Exploring project directories...");
			javaFiles = explorer.exploreDirectory(rootPath);
			logger.info("End of exploration, see results below:\n"+explorer.getStatisticsOnCurrentProject());
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.debug(e.getStackTrace().toString());
			return null;
		}
		
		// STEP 2: Building JavaProject
		ProjectInfo project = null;
		try {
			logger.info("[2/5] Building project hierarchy...");
			project = explorer.buildJavaProject(projectName, rootPath, javaFiles);
			logger.debug(project.toString());
			logger.info("End of building");
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.debug(e.getStackTrace().toString());
			return null;
		}
		
		// STEP 3: Parsing all 
		logger.info("[3/5] Parsing all Java files...");
		List<ASTError> parsingErrors = new ArrayList<>();
		Map<String,CompilationUnit> compilationUnits = parserFacade.parseFiles(parsingErrors, javaFiles);
		logger.debug(project.copyMappedPackages().toString());
		project.buildCompilationUnitsAssociation(compilationUnits);
		logger.debug(project.toString());
		project.getPackages().stream()
			.forEach(p -> logger.debug("Package: {}\nUnits: {}",p.getName(),p.getMappedUnits()));
		logger.info("End of parsing files, successfully parsed "+compilationUnits.size()+"/"+javaFiles.size());
		
		// STEP 4: Visitor Execution
		logger.info("[4/5] Executing all visitors...");
		executeVisitors(project);
		AnalysisResult result = new AnalysisResult(project);
		logger.debug(result.toString());
		logger.info("End of visiting");
		
		// STEP 5: Metric Extraction
		logger.info("[5/5] Extracting all metrics...");
		calculateMetrics(result);
		logger.info("End of calculations");
		
		return result;
	}
	
	public AnalysisResult processProject(String rootPath) {
		Path path = Path.of(rootPath);
		String projectName = path.getFileName().toString();
		return processProject(projectName, path);
	}
	
	private void executeVisitors(ProjectInfo project) {
        if (visitors.isEmpty()) {
        	logger.warn("No visitors registered, using default ClassStructureVisitor");
            ClassStructureVisitor defaultVisitor = new ClassStructureVisitor();
            executeVisitor(defaultVisitor, project);
            return;
        }
        
        for (BaseASTVisitor visitor : visitors) {
            executeVisitor(visitor, project);
        }
	}
	
	private void calculateMetrics(AnalysisResult result) {
		// TODO
	}
	
    private Collection<VisitorResult> executeVisitor(BaseASTVisitor visitor, ProjectInfo project) {
		try {
			logger.debug("Executing visitor: "+visitor.getVisitorName());
			return visitor.visit(project);
			
		} catch (Exception e) {
			logger.error("Error executing visitor '"+visitor.getVisitorName()+"': "+visitor.getResult().getErrorMessage());
			return Collections.emptyList();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ASTProcessor that = (ASTProcessor) obj;
		return 
			Objects.equals(this.parserFacade, that.parserFacade) &&
			Objects.equals(this.explorer, that.explorer) &&
			Objects.equals(this.calculator, that.calculator) &&
			Objects.equals(this.visitors.size(), that.visitors.size()) &&
			Objects.equals(this.extractors.size(), that.extractors.size())
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(parserFacade, explorer, calculator, visitors, extractors);
	}
    
    @Override
    public String toString() {
    	return (this.getClass().getSimpleName()+"{"
    			+ "explorer=%s, "
    			+ "parser=%s, "
    			+ "calculator=%s, "
    			+ "visitors=%d, "
    			+ "extractors=%d, "
    			+ "errors=%d}")
    			.formatted(explorer,parserFacade,calculator,visitors.size(),extractors.size(),errors.size());
    }
    
}