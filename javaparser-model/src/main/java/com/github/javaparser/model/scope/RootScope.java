package com.github.javaparser.model.scope;

import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;

import java.util.List;

/**
 * @author Didier Villevalois
 */
public abstract class RootScope extends Scope {

	public abstract List<PackageElem> resolvePackages(EltName name);

	@Override
	public TypeElem resolveType(EltName name) {
		List<PackageElem> packageElems = resolvePackages(name.qualifier());
		if (packageElems != null) {
			for (PackageElem packageElem : packageElems) {
				TypeElem typeElem = packageElem.scope().resolveType(name.simpleName());
				if (typeElem != null) return typeElem;
			}
		}

		TypeElem parentTypeElem = resolveType(name.qualifier());
		if (parentTypeElem != null) {
			TypeElem typeElem = parentTypeElem.scope().resolveType(name.simpleName());
			if (typeElem != null) return typeElem;
		}

		return super.resolveType(name);
	}

	@Override
	public TypeElem resolveLocalType(EltSimpleName name) {
		return null;
	}

	@Override
	public VariableElem resolveLocalVariable(EltSimpleName name) {
		return null;
	}

	@Override
	public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
		return null;
	}
}
