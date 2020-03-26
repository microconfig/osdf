package io.microconfig.osdf.install;

import io.microconfig.osdf.api.OSDFApiInfo;
import io.microconfig.osdf.components.AbstractOpenShiftComponent;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static java.lang.String.join;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;

@RequiredArgsConstructor
public class AutoCompleteInstaller {
    private final Path componentsPath;

    public static AutoCompleteInstaller autoCompleteInstaller(Path componentsPath) {
        return new AutoCompleteInstaller(componentsPath);
    }

    public void installAutoComplete(boolean withComponents) {
        Path bashCompletion = of(getUserDirectoryPath() + "/.bash_completion");
        writeStringToFile(bashCompletion, script(withComponents));
    }

    private String script(boolean withComponents) {
        return "_osdf_completions() {\n" +
                "  local cur prev commands components\n" +
                "  COMPREPLY=()\n" +
                "  cur=\"${COMP_WORDS[COMP_CWORD]}\"\n" +
                "  prev=\"${COMP_WORDS[COMP_CWORD-1]}\"\n" +
                "  commands=\"" + commands() + "\"\n" +
                "  components=\"" + (withComponents ? components() : "") + "\"\n" +
                "\n" +
                "  if [[ ${prev} == \"osdf\" ]]; then\n" +
                "    COMPREPLY=( $(compgen -W \"${commands}\" -- ${cur}) )\n" +
                "    return 0;\n" +
                "  fi\n" +
                "\n" +
                "  COMPREPLY=( $(compgen -W \"${components}\" -- ${cur}) )\n" +
                "  return 0;\n" +
                "\n" +
                "}\n" +
                "complete -F _osdf_completions osdf";
    }

    private String commands() {
        return join(" ", OSDFApiInfo.commands());
    }

    private String components() {
        List<String> components = componentsLoader(componentsPath, null, null).load()
                .stream()
                .map(AbstractOpenShiftComponent::getName)
                .collect(toList());
        return join(" ", components);
    }
}
