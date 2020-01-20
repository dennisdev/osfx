package dev.dennis.osfx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CompileShadersMojo extends AbstractMojo {
    private static final String BASE_URL = "https://build.lwjgl.org/release/";

    private static final String FILE_NAME = "shaderc";

    private final Log log = getLog();

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "shaderc.lwjgl-version", required = true)
    private String lwjglVersion;

    @Parameter(property = "shaderc.tools-dir", defaultValue = "bin")
    private String toolsDir;

    @Parameter(property = "shaderc.input-dir", required = true)
    private String inputDir;

    @Parameter(property = "shaderc.output-dir", required = true)
    private String outputDir;

    @Parameter(property = "shaderc.disassemble", defaultValue = "false")
    private boolean disassemble;

    @Parameter(property = "shaderc.dx9", defaultValue = "false")
    private boolean dx9;

    @Parameter(property = "shaderc.dx11", defaultValue = "false")
    private boolean dx11;

    @Parameter(property = "shaderc.glsl", defaultValue = "false")
    private boolean glsl;

    @Parameter(property = "shaderc.metal", defaultValue = "false")
    private boolean metal;

    private static ShaderType getShaderType(Path shaderPath) {
        String fileName = shaderPath.getFileName().toString();
        if (fileName.startsWith("vs_")) {
            return ShaderType.VERTEX;
        } else if (fileName.startsWith("fs_")) {
            return ShaderType.FRAGMENT;
        } else if (fileName.startsWith("cs_")) {
            return ShaderType.COMPUTE;
        }
        return null;
    }

    private static boolean isShader(Path shaderPath) {
        return getShaderType(shaderPath) != null && shaderPath.toString().endsWith(".sc");
    }

    private static String getDx9Profile(ShaderType type) {
        switch (type) {
            case VERTEX:
                return "vs_3_0";
            case FRAGMENT:
                return "ps_3_0";
        }
        return null;
    }

    private static String getDx11Profile(ShaderType type) {
        switch (type) {
            case VERTEX:
                return "vs_4_0";
            case FRAGMENT:
                return "ps_4_0";
            case COMPUTE:
                return "cs_4_0";
        }
        return null;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String osName = System.getProperty("os.name").toLowerCase();
        String os = "";
        String extension = "";
        boolean os64Bit = true;
        if (osName.contains("win")) {
            os = "windows";
            extension = ".exe";
            String osArch = System.getProperty("os.arch");
            if (!osArch.startsWith("amd64") && !osArch.startsWith("x86_64")) {
                os64Bit = false;
            }
        } else {
            if (dx9 || dx11) {
                log.warn("Disabling directX shader compilation: windows required");
                dx9 = false;
                dx11 = false;
            }
            if (osName.contains("mac")) {
                os = "macosx";
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                os = "linux";
            }
        }
        String bitness;
        if (os64Bit) {
            bitness = "x64";
        } else {
            bitness = "x86";
        }
        String fileName = FILE_NAME + extension;
        File baseDir = project.getParent() == null ? project.getBasedir() : project.getParent().getBasedir();
        Path basePath = Paths.get(baseDir.getAbsolutePath());
        Path toolsPath = basePath.resolve(toolsDir);
        try {
            Files.createDirectories(toolsPath);
        } catch (IOException e) {
            throw new MojoFailureException("Failed creating tools directory", e);
        }
        Path compilerPath = toolsPath.resolve(fileName);
        if (!Files.exists(compilerPath) || !Files.isRegularFile(compilerPath)) {
            log.info("Downloading shader compiler binary");
            try {
                String url = BASE_URL + lwjglVersion + "/" + os + "/" + bitness
                        + "/bgfx-tools/" + fileName;
                log.info(url);
                try (InputStream in = new URL(url).openStream()) {
                    Files.copy(in, compilerPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new MojoFailureException("Failed downloading shader compiler binary", e);
            }
        }
        log.info("Compiling shaders");
        try {
            Path shadersPath = Paths.get(project.getBasedir().toString(), inputDir);
            Path outputPath = Paths.get(project.getBasedir().toString(), outputDir);
            try (Stream<Path> paths = Files.walk(shadersPath)) {
                for (Path path : paths.collect(Collectors.toList())) {
                    if (!Files.isRegularFile(path) || !isShader(path)) {
                        continue;
                    }
                    log.info("Compiling shader: " + path);
                    try {
                        compileShaders(compilerPath, path, outputPath);
                    } catch (Exception e) {
                        throw new MojoFailureException("Failed compiling shader: " + path + ", " + e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoFailureException("Failed compiling shaders", e);
        }
    }

    private void compileShaders(Path compilerPath, Path shaderPath, Path outputPath)
            throws IOException, MojoFailureException {
        ShaderType type = getShaderType(shaderPath);
        if (type == null) {
            log.error("Unknown shader type");
            return;
        }
        if (glsl) {
            Path glslOutputPath = outputPath.resolve("glsl");
            String platform = "linux";
            if (type == ShaderType.COMPUTE) {
                compileShader(compilerPath, shaderPath, glslOutputPath, type, platform,
                        "430", 0);
            } else {
                compileShader(compilerPath, shaderPath, glslOutputPath, type, platform,
                        "120", 0);
            }
        }
        if (dx9) {
            Path dx9OutputPath = outputPath.resolve("dx9");
            String platform = "windows";
            String profile = getDx9Profile(type);
            if (profile != null) {
                compileShader(compilerPath, shaderPath, dx9OutputPath, type, platform,
                        profile, 3);
            }
        }
        if (dx11) {
            Path dx11OutputPath = outputPath.resolve("dx11");
            String platform = "windows";
            String profile = getDx11Profile(type);
            int optLvl = type == ShaderType.COMPUTE ? 1 : 3;
            compileShader(compilerPath, shaderPath, dx11OutputPath, type, platform,
                    profile, optLvl);
        }
        if (metal) {
            Path metalOutputPath = outputPath.resolve("metal");
            compileShader(compilerPath, shaderPath, metalOutputPath, type, "osx",
                    "metal", 0);
        }
    }

    private void compileShader(Path compilerPath, Path shaderPath, Path outputPath, ShaderType type,
                               String platform, String profile, int optimizationLvl)
            throws IOException, MojoFailureException {
        Files.createDirectories(outputPath);
        Path shaderOutputPath = outputPath.resolve(shaderPath.getFileName().toString()
                .replace(".sc", ".bin"));
        List<String> command = new ArrayList<>();
        command.add(compilerPath.toString());
        command.add("-f");
        command.add(shaderPath.toString());
        command.add("-o");
        command.add(shaderOutputPath.toString());
        command.add("--type");
        command.add(type.getType());
        command.add("--platform");
        command.add(platform);
        if (profile != null) {
            command.add("-p");
            command.add(profile);
        }
        if (optimizationLvl != 0) {
            command.add("-O");
            command.add(String.valueOf(optimizationLvl));
        }
        if (disassemble) {
            command.add("--disasm");
        }
        Process process = Runtime.getRuntime().exec(command.toArray(new String[0]));
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = outputReader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        String output = builder.toString();
        if (output.endsWith("Failed to build shader.\n")) {
            throw new MojoFailureException(output);
        }
    }
}
