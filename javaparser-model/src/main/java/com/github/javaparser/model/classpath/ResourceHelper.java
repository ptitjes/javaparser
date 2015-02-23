package com.github.javaparser.model.classpath;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * This code has been inspired from code of Greg Briggs as found http://www.uofr.net/~greg/java/get-resource-listing.html.
 *
 * @author Federico Tomassetti
 */
public class ResourceHelper {

	private Set<Pattern> classpathsToAvoids = new HashSet<Pattern>();

	public ResourceHelper() {
	}

	/**
	 * This resorce helper is based on the current classpath but excluded jars from
	 * the jre.
	 */
	public static ResourceHelper classpathExcludingJre() {
		ResourceHelper resourceHelper = new ResourceHelper();
		resourceHelper.ignore(Pattern.compile(".*/jre/.*"));
		return resourceHelper;
	}

	/**
	 * Instruct to ignore all the classpath elements which corresponds to the given regular expression.
	 */
	public void ignore(Pattern regex) {
		classpathsToAvoids.add(regex);
	}

	public Set<ClasspathElement> listElements(String packageName) throws IOException {
		Set<ClasspathElement> elements = new HashSet<ClasspathElement>();
		for (String classpathElement : System.getProperty("java.class.path").split(":")) {
			boolean skip = false;
			for (Pattern classpathToAvoid : classpathsToAvoids) {
				if (classpathToAvoid.matcher(classpathElement).matches()) {
					skip = true;
				}
			}
			if (skip) {
				continue;
			}

			if (classpathElement.endsWith(".jar")) {
				elements.addAll(exploreJar(classpathElement, packageName));
			} else {
				File f = new File(classpathElement);
				if (f.isDirectory()) {
					elements.addAll(exploreDir(f, packageName));
				}
			}
		}
		return elements;
	}

	private Set<ClasspathElement> exploreJar(String jarPath, String path) throws IOException {
		JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
		Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
		Set<ClasspathElement> result = new HashSet<ClasspathElement>(); //ignore duplicates in case it is a subdirectory
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String name = jarEntry.getName();
			if (name.startsWith(path)) { //filter according to the path
				String entry = name.substring(path.length());
				int checkSubdir = entry.indexOf("/");
				if (checkSubdir >= 0) {
					// if it is a subdirectory, we just return the directory name
					entry = entry.substring(0, checkSubdir);
				}
				result.add(new JarClasspathElement(entry, jar, jarEntry));
			}
		}
		return result;
	}

	private Set<ClasspathElement> exploreDir(File dir, String path) throws IOException {
		Set<ClasspathElement> set = new HashSet<ClasspathElement>();
		exploreDir(dir, path, "", set);
		return set;
	}

	private void exploreDir(File dir, String path, String base, Set<ClasspathElement> collector) throws IOException {
		for (File child : dir.listFiles()) {
			String nextBase = base.isEmpty() ? child.getName() : base + "/" + child.getName();
			if (child.isFile()) {
				if (nextBase.startsWith(path)) {
					collector.add(new FileClasspathElement(nextBase, child));
				}
			} else if (child.isDirectory()) {
				exploreDir(child, path, nextBase, collector);
			}
		}
	}

}
