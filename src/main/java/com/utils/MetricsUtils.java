package com.utils;

import java.util.List;

import com.model.project.PackageInfo;
import com.model.project.ProjectInfo;
import com.model.structural.ClassInfo;
import com.model.structural.NodeModifiers;
import com.model.structural.NodeVisibility;

public final class MetricsUtils {
	
/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||  BASIC MATHEMATIC OPERATION  ||||||||||||||||||||||||||||||
    +-----------------------------------------------------------------------------------------+    
*/
	
	public static double average(List<Double> values) {
		return values.stream().mapToDouble(v -> v).sum() / values.size();
	}
	
	public static double average(double total, double count) {
		return count>0 ? total / count : 0.0;
	}
	
	public static double ratio(double count, double total) {
		return total>0 ? count / total : 0.0;
	}
	
/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||||  PACKAGES COUNTING  |||||||||||||||||||||||||||||||||||
    +-----------------------------------------------------------------------------------------+    
*/

	//			====== Packages ======			//
	public static int countPackages(ProjectInfo projectInfo) {
		return projectInfo.getPackages().size();
	}

	//			====== PackagesByDepthEquals ======			//
	public static int countPackagesByDepthEquals(ProjectInfo projectInfo, int depth) {
		return (int) projectInfo.getPackages().stream().filter(p -> p.getDepth()==depth).count();
	}

	//			====== PackagesByDepthLessThan ======			//
	public static int countPackagesByDepthLessThan(ProjectInfo projectInfo, int depth) {
		return (int) projectInfo.getPackages().stream().filter(p -> p.getDepth()<depth).count();
	}

	//			====== PackagesByDepthMoreThan ======			//
	public static int countPackagesByDepthMoreThan(ProjectInfo projectInfo, int depth) {
		return (int) projectInfo.getPackages().stream().filter(p -> p.getDepth()>depth).count();
	}

	//			====== LeafPackages ======			//
	public static int countLeafPackages(ProjectInfo projectInfo) {
		return (int) projectInfo.getPackages().stream().filter(p -> !p.hasSubPackages()).count();
	}

	//			====== BranchPackages ======			//
	public static int countBranchPackages(ProjectInfo projectInfo) {
		return (int) projectInfo.getPackages().stream().filter(p -> p.hasSubPackages()).count();
	}

	//			====== EmptyPackages ======			//
	public static int countEmptyPackages(ProjectInfo projectInfo) {
		return (int) projectInfo.getPackages().stream().filter(p -> !p.hasClasses()).count();
	}

	//			====== NonEmptyPackages ======			//
	public static int countNonEmptyPackages(ProjectInfo projectInfo) {
		return (int) projectInfo.getPackages().stream().filter(p -> p.hasClasses()).count();
	}

	//			====== SiblingsPackages ======			//
	public static int countSiblingsPackages(PackageInfo packageInfo) {
		return packageInfo.isRoot() ? 0 : packageInfo.getParentPackage().getSubPackages().size() - 1;
	}
	
/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||||||  CLASSES COUNTING  |||||||||||||||||||||||||||||||||||
    +-----------------------------------------------------------------------------------------+    
*/

	//			====== Classes ======			//
	public static int countClasses(ProjectInfo projectInfo) {
		return projectInfo.getClasses().size();
	}

	public static int countClasses(PackageInfo packageInfo) {
		return packageInfo.getClasses().size();
	}
	
	public static int sumClassesCount(List<PackageInfo> packages) {
		return packages.stream().mapToInt(c -> countClasses(c)).sum();
	}
	
	public static int countAllRecursiveClasses(PackageInfo packageInfo) {
		return packageInfo.getAllClassesRecursive().size();
	}
	
	//			====== ClassesByVisibility ======			//
	public static int countClassesByVisibility(ProjectInfo projectInfo, NodeVisibility visibility) {
		return (int) projectInfo.getClasses().stream().filter(c -> c.getVisibility()==visibility).count();
	}

	public static int countClassesByVisibility(PackageInfo packageInfo, NodeVisibility visibility) {
		return (int) packageInfo.getClasses().stream().filter(c -> c.getVisibility()==visibility).count();
	}
	
	public static int sumClassesByVisibilityCount(List<PackageInfo> packages, NodeVisibility visibility) {
		return packages.stream().mapToInt(c -> countClassesByVisibility(c, visibility)).sum();
	}

	public static int countAllRecursiveClassesByVisibility(PackageInfo packageInfo, NodeVisibility visibility) {
		return (int) packageInfo.getAllClassesRecursive().stream().filter(c -> c.getVisibility()==visibility).count();
	}
	
	//			====== AbstractClasses ======			//
	public static int countAbstractClasses(ProjectInfo projectInfo) {
		return (int) projectInfo.getClasses().stream().filter(c -> c.isAbstract()).count();
	}

	public static int countAbstractClasses(PackageInfo packageInfo) {
		return (int) packageInfo.getClasses().stream().filter(c -> c.isAbstract()).count();
	}
	
	public static int sumAbstractClassesCount(List<PackageInfo> packages) {
		return packages.stream().mapToInt(c -> countAbstractClasses(c)).sum();
	}

	public static int countAllRecursiveAbstractClasses(PackageInfo packageInfo) {
		return (int) packageInfo.getAllClassesRecursive().stream().filter(c -> c.isAbstract()).count();
	}
	
	//			====== Interfaces ======			//
	public static int countInterfaces(ProjectInfo projectInfo) {
		return (int) projectInfo.getClasses().stream().filter(c -> c.isInterface()).count();
	}

	public static int countInterfaces(PackageInfo packageInfo) {
		return (int) packageInfo.getClasses().stream().filter(c -> c.isInterface()).count();
	}
	
	public static int sumAInterfacesCount(List<PackageInfo> packages) {
		return packages.stream().mapToInt(c -> countInterfaces(c)).sum();
	}

	public static int countAllRecursiveInterfaces(PackageInfo packageInfo) {
		return (int) packageInfo.getAllClassesRecursive().stream().filter(c -> c.isInterface()).count();
	}

	//			====== ClassesByModifier ======			//
	public static int countClassesByModifier(ProjectInfo projectInfo, NodeModifiers modifier) {
		return (int) projectInfo.getClasses().stream().filter(m -> m.getModifiers().contains(modifier)).count();
	}

	public static int countClassesByModifier(PackageInfo packageInfo, NodeModifiers modifier) {
		return (int) packageInfo.getClasses().stream().filter(m -> m.getModifiers().contains(modifier)).count();
	}

	public static int sumClassesByModifierCount(List<PackageInfo> packages, NodeModifiers modifier) {
		return packages.stream().mapToInt(c -> countClassesByModifier(c, modifier)).sum();
	}

	public static int countAllRecursiveClassesByModifier(PackageInfo packageInfo, NodeModifiers modifier) {
		return (int) packageInfo.getAllClassesRecursive().stream().filter(m -> m.getModifiers().contains(modifier)).count();
	}

	//			====== ClassesByAllModifiers ======			//
	public static int countClassesByAllModifiers(ProjectInfo projectInfo, List<NodeModifiers> modifiers) {
		return (int) projectInfo.getClasses().stream().filter(m -> m.getModifiers().containsAll(modifiers)).count();
	}

	public static int countClassesByAllModifiers(PackageInfo packageInfo, List<NodeModifiers> modifiers) {
		return (int) packageInfo.getClasses().stream().filter(m -> m.getModifiers().containsAll(modifiers)).count();
	}

	public static int sumClassesByAllModifiersCount(List<PackageInfo> packages, List<NodeModifiers> modifiers) {
		return packages.stream().mapToInt(c -> countClassesByAllModifiers(c, modifiers)).sum();
	}

	public static int countAllRecursiveClassesByAllModifiers(PackageInfo packageInfo, List<NodeModifiers> modifiers) {
		return (int) packageInfo.getAllClassesRecursive().stream().filter(m -> m.getModifiers().containsAll(modifiers)).count();
	}

	//			====== ClassesByNameMatching ======			//
	public static int countClassesByNameMatching(ProjectInfo projectInfo, String pattern) {
		return (int) projectInfo.getClasses().stream().filter(m -> m.getName().matches(pattern)).count();
	}

	public static int countClassesByNameMatching(PackageInfo packageInfo, String pattern) {
		return (int) packageInfo.getClasses().stream().filter(m -> m.getName().matches(pattern)).count();
	}

	public static int sumClassesByNameMatchingCount(List<PackageInfo> packages, String pattern) {
		return packages.stream().mapToInt(c -> countClassesByNameMatching(c, pattern)).sum();
	}

	public static int countAllRecursiveClassesByNameMatching(PackageInfo packageInfo, String pattern) {
		return (int) packageInfo.getAllClassesRecursive().stream().filter(m -> m.getName().matches(pattern)).count();
	}

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||||||  METHODS COUNTING  |||||||||||||||||||||||||||||||||||
    +-----------------------------------------------------------------------------------------+    
*/

	//			====== Methods ======			//
	public static int countMethods(ProjectInfo projectInfo) {
		return sumMethodsCount(projectInfo.getClasses());
	}
	
	public static int countMethods(PackageInfo packageInfo) {
		return sumMethodsCount(packageInfo.getClasses());
	}
	
	public static int sumMethodsCountFromPackages(List<PackageInfo> packages) {
		return packages.stream().mapToInt(p -> countMethods(p)).sum();
	}
	
	public static int countMethods(ClassInfo classInfo) {
		return classInfo.getMethods().size();
	}
	
	public static int sumMethodsCount(List<ClassInfo> classes) {
		return classes.stream().mapToInt(c -> countMethods(c)).sum();
	}

	//			====== MethodsByVisibility ======			//
	public static int countMethodsByVisibility(ProjectInfo projectInfo, NodeVisibility visibility) {
		return sumMethodsByVisibilityCount(projectInfo.getClasses(), visibility);
	}
	
	public static int countMethodsByVisibility(PackageInfo packageInfo, NodeVisibility visibility) {
		return sumMethodsByVisibilityCount(packageInfo.getClasses(), visibility);
	}
	
	public static int sumMethodsByVisibilityCountFromPackages(List<PackageInfo> packages, NodeVisibility visibility) {
		return packages.stream().mapToInt(p -> countMethodsByVisibility(p, visibility)).sum();
	}
	
	public static int countMethodsByVisibility(ClassInfo classInfo, NodeVisibility visibility) {
		return classInfo.getMethodsByVisibility(visibility).size();
	}
	
	public static int sumMethodsByVisibilityCount(List<ClassInfo> classes, NodeVisibility visibility) {
		return classes.stream().mapToInt(c -> countMethodsByVisibility(c, visibility)).sum();
	}

	//			====== PublicMethods ======			//
	public static int countPublicMethods(ProjectInfo projectInfo) {
		return sumPublicMethodsCount(projectInfo.getClasses());
	}
	
	public static int countPublicMethods(PackageInfo packageInfo) {
		return sumPublicMethodsCount(packageInfo.getClasses());
	}
	
	public static int sumPublicMethodsCountFromPackages(List<PackageInfo> packages) {
		return packages.stream().mapToInt(p -> countPublicMethods(p)).sum();
	}
	
	public static int countPublicMethods(ClassInfo classInfo) {
		return classInfo.getPublicMethods().size();
	}
	
	public static int sumPublicMethodsCount(List<ClassInfo> classes) {
		return classes.stream().mapToInt(c -> countPublicMethods(c)).sum();
	}

	//			====== MethodsByModifier ======			//
	public static int countMethodsByModifier(ProjectInfo projectInfo, NodeModifiers modifier) {
		return sumMethodsByModifierCount(projectInfo.getClasses(), modifier);
	}
	
	public static int countMethodsByModifier(PackageInfo packageInfo, NodeModifiers modifier) {
		return sumMethodsByModifierCount(packageInfo.getClasses(), modifier);
	}
	
	public static int sumMethodsByModifierCountFromPackages(List<PackageInfo> packages, NodeModifiers modifier) {
		return packages.stream().mapToInt(p -> countMethodsByModifier(p, modifier)).sum();
	}
	
	public static int countMethodsByModifier(ClassInfo classInfo, NodeModifiers modifier) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getModifiers().contains(modifier)).count();
	}

	public static int sumMethodsByModifierCount(List<ClassInfo> classes, NodeModifiers modifier) {
		return classes.stream().mapToInt(c -> countMethodsByModifier(c, modifier)).sum();
	}

	//			====== MethodsByAllModifiers ======			//
	public static int countMethodsByAllModifiers(ProjectInfo projectInfo, List<NodeModifiers> modifiers) {
		return sumMethodsByAllModifiersCount(projectInfo.getClasses(), modifiers);
	}
	
	public static int countMethodsByAllModifiers(PackageInfo packageInfo, List<NodeModifiers> modifiers) {
		return sumMethodsByAllModifiersCount(packageInfo.getClasses(), modifiers);
	}
	
	public static int sumMethodsByAllModifiersCountFromPackages(List<PackageInfo> packages, List<NodeModifiers> modifiers) {
		return packages.stream().mapToInt(p -> countMethodsByAllModifiers(p, modifiers)).sum();
	}
	
	public static int countMethodsByAllModifiers(ClassInfo classInfo, List<NodeModifiers> modifiers) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getModifiers().containsAll(modifiers)).count();
	}

	public static int sumMethodsByAllModifiersCount(List<ClassInfo> classes, List<NodeModifiers> modifiers) {
		return classes.stream().mapToInt(c -> countMethodsByAllModifiers(c, modifiers)).sum();
	}

	//			====== MethodsByReturnTypeMatching ======			//
	public static int countMethodsByReturnTypeMatching(ProjectInfo projectInfo, String pattern) {
		return sumMethodsByReturnTypeMatchingCount(projectInfo.getClasses(), pattern);
	}
	
	public static int countMethodsByReturnTypeMatching(PackageInfo packageInfo, String pattern) {
		return sumMethodsByReturnTypeMatchingCount(packageInfo.getClasses(), pattern);
	}
	
	public static int sumMethodsByReturnTypeMatchingCountFromPackages(List<PackageInfo> packages, String pattern) {
		return packages.stream().mapToInt(p -> countMethodsByReturnTypeMatching(p, pattern)).sum();
	}
	
	public static int countMethodsByReturnTypeMatching(ClassInfo classInfo, String pattern) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getReturnType().matches(pattern)).count();
	}
	
	public static int sumMethodsByReturnTypeMatchingCount(List<ClassInfo> classes, String pattern) {
		return classes.stream().mapToInt(c -> countMethodsByReturnTypeMatching(c, pattern)).sum();
	}

	//			====== MethodsByNameMatching ======			//
	public static int countMethodsByNameMatching(ProjectInfo projectInfo, String pattern) {
		return sumMethodsByNameMatchingCount(projectInfo.getClasses(), pattern);
	}
	
	public static int countMethodsByNameMatching(PackageInfo packageInfo, String pattern) {
		return sumMethodsByNameMatchingCount(packageInfo.getClasses(), pattern);
	}
	
	public static int sumMethodsByNameMatchingCountFromPackages(List<PackageInfo> packages, String pattern) {
		return packages.stream().mapToInt(p -> countMethodsByNameMatching(p, pattern)).sum();
	}
	
	public static int countMethodsByNameMatching(ClassInfo classInfo, String pattern) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getName().matches(pattern)).count();
	}

	public static int sumMethodsByNameMatchingCount(List<ClassInfo> classes, String pattern) {
		return classes.stream().mapToInt(c -> countMethodsByNameMatching(c, pattern)).sum();
	}

	//			====== MethodsByParametersEquals ======			//
	public static int countMethodsByParametersEquals(ProjectInfo projectInfo, int parameters) {
		return sumMethodsByParametersEqualsCount(projectInfo.getClasses(), parameters);
	}
	
	public static int countMethodsByParametersEquals(PackageInfo packageInfo, int parameters) {
		return sumMethodsByParametersEqualsCount(packageInfo.getClasses(), parameters);
	}
	
	public static int sumMethodsByParametersEqualsCountFromPackages(List<PackageInfo> packages, int parameters) {
		return packages.stream().mapToInt(p -> countMethodsByParametersEquals(p, parameters)).sum();
	}
	
	public static int countMethodsByParametersEquals(ClassInfo classInfo, int parameters) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getParameters().size()==parameters).count();
	}
	
	public static int sumMethodsByParametersEqualsCount(List<ClassInfo> classes, int parameters) {
		return classes.stream().mapToInt(c -> countMethodsByParametersEquals(c, parameters)).sum();
	}

	//			====== MethodsByParametersLessThan ======			//
	public static int countMethodsByParametersLessThan(ProjectInfo projectInfo, int parameters) {
		return sumMethodsByParametersLessThanCount(projectInfo.getClasses(), parameters);
	}
	
	public static int countMethodsByParametersLessThan(PackageInfo packageInfo, int parameters) {
		return sumMethodsByParametersLessThanCount(packageInfo.getClasses(), parameters);
	}
	
	public static int sumMethodsByParametersLessThanCountFromPackages(List<PackageInfo> packages, int parameters) {
		return packages.stream().mapToInt(p -> countMethodsByParametersLessThan(p, parameters)).sum();
	}
	
	public static int countMethodsByParametersLessThan(ClassInfo classInfo, int parameters) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getParameters().size()<parameters).count();
	}

	public static int sumMethodsByParametersLessThanCount(List<ClassInfo> classes, int parameters) {
		return classes.stream().mapToInt(c -> countMethodsByParametersLessThan(c, parameters)).sum();
	}

	//			====== MethodsByParametersMoreThan ======			//
	public static int countMethodsByParametersMoreThan(ProjectInfo projectInfo, int parameters) {
		return sumMethodsByParametersMoreThanCount(projectInfo.getClasses(), parameters);
	}
	
	public static int countMethodsByParametersMoreThan(PackageInfo packageInfo, int parameters) {
		return sumMethodsByParametersMoreThanCount(packageInfo.getClasses(), parameters);
	}
	
	public static int sumMethodsByParametersMoreThanCountFromPackages(List<PackageInfo> packages, int parameters) {
		return packages.stream().mapToInt(p -> countMethodsByParametersMoreThan(p, parameters)).sum();
	}
	
	public static int countMethodsByParametersMoreThan(ClassInfo classInfo, int parameters) {
		return (int) classInfo.getMethods().stream().filter(m -> m.getParameters().size()>parameters).count();
	}

	public static int sumMethodsByParametersMoreThanCount(List<ClassInfo> classes, int parameters) {
		return classes.stream().mapToInt(c -> countMethodsByParametersMoreThan(c, parameters)).sum();
	}
	
/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||||||  FIELDS COUNTING  ||||||||||||||||||||||||||||||||||||
    +-----------------------------------------------------------------------------------------+    
*/

	//			====== Fields ======			//
	public static int countFields(ProjectInfo projectInfo) {
		return sumFieldsCount(projectInfo.getClasses());
	}
	
	public static int countFields(PackageInfo packageInfo) {
		return sumFieldsCount(packageInfo.getClasses());
	}
	
	public static int sumFieldsCountFromPackages(List<PackageInfo> packages) {
		return packages.stream().mapToInt(p -> countFields(p)).sum();
	}
	
	public static int countFields(ClassInfo classInfo) {
		return classInfo.getFields().size();
	}
	
	public static int sumFieldsCount(List<ClassInfo> classes) {
		return classes.stream().mapToInt(c -> countFields(c)).sum();
	}

	//			====== FieldsByVisibility ======			//
	public static int countFieldsByVisibility(ProjectInfo projectInfo, NodeVisibility visibility) {
		return sumFieldsByVisibilityCount(projectInfo.getClasses(), visibility);
	}
	
	public static int countFieldsByVisibility(PackageInfo packageInfo, NodeVisibility visibility) {
		return sumFieldsByVisibilityCount(packageInfo.getClasses(), visibility);
	}
	
	public static int sumFieldsByVisibilityCountFromPackages(List<PackageInfo> packages, NodeVisibility visibility) {
		return packages.stream().mapToInt(p -> countFieldsByVisibility(p, visibility)).sum();
	}
	
	public static int countFieldsByVisibility(ClassInfo classInfo, NodeVisibility visibility) {
		return (int) classInfo.getFields().stream().filter(f -> f.getVisibility() == visibility).count();
	}

	public static int sumFieldsByVisibilityCount(List<ClassInfo> classes, NodeVisibility visibility) {
		return classes.stream().mapToInt(c -> countFieldsByVisibility(c, visibility)).sum();
	}

	//			====== ConstantFields ======			//
	public static int countConstantFields(ProjectInfo projectInfo) {
		return sumConstantFieldsCount(projectInfo.getClasses());
	}
	
	public static int countConstantFields(PackageInfo packageInfo) {
		return sumConstantFieldsCount(packageInfo.getClasses());
	}
	
	public static int sumConstantFieldsCountFromPackages(List<PackageInfo> packages) {
		return packages.stream().mapToInt(p -> countConstantFields(p)).sum();
	}
	
	public static int countConstantFields(ClassInfo classInfo) {
		return (int) classInfo.getFields().stream().filter(f -> f.isConstant()).count();
	}

	public static int sumConstantFieldsCount(List<ClassInfo> classes) {
		return classes.stream().mapToInt(c -> countConstantFields(c)).sum();
	}

	//			====== FieldsByModifier ======			//
	public static int countFieldsByModifier(ProjectInfo projectInfo, NodeModifiers modifier) {
		return sumFieldsByModifierCount(projectInfo.getClasses(), modifier);
	}
	
	public static int countFieldsByModifier(PackageInfo packageInfo, NodeModifiers modifier) {
		return sumFieldsByModifierCount(packageInfo.getClasses(), modifier);
	}
	
	public static int sumFieldsByModifierCountFromPackages(List<PackageInfo> packages, NodeModifiers modifier) {
		return packages.stream().mapToInt(p -> countFieldsByModifier(p, modifier)).sum();
	}
	
	public static int countFieldsByModifier(ClassInfo classInfo, NodeModifiers modifier) {
		return (int) classInfo.getFields().stream().filter(f -> f.getModifiers().contains(modifier)).count();
	}

	public static int sumFieldsByModifierCount(List<ClassInfo> classes, NodeModifiers modifier) {
		return classes.stream().mapToInt(c -> countFieldsByModifier(c, modifier)).sum();
	}

	//			====== FieldsByAllModifiers ======			//
	public static int countFieldsByAllModifiers(ProjectInfo projectInfo, List<NodeModifiers> modifiers) {
		return sumFieldsByAllModifiersCount(projectInfo.getClasses(), modifiers);
	}
	
	public static int countFieldsByAllModifiers(PackageInfo packageInfo, List<NodeModifiers> modifiers) {
		return sumFieldsByAllModifiersCount(packageInfo.getClasses(), modifiers);
	}
	
	public static int sumFieldsByAllModifiersCountFromPackages(List<PackageInfo> packages, List<NodeModifiers> modifiers) {
		return packages.stream().mapToInt(p -> countFieldsByAllModifiers(p, modifiers)).sum();
	}
	
	public static int countFieldsByAllModifiers(ClassInfo classInfo, List<NodeModifiers> modifiers) {
		return (int) classInfo.getFields().stream().filter(f -> f.getModifiers().containsAll(modifiers)).count();
	}

	public static int sumFieldsByAllModifiersCount(List<ClassInfo> classes, List<NodeModifiers> modifiers) {
		return classes.stream().mapToInt(c -> countFieldsByAllModifiers(c, modifiers)).sum();
	}

	//			====== FieldsByTypeMatching ======			//
	public static int countFieldsByTypeMatching(ProjectInfo projectInfo, String pattern) {
		return sumFieldsByTypeMatchingCount(projectInfo.getClasses(), pattern);
	}
	
	public static int countFieldsByTypeMatching(PackageInfo packageInfo, String pattern) {
		return sumFieldsByTypeMatchingCount(packageInfo.getClasses(), pattern);
	}
	
	public static int sumFieldsByTypeMatchingCountFromPackages(List<PackageInfo> packages, String pattern) {
		return packages.stream().mapToInt(p -> countFieldsByTypeMatching(p, pattern)).sum();
	}
	
	public static int countFieldsByTypeMatching(ClassInfo classInfo, String pattern) {
		return (int) classInfo.getFields().stream().filter(f -> f.getType().matches(pattern)).count();
	}
	
	public static int sumFieldsByTypeMatchingCount(List<ClassInfo> classes, String pattern) {
		return classes.stream().mapToInt(c -> countFieldsByTypeMatching(c, pattern)).sum();
	}

	//			====== FieldsByNameMatching ======			//
	public static int countFieldsByNameMatching(ProjectInfo projectInfo, String pattern) {
		return sumFieldsByNameMatchingCount(projectInfo.getClasses(), pattern);
	}
	
	public static int countFieldsByNameMatching(PackageInfo packageInfo, String pattern) {
		return sumFieldsByNameMatchingCount(packageInfo.getClasses(), pattern);
	}
	
	public static int sumFieldsByNameMatchingCountFromPackages(List<PackageInfo> packages, String pattern) {
		return packages.stream().mapToInt(p -> countFieldsByNameMatching(p, pattern)).sum();
	}
	public static int countFieldsByNameMatching(ClassInfo classInfo, String pattern) {
		return (int) classInfo.getFields().stream().filter(f -> f.getName().matches(pattern)).count();
	}

	public static int sumFieldsByNameMatchingCount(List<ClassInfo> classes, String pattern) {
		return classes.stream().mapToInt(c -> countFieldsByNameMatching(c, pattern)).sum();
	}
	
}
