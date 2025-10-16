package com.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.core.exceptions.ExplorationError;
import com.model.project.ProjectInfo;
import com.utils.table.TableUI;

public class ProjectExplorer {
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectExplorer.class);

	private final List<String> excludedPatterns = new ArrayList<>();
	private final List<String> excludedDirectories = new ArrayList<>();
	private int maxDepth = Integer.MAX_VALUE;
	private Path currentProjectRootPath;
	private final Map<String,List<File>> currentProjectGroupedFilesByPackage = new HashMap<>();
	private final List<ExplorationError> currentProjectErrors = new ArrayList<>();
	private long currentProjectTimeStart;
	private long currentProjectTimeEnd;

	public ProjectExplorer() {
	}
	
	public ProjectExplorer(int maxDepth) {
		setMaxDepth(maxDepth);
	}
	
	public ProjectExplorer withDefaultExcludedPatterns() {
		setupExcludedPatterns();
		return this;
	}
	
	public ProjectExplorer withDefaultExcludedDirectories() {
		setupExcludedDirectories();
		return this;
	}
	
	public ProjectExplorer withDefaultExclusions() {
		setupExcludedPatterns();
		setupExcludedDirectories();
		return this;
	}
	
	public ProjectExplorer withExcludedPatterns(String... patterns) {
		for (String pattern : patterns) {
			addExcludedPattern(pattern);
		}
		return this;
	}
	
	public ProjectExplorer withExcludedDirectories(String... directoriesName) {
		for (String directoryName : directoriesName) {
			addExcludedDirectory(directoryName);
		}
		return this;
	}
	
	private void setupExcludedPatterns() {
		addExcludedPattern(".*Test\\.java");
		addExcludedPattern(".*Tests\\.java");
	}
	
	private void setupExcludedDirectories() {
		addExcludedDirectory("target");
		addExcludedDirectory("build");
		addExcludedDirectory("bin");
		addExcludedDirectory(".git");
		addExcludedDirectory(".svn");
	}

	public void addExcludedPattern(String pattern) {
		if (pattern!=null && !excludedPatterns.contains(pattern)) {
			excludedPatterns.add(pattern);
			logger.debug("Added excluded pattern: "+pattern);
		}
	}

	public void addExcludedDirectory(String directoryName) {
		directoryName = directoryName.toLowerCase();
		if (directoryName!=null && !excludedDirectories.contains(directoryName)) {
			excludedDirectories.add(directoryName);
			logger.debug("Added excluded directory: "+directoryName);
		}
	}

	public void clearExcludedPatterns() {
		excludedPatterns.clear();
		logger.debug("Cleared all excluded patterns");
	}

	public void clearExcludedDirectories() {
		excludedDirectories.clear();
		logger.debug("Cleared all excluded directories");
	}

	public void setMaxDepth(int maxDepth) {
		int old = this.maxDepth;
		this.maxDepth = Math.max(0,maxDepth+1);
		logger.debug("Change value of 'maxDepth': %s -> %s".formatted(old,this.maxDepth));
	}
	
	public void setupCurrentProject(Path rootPath) throws IOException {
		if (rootPath == null)
			throw new IllegalArgumentException("Root path cannot be null");
		if (!Files.exists(rootPath))
			throw new IOException("Path does not exist: " + rootPath);
		if (!Files.isDirectory(rootPath))
			throw new IOException("Path is not a directory: " + rootPath);
		
		clearCurrentProjectErrors();
		clearCurrentProjectFilesByPackage();
		Path old = this.currentProjectRootPath;
		this.currentProjectRootPath = findSourceRoot(rootPath);
		logger.debug("Change value of 'currentProjectRootPath': %s -> %s".formatted(old,this.currentProjectRootPath));
	}
	
	public void startCurrentProjectTimer() {
		this.currentProjectTimeStart = System.currentTimeMillis();
		logger.debug("Change starting timer on project "+currentProjectRootPath);
	}
	
	public void stopCurrentProjectTimer() {
		this.currentProjectTimeEnd = System.currentTimeMillis();
		logger.debug("Change stoping timer on project "+currentProjectRootPath);
	}

	public List<File> exploreDirectory(Path rootPath) throws IOException {
		startCurrentProjectTimer();
		setupCurrentProject(rootPath);

		List<File> result = new ArrayList<>();
		Files.walkFileTree(currentProjectRootPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), maxDepth, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				logger.trace("Exploring directory: "+dir);
				if (excludedDirectories.contains(dir.getFileName().toString().toLowerCase())) {
					logger.trace("This directory is excluded, skipping next...");
					return FileVisitResult.SKIP_SUBTREE;
				}
				// Avoid self-cycling symbolicLink
				if (Files.isSymbolicLink(dir)) {
				    try {
				        Path target = Files.readSymbolicLink(dir);
				        if (dir.startsWith(target)) {
				        	logger.trace("The symbolic link is cycling on itself, skipping next...");
				            return FileVisitResult.SKIP_SUBTREE;
				        }
				    } catch (IOException ignored) {}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path filename, BasicFileAttributes attrs) {
				logger.trace("Checking file: "+filename);
				if (isJavaFile(filename) && !isExcluded(filename)) {
					File file = filename.toFile();
					result.add(file);
					addFileToGroup(file);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path filename, IOException exception) {
				currentProjectErrors.add(new ExplorationError(filename, "Cannot access file", exception));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exception) {
				if (exception != null) {
					currentProjectErrors.add(new ExplorationError(dir, "Error visiting directory", exception));
				}
				return FileVisitResult.CONTINUE;
			}
		});

		stopCurrentProjectTimer();
		return result;
	}

	public List<File> exploreDirectory() throws IOException {
		return exploreDirectory(currentProjectRootPath);
	}

	public ProjectInfo buildJavaProject(String projectName, Path rootPath, List<File> javaFiles) throws IOException {
		if (javaFiles == null)
			exploreDirectory(rootPath);
		else
			groupAllFilesByPackage(javaFiles);
		return new ProjectInfo(projectName, currentProjectRootPath, currentProjectGroupedFilesByPackage);
	}
	
	public ProjectInfo buildJavaProject(String projectName, Path rootPath) throws IOException {
		return buildJavaProject(projectName, rootPath, null);
	}
	
	private void groupAllFilesByPackage(List<File> files) {
		clearCurrentProjectFilesByPackage();
		for (File file : files) {
			Path filePath = file.toPath();
			if (!isJavaFile(filePath) || isExcluded(filePath)) continue;
			addFileToGroup(file);
		}
		stopCurrentProjectTimer();
	}
	
	private void addFileToGroup(File javaFile) {
		if (javaFile == null) return;
		String packageName = inferPackageName(javaFile.toPath());
		currentProjectGroupedFilesByPackage.computeIfAbsent(packageName, k -> new ArrayList<>()).add(javaFile);
		logger.debug("Added file on '"+packageName+"': "+javaFile.getName());
	}

	public String inferPackageName(Path filePath, Path rootPath) {
		try {
			Path relativePath = rootPath.relativize(filePath);
			Path parentPath = relativePath.getParent();

			if (parentPath == null)
				return "";

			return parentPath.toString().replace(File.separator, ".");

		} catch (IllegalArgumentException e) {
			currentProjectErrors.add(new ExplorationError(filePath, "Cannot determine package name", e));
			return "";
		}
	}
	
	protected String inferPackageName(Path filePath) {
		return inferPackageName(filePath, currentProjectRootPath);
	}

	public Path findSourceRoot(Path rootPath) {
		Path mavenSrc = rootPath.resolve("src/main/java");
		if (Files.exists(mavenSrc) && Files.isDirectory(mavenSrc)) {
			return mavenSrc;
		}

		Path simpleSrc = rootPath.resolve("src");
		if (Files.exists(simpleSrc) && Files.isDirectory(simpleSrc)) {
			return simpleSrc;
		}

		return rootPath;
	}

	public boolean isJavaFile(Path filePath) {
		return filePath.toString().endsWith(".java");
	}

	public boolean isExcluded(Path filePath) {
		String fileName = filePath.getFileName().toString();

		for (String pattern : excludedPatterns) {
			if (pattern.matches(fileName)) {
				return true;
			}
		}

		return false;
	}
	
	public Map<String,List<File>> getFilesByPackage() {return Collections.unmodifiableMap(currentProjectGroupedFilesByPackage);}

	private void clearCurrentProjectFilesByPackage() {
		currentProjectGroupedFilesByPackage.clear();
		logger.debug("Cleared all grouped files by package");
	}
	
	public List<ExplorationError> getCurrentProjectErrors() {
		return new ArrayList<>(currentProjectErrors);
	}

	public boolean currentProjectHasErrors() {
		return !currentProjectErrors.isEmpty();
	}

	private void clearCurrentProjectErrors() {
		currentProjectErrors.clear();
		logger.debug("Cleared all current project errors trace");
	}
	
	public long getEllapsedTimeOfExploration() {
		return currentProjectTimeEnd - currentProjectTimeStart;
	}

	public String getStatisticsOnCurrentProject() {
		int totalFiles = currentProjectGroupedFilesByPackage.values().stream().mapToInt(List::size).sum();
		return TableUI.titledTable(
			currentProjectRootPath.toString(),
			List.of(
				new String[] {"Ellapsed time", getEllapsedTimeOfExploration()+"ms"},
				new String[] {"Total packages", String.valueOf(currentProjectGroupedFilesByPackage.size())},
				new String[] {"Total Java files", String.valueOf(totalFiles)},
				new String[] {"Errors encountered", String.valueOf(currentProjectErrors.size())}
			),
			":"
		);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		ProjectExplorer that = (ProjectExplorer) obj;
		return 
			Objects.equals(this.excludedPatterns, that.excludedPatterns) &&
			Objects.equals(this.excludedDirectories, that.excludedDirectories) &&
			Objects.equals(this.maxDepth, that.maxDepth)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(excludedPatterns, excludedDirectories, maxDepth);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "excludedPatterns=%s, "
				+ "excludedDirectories=%s, "
				+ "maxDepth=%d}")
				.formatted(excludedPatterns,excludedDirectories,maxDepth);
	}
	
}