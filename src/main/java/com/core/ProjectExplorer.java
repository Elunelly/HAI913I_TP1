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
import java.util.regex.Pattern;

import com.model.project.JavaProject;
import com.model.project.PackageInfo;

public class ProjectExplorer {

	private final List<Pattern> excludedPatterns = new ArrayList<>();
	private final List<String> excludedDirectories = new ArrayList<>();
	private int maxDepth = Integer.MAX_VALUE;
	private Path currentProjectRootPath;
	private final Map<String,List<File>> currentProjectGroupedFilesByPackage = new HashMap<>();
	private final List<ExplorationError> currentProjectErrors = new ArrayList<>();
	private long currentProjectTimeStart;
	private long currentProjectTimeEnd;

	public ProjectExplorer() {
		setupExcludedPatterns();
		setupExcludedDirectories();
	}
	
	public ProjectExplorer(int maxDepth) {
		setupExcludedPatterns();
		setupExcludedDirectories();
		setMaxDepth(maxDepth);
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
		excludedPatterns.add(Pattern.compile(pattern));
	}

	public void addExcludedDirectory(String directoryName) {
		excludedDirectories.add(directoryName);
	}

	public void clearExcludedPatterns() {
		excludedPatterns.clear();
	}

	public void clearExcludedDirectories() {
		excludedDirectories.clear();
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = Math.max(0,maxDepth+1);
	}

	public List<File> exploreDirectory(Path rootPath) throws IOException {
		if (rootPath == null)
			throw new IllegalArgumentException("Root path cannot be null");
		if (!Files.exists(rootPath))
			throw new IOException("Path does not exist: " + rootPath);
		if (!Files.isDirectory(rootPath))
			throw new IOException("Path is not a directory: " + rootPath);
		this.currentProjectTimeStart = System.currentTimeMillis();
		this.currentProjectRootPath = findSourceRoot(rootPath);
		clearCurrentProjectErrors();
		clearCurrentProjectFilesByPackage();

		List<File> result = new ArrayList<>();
		Files.walkFileTree(rootPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), maxDepth, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				if (excludedDirectories.contains(dir.getFileName().toString()))
					return FileVisitResult.SKIP_SUBTREE;
				// Avoid self-cycling symbolicLink
				if (Files.isSymbolicLink(dir)) {
				    try {
				        Path target = Files.readSymbolicLink(dir);
				        if (dir.startsWith(target)) {
				            return FileVisitResult.SKIP_SUBTREE;
				        }
				    } catch (IOException ignored) {}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path filename, BasicFileAttributes attrs) {
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
				if (exception != null)
					currentProjectErrors.add(new ExplorationError(dir, "Error visiting directory", exception));
				return FileVisitResult.CONTINUE;
			}
		});

		this.currentProjectTimeEnd = System.currentTimeMillis();
		return result;
	}

	public List<File> exploreDirectory() throws IOException {
		return exploreDirectory(currentProjectRootPath);
	}

	public JavaProject buildJavaProject(String projectName, Path rootPath) throws IOException {
		exploreDirectory(rootPath);
		JavaProject project = new JavaProject(projectName, currentProjectRootPath);

		for (Map.Entry<String, List<File>> entry : currentProjectGroupedFilesByPackage.entrySet()) {
			String packageName = entry.getKey();
			PackageInfo packageInfo = new PackageInfo(packageName);
			project.addPackage(packageInfo);
		}

		project.buildPackagesHierarchy();
		return project;
	}
	
	private void addFileToGroup(File javaFile) {
		if (javaFile == null) return;
		String packageName = inferPackageName(javaFile.toPath());
		currentProjectGroupedFilesByPackage.computeIfAbsent(packageName, k -> new ArrayList<>()).add(javaFile);
	}

	private String inferPackageName(Path filePath, Path rootPath) {
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
	
	private String inferPackageName(Path filePath) {
		return inferPackageName(filePath, currentProjectRootPath);
	}

	private Path findSourceRoot(Path rootPath) {
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

	private boolean isJavaFile(Path filePath) {
		return filePath.toString().endsWith(".java");
	}

	private boolean isExcluded(Path filePath) {
		String fileName = filePath.getFileName().toString();

		for (Pattern pattern : excludedPatterns) {
			if (pattern.matcher(fileName).matches()) {
				return true;
			}
		}

		return false;
	}
	
	public Map<String,List<File>> getFilesByPackage() {return Collections.unmodifiableMap(currentProjectGroupedFilesByPackage);}

	private void clearCurrentProjectFilesByPackage() {
		currentProjectGroupedFilesByPackage.clear();
	}
	
	public List<ExplorationError> getCurrentProjectErrors() {
		return new ArrayList<>(currentProjectErrors);
	}

	public boolean currentProjectHasErrors() {
		return !currentProjectErrors.isEmpty();
	}

	private void clearCurrentProjectErrors() {
		currentProjectErrors.clear();
	}
	
	public long getEllapsedTimeOfExploration() {
		return currentProjectTimeEnd - currentProjectTimeStart;
	}

	public String getStatisticsOnCurrentProject() {
		int totalFiles = currentProjectGroupedFilesByPackage.values().stream().mapToInt(List::size).sum();
		int keyLength = 20;
		StringBuilder stats = new StringBuilder();
		stats.append("=== Exploration Statistics ===");
		stats.append(String.format("\n%-"+keyLength+"s: %dms", "Ellapsed time", getEllapsedTimeOfExploration()));
		stats.append(String.format("\n%-"+keyLength+"s: %d", "Total packages", currentProjectGroupedFilesByPackage.size()));
		stats.append(String.format("\n%-"+keyLength+"s: %d", "Total Java files", totalFiles));
		stats.append(String.format("\n%-"+keyLength+"s: %d", "Errors encountered", currentProjectErrors.size()));
		return stats.toString();
	}
}