package io.osdf.actions.system.install;

import io.osdf.api.MainApi;
import io.osdf.api.lib.annotations.Hidden;
import io.osdf.api.lib.annotations.Import;
import io.osdf.common.exceptions.MicroConfigException;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.nio.file.Path;

import static io.osdf.api.lib.ImportPrefix.importPrefix;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.core.local.component.MicroConfigComponents.microConfigComponents;
import static java.lang.String.join;
import static java.nio.file.Path.of;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;

@RequiredArgsConstructor
public class AutoCompleteInstaller implements FileReplacer {
    private final OsdfPaths paths;
    private final boolean withComponents;
    private final Path tmpPath;
    private final Path dest;

    public static AutoCompleteInstaller autoCompleteInstaller(OsdfPaths paths, boolean withComponents) {
        return new AutoCompleteInstaller(paths, withComponents, of(paths.tmp() + "/osdf_completion"), of(getUserDirectoryPath() + "/.osdf_completion"));
    }

    private String script() {
        return "_osdf_completions() {\n" +
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
                "  local IFS=$' \\t\\n'\n" +
                "  COMPREPLY=( $(compgen -W \"${components}\" -- ${cur}) )\n" +
                "  return 0;\n" +
                "\n" +
                "}\n" +
                "complete -F _osdf_completions osdf";
    }

    private String commands() {
        return stream(MainApi.class.getMethods())
                .filter(m -> m.getAnnotation(Hidden.class) == null)
                .map(this::findCommandPrefixedName)
                .collect(joining(" "));
    }

    private String findCommandPrefixedName(Method method) {
        String prefix = importPrefix(method).toString();
        if (!prefix.isEmpty()) {
            return "\"" + prefix + "\"";
        }
        return stream(method.getAnnotation(Import.class).api().getMethods())
                .map(Method::getName)
                .map(String::trim)
                .collect(joining("\" \"", "\"", "\""));
    }

    private String components() {
        try {
            return join(" ", microConfigComponents(paths).forGroup("ALL"));
        } catch (MicroConfigException e) {
            //no configs for generating autocomplete
            return "";
        }
    }

    @Override
    public void prepare() {
        writeStringToFile(tmpPath, script());
    }

    @Override
    public void replace() {
        execute("cp " + tmpPath + " " + dest);
    }
}

