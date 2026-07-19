package com.tree3;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


@SupportedAnnotationTypes("com.tree3.BotCommand")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class CommandAnnotationProcessor extends AbstractProcessor {
    private static final String SLASH_COMMAND_EVENT = "net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent";
    private static final String COMMAND_TYPE = "com.tree3.Command";
    private static final String GENERATED_PACKAGE = "com.tree3.generated";
    private static final String GENERATED_CLASS = "GeneratedCommands";

    private boolean generated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Emit the holder exactly once, and never from the final round (the Filer
        // rejects file creation there). This runs even when no @BotCommand class exists,
        // so GeneratedCommands is always generated for callers to reference.
        if (generated || roundEnv.processingOver()) {
            return false;
        }
        TypeElement commandType = processingEnv.getElementUtils().getTypeElement(COMMAND_TYPE);
        if(commandType == null) {
            return false;
        }
        TypeMirror commandTypeMirror = commandType.asType();
        Types types = processingEnv.getTypeUtils();

        Set<CommandRegisterData> commands = new LinkedHashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(BotCommand.class)) {
            if(element.getKind() != ElementKind.CLASS) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            if(!types.isSubtype(typeElement.asType(), commandTypeMirror)) {
                ProcessorUtil.error(element, "@BotCommand class " + typeElement.getQualifiedName() + " must implement " + COMMAND_TYPE, processingEnv);
                continue;
            }
            if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                ProcessorUtil.error(element, "@BotCommand class " + typeElement.getQualifiedName()
                        + " must not be abstract", processingEnv);
                continue;
            }
            if (typeElement.getNestingKind().isNested()
                    && !typeElement.getModifiers().contains(Modifier.STATIC)) {
                ProcessorUtil.error(element, "@BotCommand class " + typeElement.getQualifiedName()
                        + " must be a top-level or static nested class", processingEnv);
                continue;
            }
            if (!ProcessorUtil.hasPublicNoArgConstructor(typeElement)) {
                ProcessorUtil.error(element, "@BotCommand class " + typeElement.getQualifiedName()
                        + " must have a public no-argument constructor", processingEnv);
                continue;
            }
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                if(annotationMirror.getAnnotationType().toString().equals(BotCommand.class.getCanonicalName())) {
                    AnnotationValue name = ProcessorUtil.getAnnotationValue(processingEnv.getElementUtils(), annotationMirror, "name");
                    String commandName = name.getValue().toString();
                    AnnotationValue description = ProcessorUtil.getAnnotationValue(processingEnv.getElementUtils(), annotationMirror, "description");
                    String commandDescription = description.getValue().toString();
                    commands.add(new CommandRegisterData(typeElement.getQualifiedName().toString(), commandName, commandDescription));
                }
            }
        }
        generate(commands);
        generated = true;
        return true;
    }
    private void generate(Set<CommandRegisterData> commands) {
        String qualifiedName = GENERATED_PACKAGE + "." + GENERATED_CLASS;
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(qualifiedName);
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.println("package " + GENERATED_PACKAGE + ";");
                out.println();
                out.println("import java.util.List;");
                out.println("import " + COMMAND_TYPE + ";");
                out.println("import com.tree3.GeneratedCommand;");
                out.println();
                out.println("// Generated by com.tree3.CommandAnnotationProcessor. Do not edit.");
                out.println("public final class " + GENERATED_CLASS + " {");
                out.println();

                // The immutable list of commands, one factory call per @BotCommand class.
                out.print("    private static final List<GeneratedCommand> COMMANDS = List.of(");
                if (commands.isEmpty()) {
                    out.println(");");
                } else {
                    out.println();
                    int i = 0;
                    for (CommandRegisterData command : commands) {
                        String separator = (i + 1 < commands.size()) ? "," : "";
                        out.println("            command" + i + "()" + separator);
                        i++;
                    }
                    out.println("    );");
                }
                out.println();
                out.println("    private " + GENERATED_CLASS + "() {");
                out.println("    }");
                out.println();
                out.println("    public static List<GeneratedCommand> getCommands() {");
                out.println("        return COMMANDS;");
                out.println("    }");

                // One factory method per command: instantiate the command once and let both
                // consumers delegate to that single instance through the Command interface.
                int i = 0;
                for (CommandRegisterData command : commands) {
                    out.println();
                    out.println("    private static GeneratedCommand command" + i + "() {");
                    out.println("        Command instance = new " + command.qualifiedClassName() + "();");
                    out.println("        return new GeneratedCommand(");
                    out.println("                \"" + escape(command.name()) + "\",");
                    out.println("                \"" + escape(command.description()) + "\",");
                    out.println("                slash -> instance.registerCommandData(slash),");
                    out.println("                event -> instance.onCommand(event));");
                    out.println("    }");
                    i++;
                }
                out.println("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to generate " + qualifiedName + ": " + e.getMessage());
        }
    }

    /** Escapes a raw annotation value so it can be emitted as a Java string literal. */
    private static String escape(String value) {
        StringBuilder sb = new StringBuilder(value.length() + 2);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
    public record CommandRegisterData(String qualifiedClassName, String name, String description) {}
}
