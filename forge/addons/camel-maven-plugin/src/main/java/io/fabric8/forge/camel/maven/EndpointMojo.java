/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.forge.camel.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.fabric8.forge.camel.commands.project.helper.RouteBuilderParser;
import io.fabric8.forge.camel.commands.project.helper.XmlRouteParser;
import io.fabric8.forge.camel.commands.project.model.CamelEndpointDetails;
import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.catalog.EndpointValidationResult;
import org.apache.camel.catalog.lucene.LuceneSuggestionStrategy;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Analyses the project source code for Camel routes, and validates the endpoint uris whether there may be invalid uris.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class EndpointMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Whether to fail if invalid Camel endpoints was found. By default the plugin logs the errors at WARN level
     */
    @Parameter(defaultValue = "false", readonly = true, required = false)
    private boolean failOnError;

    /**
     * Whether to include Java files to be validated for invalid Camel endpoints
     */
    @Parameter(defaultValue = "true", readonly = true, required = false)
    private boolean includeJava;

    /**
     * Whether to include XML files to be validated for invalid Camel endpoints
     */
    @Parameter(defaultValue = "true", readonly = true, required = false)
    private boolean includeXml;

    /**
     * Whether to include test source code
     */
    @Parameter(defaultValue = "false", readonly = true, required = false)
    private boolean includeTest;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        CamelCatalog catalog = new DefaultCamelCatalog();
        // enable did you mean
        catalog.setSuggestionStrategy(new LuceneSuggestionStrategy());

        List<CamelEndpointDetails> endpoints = new ArrayList<>();
        Set<File> javaFiles = new LinkedHashSet<File>();
        Set<File> xmlFiles = new LinkedHashSet<File>();

        // find all java route builder classes
        if (includeJava) {
            for (String dir : project.getCompileSourceRoots()) {
                findJavaFiles(new File(dir), javaFiles);
            }
            if (includeTest) {
                for (String dir : project.getTestCompileSourceRoots()) {
                    findJavaFiles(new File(dir), javaFiles);
                }
            }
        }
        // find all xml routes
        if (includeXml) {
            for (Resource dir : project.getResources()) {
                findXmlFiles(new File(dir.getDirectory()), xmlFiles);
            }
            if (includeTest) {
                for (Resource dir : project.getTestResources()) {
                    findXmlFiles(new File(dir.getDirectory()), xmlFiles);
                }
            }
        }

        for (File file : javaFiles) {
            try {
                // parse the java source code and find Camel RouteBuilder classes
                String fqn = file.getPath();
                String baseDir = ".";
                JavaClassSource clazz = (JavaClassSource) Roaster.parse(file);
                if (clazz != null) {
                    RouteBuilderParser.parseRouteBuilder(clazz, baseDir, fqn, endpoints);
                }
            } catch (Exception e) {
                getLog().warn("Error parsing java file " + file + " code due " + e.getMessage());
            }
        }
        for (File file : xmlFiles) {
            try {
                // parse the xml source code and find Camel routes
                String fqn = file.getPath();
                String baseDir = ".";
                InputStream is = new FileInputStream(file);
                XmlRouteParser.parseXmlRoute(is, baseDir, fqn, endpoints);
                is.close();
            } catch (Exception e) {
                getLog().warn("Error parsing xml file " + file + " code due " + e.getMessage());
            }
        }

        boolean allOk = true;
        for (CamelEndpointDetails detail : endpoints) {
            EndpointValidationResult result = catalog.validateEndpointProperties(detail.getEndpointUri());
            if (!result.isSuccess()) {
                allOk = false;

                StringBuilder sb = new StringBuilder();
                sb.append("Camel endpoint validation error: ").append(asRelativeFile(detail.getFileName()));
                if (detail.getLineNumber() != null) {
                    sb.append(" at line: ").append(detail.getLineNumber());
                }
                sb.append("\n\n");
                String out = result.summaryErrorMessage(false);
                sb.append(out);
                sb.append("\n\n");

                getLog().warn(sb.toString());
            }
        }

        if (failOnError && !allOk) {
            throw new MojoExecutionException("Camel endpoint validation failed");
        }
        if (allOk) {
            getLog().info("Camel endpoint validation successful");
        }
    }

    private void findJavaFiles(File dir, Set<File> javaFiles) {
        File[] files = dir.isDirectory() ? dir.listFiles() : null;
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                } else if (file.isDirectory()) {
                    findJavaFiles(file, javaFiles);
                }
            }
        }
    }

    private void findXmlFiles(File dir, Set<File> xmlFiles) {
        File[] files = dir.isDirectory() ? dir.listFiles() : null;
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".xml")) {
                    xmlFiles.add(file);
                } else if (file.isDirectory()) {
                    findXmlFiles(file, xmlFiles);
                }
            }
        }
    }

    private String asRelativeFile(String name) {
        String answer = name;

        String base = project.getBasedir().getAbsolutePath();
        if (name.startsWith(base)) {
            answer = name.substring(base.length());
            // skip leading slash for relative path
            if (answer.startsWith(File.separator)) {
                answer = answer.substring(1);
            }
        }
        return answer;
    }

}
