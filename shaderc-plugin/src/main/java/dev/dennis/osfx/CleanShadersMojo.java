package dev.dennis.osfx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN)
public class CleanShadersMojo extends AbstractMojo {
    private final Log log = getLog();

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "shaderc.output-dir", required = true)
    private String outputDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("Cleaning shaders");
        Path shadersPath = Paths.get(project.getBasedir().toString(), outputDir);
        if (!Files.exists(shadersPath)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(shadersPath)) {
            List<Path> pathsList = paths.collect(Collectors.toList());
            List<Path> files = pathsList.stream()
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return (fileName.startsWith("vs_")
                                || fileName.startsWith("fs_")
                                || fileName.startsWith("cs_"))
                                && (fileName.endsWith(".bin")
                                || fileName.endsWith(".bin.d")
                                || fileName.endsWith(".bin.disasm"));

                    })
                    .collect(Collectors.toList());
            for (Path path : files) {
                log.info("Deleting " + path);
                Files.deleteIfExists(path);
            }
            List<Path> directories = pathsList.stream()
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
            Collections.reverse(directories);
            for (Path path : directories) {
                log.info("Deleting " + path);
                try {
                    Files.deleteIfExists(path);
                } catch (DirectoryNotEmptyException e) {
                    log.warn(path + " is not empty");
                }
            }
        } catch (IOException e) {
            throw new MojoFailureException("Failed cleaning shaders", e);
        }

    }
}
