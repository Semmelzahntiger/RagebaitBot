package com.tree3.registers;
import com.sun.source.tree.Tree;
import com.tree3.data.TreeologieData;
import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CommandRegister {

    public static void register() {
        Registry.addCommand("tree", "prints tree", slash -> {
            slash.addOption(OptionType.INTEGER, "n", "n-th tree",true);
        }, getTreeBehaviour());
        Registry.addCommand("treeologie", "treeologie", slash -> {}, getTreeologieBehaviour());
        Registry.addCommand("answertreeologie", "Antwort auf die Treeologie", slash -> {
            slash.addOption(OptionType.INTEGER, "answer", "Antwort");
        }, getAnswerTreeologieBehaviour());
    }

    private static Consumer<SlashCommandInteractionEvent> getTreeBehaviour() {
        return event -> {
            String n = event.getOption("n").getAsString();
            int nth = Integer.parseInt(n);
            event.reply("Noooooo").queue();

            for (int i = 1; i <= nth; i++) {
                event.getChannel().sendMessage("I love TREE(" + i + ")").queue();

            }
        };
    }

    private static  Consumer<SlashCommandInteractionEvent> getTreeologieBehaviour(){
        return event -> {
            boolean newUser = TreeologieData.getInstance().addUser(event.getUser().getId());
            if(newUser){
                event.reply(TreeologieData.getInstance().getQuestion(event.getUser().getId())).queue();
            }
            else {
                event.reply("Du arschgeige antworte doch").queue();
            }
        };
    }
    private static Consumer<SlashCommandInteractionEvent> getAnswerTreeologieBehaviour(){
        return event -> {
            String n = event.getOption("answer").getAsString();
            boolean inTreeologie = TreeologieData.getInstance().userInTreeologie(event.getUser().getId());
            if(inTreeologie){
                if(TreeologieData.getInstance().inFinalStage(event.getUser().getId())) {
                    event.reply("Noooooooo").queue();
                    TreeologieData.getInstance().removeUser(event.getUser().getId());
                }
                boolean correctAnswer = TreeologieData.getInstance().isCorrectAnswer(event.getUser().getId(), n);
                if(correctAnswer){
                    Optional<TreeologieData.Stage> stage = TreeologieData.getInstance().getUserStage(event.getUser().getId());
                    if(stage.isPresent()) {
                        event.reply(stage.get().getQuestion()).queue();
                    }
                    else {
                        event.reply("You finished the Treeology").queue();
                    }
                }
                else {
                    event.reply("Noooooooo").queue();
                    TreeologieData.getInstance().removeUser(event.getUser().getId());
                }
            }
        };
    }
}

//TREE(3)