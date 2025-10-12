package com.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.calculators.StatisticsCalculator;
import com.exceptions.ASTError;
import com.extractors.MetricExtractor;
import com.model.project.JavaProject;
import com.model.structural.ClassInfo;
import com.parser.ASTParserFacade;
import com.parser.ParseConfiguration;
import com.utils.table.TableUI;
import com.visitors.base.BaseASTVisitor;
import com.visitors.base.VisitorResult;
import com.visitors.structural.ClassStructureVisitor;

public class ASTProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTProcessor.class);
	
	private final List<BaseASTVisitor> visitors = new ArrayList<>();
	private final List<MetricExtractor> extractors = new ArrayList<>();
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
	
	public List<MetricExtractor> getExtractors() {return Collections.unmodifiableList(extractors);}
	
	public List<MetricExtractor> copyExtractors() {return new ArrayList<>(extractors);}
	
	public void addExtractor(MetricExtractor extractor) {
		if (extractor!=null && !extractors.contains(extractor) && extractors.add(extractor))
			logger.debug("Extractor added: %s".formatted(extractor));
	}
	
	public boolean removeExtractor(MetricExtractor extractor) {
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
		
		// STEP 0: Configuration
		/*
		 * int keyPadding = 18;
		logger.info((
			"STARTING PROJECT ANALYSIS"+
			"\n  -» %-"+keyPadding+"s: %s"+
			"\n  -» %-"+keyPadding+"s: %s"
			).formatted("Project Name",projectName,"Starting at", rootPath));
			*/
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
		}
		
		// STEP 2: Building JavaProject
		JavaProject project = null;
		try {
			logger.info("[2/5] Building project hierarchy...");
			project = explorer.buildJavaProject(projectName, rootPath, javaFiles);
			logger.info("End of building");
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.debug(e.getStackTrace().toString());
		}
		
		// STEP 3: Parsing all 
		logger.info("[3/5] Parsing all Java files...");
		List<ASTError> parsingErrors = new ArrayList<>();
		List<CompilationUnit> compilationUnits = parserFacade.parseFiles(parsingErrors, javaFiles);
		logger.info("End of parsing files, successfully parsed "+compilationUnits.size()+"/"+javaFiles.size());
		
		// STEP 4: Visitor Execution
		logger.info("[4/5] Executing all visitors...");
		executeVisitors(project, compilationUnits);
		AnalysisResult result = new AnalysisResult(project);
		logger.info("End of visiting");
		
		// STEP 5: Metric Extraction
		logger.info("[5/5] Extracting all metrics...");
		logger.info("End of calculations");
		
		return result;
	}
	
	public AnalysisResult processProject(String rootPath) {
		Path path = Path.of(rootPath);
		String projectName = path.getFileName().toString();
		return processProject(projectName, path);
	}
	
	private void executeVisitors(JavaProject project, List<CompilationUnit> compilationUnits) {
        if (visitors.isEmpty()) {
        	logger.warn("No visitors registered, using default ClassStructureVisitor");
            ClassStructureVisitor defaultVisitor = new ClassStructureVisitor();
            executeVisitor(defaultVisitor, project, compilationUnits);
            return;
        }
        
        for (BaseASTVisitor visitor : visitors) {
            executeVisitor(visitor, project, compilationUnits);
        }
	}
	
	private void calculateMetrics(AnalysisResult result) {
		// TODO
	}
	
    @SuppressWarnings("unchecked")
	private void executeVisitor(BaseASTVisitor visitor, JavaProject project, List<CompilationUnit> compilationUnits) {
		try {
			logger.debug("Executing visitor: "+visitor.getVisitorName());
			for (CompilationUnit cu : compilationUnits) {
				visitor.visitAndExtract(cu);
			}
			VisitorResult result = visitor.getResult();
			if (result.containsKey("classes")) {
				List<ClassInfo> extractedClasses = (List<ClassInfo>) result.getDataBy("classes");
				if (extractedClasses != null) {
					for (ClassInfo classInfo : extractedClasses) {
					   project.addClass(classInfo);
					}
					logger.debug("Successfully extracted "+extractedClasses.size()+" class(es)");
				}
			}
			
			project.addAllCompilationUnits(compilationUnits);
		} catch (Exception e) {
			logger.error("Error executing visitor '"+visitor.getVisitorName()+"': "+visitor.getResult().getErrorMessage());
		}
	}
    
    @Override
    public String toString() {
    	return ("ASTProcessor{"
    			+ "explorer=%s, "
    			+ "parser=%s, "
    			+ "calculator=%s, "
    			+ "visitors=%d, "
    			+ "extractors=%d, "
    			+ "errors=%d}")
    			.formatted(explorer,parserFacade,calculator,visitors.size(),extractors.size(),errors.size());
    }
    
}