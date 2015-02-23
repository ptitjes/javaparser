package com.github.javaparser.model.source;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.model.element.Origin;

/**
 * @author Didier Villevalois
 */
public class SourceOrigin implements Origin {

	private final CompilationUnit cu;
	private final Node node;

	public SourceOrigin(CompilationUnit cu, Node node) {
		this.cu = cu;
		this.node = node;
	}

	@Override
	public String toLocationString() {
		CompilationUnitAttr srcFile = CompilationUnitAttr.get(cu);
		Position position = Position.beginOf(node);
		return srcFile.file().getPath() + "(" + position.getLine() + "," + position.getColumn() + ")";
	}

	public CompilationUnit getCu() {
		return cu;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public String toString() {
		PackageDeclaration packageDecl = cu.getPackage();
		return "SourceOrigin{package=" +
				(packageDecl == null ? "<default>" : packageDecl.getName()) +
				", nodeClass=" + node.getClass() + '}';
	}
}
