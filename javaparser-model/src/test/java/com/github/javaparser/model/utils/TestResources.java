package com.github.javaparser.model.utils;

import com.github.javaparser.model.classpath.ClasspathSource;
import com.github.javaparser.model.classpath.ResourceHelper;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Didier Villevalois
 */
public class TestResources {

	private final ResourceHelper helper;
	private final String testResourcesPath;
	private final String directory;

	TestResources(ResourceHelper helper, String testResourcesPath, String directory) {
		this.helper = helper;
		this.testResourcesPath = testResourcesPath;
		this.directory = directory;
	}

	public ClasspathSource getStrippedRtJar() throws IOException {
		return ResourceHelper.getJarResourceSource(ClassLoader.getSystemResource("rt-striped.jar"));
	}

	public ClasspathSource getSource(String path) {
		path = path.startsWith("/") ? path : '/' + path;
		path = path.endsWith("/") ? path : path + '/';
		return helper.getSource(testResourcesPath + directory + path);
	}

	public String getResourceAsString(String path) throws IOException {
		return getResourceAsString(path, "UTF-8");
	}

	public String getResourceAsString(String path, String encoding) throws IOException {
		path = path.startsWith("/") ? path : '/' + path;
		return fromStream(getResourceAsStream(path), encoding);
	}

	public InputStream getResourceAsStream(String path) {
		return ClassLoader.getSystemResourceAsStream(testResourcesPath + directory + path);
	}

	private String fromStream(InputStream inputStream, String encoding)
			throws IOException {
		return new String(readFully(inputStream), encoding);
	}

	private byte[] readFully(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}
}
