package io.microconfig.osdf.install;

import io.microconfig.osdf.api.MainApi;
import io.microconfig.osdf.api.annotation.Hidden;
import io.microconfig.osdf.api.annotation.Import;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static io.microconfig.osdf.api.ImportPrefix.importPrefix;
import static io.microconfig.osdf.configs.MicroConfigComponents.microConfigComponents;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.FileUtils.readAll;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static java.lang.String.join;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static java.util.Arrays.stream;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;

@RequiredArgsConstructor
public class AutoCompleteInstaller implements FileReplacer {
    private final OSDFPaths paths;
    private final boolean withComponents;
    private final Path tmpPath;
    private final Path dest;

    public static AutoCompleteInstaller autoCompleteInstaller(OSDFPaths paths, boolean withComponents) {
        return new AutoCompleteInstaller(paths, withComponents, of(paths.tmp() + "/autoComplete"), of(getUserDirectoryPath() + "/.bash_completion"));
    }

    private String script() {
        return "__osdf_completions() {\n" +
                "  local cur prev commands components\n" +
                "  local IFS=$'\\n'\n" +
                "  COMPREPLY=()\n" +
                "  CANDIDATES=()\n" +
                "  cur=\"${COMP_WORDS[COMP_CWORD]}\"\n" +
                "  prev=\"${COMP_WORDS[COMP_CWORD-1]}\"\n" +
                "  commands=(" + commands() + ")\n" +
                "  components=\"" + (withComponents ? components() : "") + "\"\n" +
                "  CANDIDATES=$(compgen -W \"${commands[*]}\" -- \"$cur\")\n" +
                "\n" +
                "  if [ ${prev} == \"osdf\" ]; then\n" +
                "    COMPREPLY=($(printf '%q\\n' ${CANDIDATES[*]}))\n" +
                "    return 0;\n" +
                "  fi \n" +
                "\n" +
                "\n" +
                "  COMPREPLY=( $(compgen -W \"${components}\" -- ${cur}) )\n" +
                "  return 0;\n" +
                "\n" +
                "}\n" +
                "complete -F _osdf_completions osdf";
    }

    private String content() {
        String newEntry = script();
        if (!exists(dest)) {
            return newEntry;
        }
        String completionFileContent = readAll(dest);
        if (completionFileContent.contains(newEntry)) {
            return completionFileContent;
        }
        return completionFileContent + "\n" + newEntry + "\n";
    }

    private String commands() {
        return stream(MainApi.class.getMethods())
                .filter(m -> m.getAnnotation(Hidden.class) == null)
                .map(this::findCommandPrefixedName)
                .collect(Collectors.joining(" "));
    }

    private String findCommandPrefixedName(Method method) {
        String prefix = importPrefix(method).toString();
        String delimiter = "\" \"";
        String groupPrefix = "\"";
        if (!prefix.isEmpty()) {
            delimiter = "\" \"" + prefix + " ";
            groupPrefix += prefix + " ";
        }
        return stream(method.getAnnotation(Import.class).api().getMethods())
                .map(Method::getName)
                .map(String::trim)
                .collect(Collectors.joining(delimiter, groupPrefix, "\""));
    }

    private String components() {
        return join(" ", microConfigComponents(paths).active());
    }

    @Override
    public void prepare() {
        writeStringToFile(tmpPath, content());
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + dest);
    }
}

