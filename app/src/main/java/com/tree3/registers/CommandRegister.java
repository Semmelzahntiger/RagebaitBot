package com.tree3.registers;
import com.sun.source.tree.Tree;
import com.tree3.Integralogic.Helperclass;
import com.tree3.data.TreeologieData;
import com.tree3.util.LaTeXParser;
import com.tree3.util.LogUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

public class CommandRegister {

    static final Map<User, Guild> servermap = new HashMap<>();
    static final Map<User, Integer> answermap = new HashMap<>();
    static final Map<User, List<Role>> rolemap = new HashMap<>();

    public static void register() {
        Registry.addCommand("tree", "prints tree", slash -> {
            slash.addOption(OptionType.INTEGER, "n", "n-th tree",true);
        }, getTreeBehaviour());
        Registry.addCommand("treeologie", "treeologie", slash -> {}, getTreeologieBehaviour());
        Registry.addCommand("answertreeologie", "Antwort auf die Treeologie", slash -> {
            slash.addOption(OptionType.INTEGER, "answer", "Antwort", true);
        }, getAnswerTreeologieBehaviour());
        Registry.addCommand("nuke", "integralragebait", slash -> {
            slash.addOption(OptionType.USER, "user", "Wen trifft die Nuke?", true);
        }, getNukeBehaviour());
        Registry.addCommand("simple", "foxy", slash -> {
            slash.addOption(OptionType.USER, "user", "Foxy", true);
        }, getSimpleBehaviour());
        Registry.addCommand("answersimple", "Layer 8",
                slash -> {
            slash.addOption(OptionType.INTEGER, "answer", "answer to the question", true);
        }, getSimpleAnswerBehaviour());
        Registry.addCommand("createchannel", "Layer 8", slash -> {
            slash.addOption(OptionType.INTEGER, "n", "creates a Channel with the name TREE(n)", true);
            slash.addOption(OptionType.STRING, "category", "Which category will it be in?", true);
        }, getCreateChannelBehaviour());
        Registry.addCommand("generatelatex", "Creates a Latex Formula based on the given String",
                slashCommandData -> slashCommandData.addOption(OptionType.STRING, "formula", "the formula to be parsed into the LaTeX Image", true),
                getGenerateLaTeXImageBehaviour());
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

    private static Consumer<SlashCommandInteractionEvent> getTreeologieBehaviour(){
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

    private static Consumer<SlashCommandInteractionEvent> getNukeBehaviour(){
        return event ->{
            System.out.println("TREE");
        };
    }

    private static Consumer<SlashCommandInteractionEvent> getSimpleBehaviour(){
        return event -> {
            int a = Helperclass.getRandomNumber(1, 10);
            int b = Helperclass.getRandomNumber(1, 10);
            User user = event.getOption("user").getAsUser();
            String id = user.getId();
            Guild guild = event.getGuild();
            Member member = guild.getMemberById(id);
            if(member == null) {
                event.reply("Error while fetching member").queue();
                return;
            }
            String reply = String.format("Was ist %d + %d?", a, b);
            List<Role> roles = member.getRoles();
            event.reply("Sendepause für " + user.getName()).queue();
            guild.modifyMemberRoles(member, List.of()).queue();
            guild.mute(user, true).queue();

            servermap.put(user, guild);
            answermap.put(user, a+b);
            rolemap.put(user, roles);
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessage(reply).queue();
            });
        };
    }

    private static Consumer<SlashCommandInteractionEvent> getSimpleAnswerBehaviour(){
        return event -> {
            int answer = event.getOption("answer").getAsInt();
            User user = event.getUser();
            Guild guild = servermap.get(user);
            Member member = guild.getMember(user);
            if(answer == answermap.get(user)){
                guild.mute(user, false).queue();
                event.reply("Yess").queue();
                guild.modifyMemberRoles(member, rolemap.get(user)).queue();
            }
            else {
                event.reply("Noooooo").queue();
            }
        };
    }

    private static Consumer<SlashCommandInteractionEvent> getCreateChannelBehaviour(){
        return event -> {
            Guild guild = event.getGuild();
            String category = event.getOption("category").getAsString();
            int n = event.getOption("n").getAsInt();
            List<Category> categories = guild.getCategoriesByName(category, true);
            event.reply("Channel wurde erstellt").queue();
            for (Category category1 : categories) {
                guild.createVoiceChannel("TREE(" + n + ")", category1).queue(voiceChannel ->
                        voiceChannel.getGuild().getCategoryById(category1.getId()).modifyVoiceChannelPositions().sortOrder(
                        (one, two) -> {
                            int orderOfOne = Integer.parseInt(one.getName().replaceAll("[^0-9]", ""));
                            int orderOfTwo = Integer.parseInt(two.getName().replaceAll("[^0-9]", ""));

                            if(orderOfOne > orderOfTwo) {
                                return 1;
                            }
                            else if (orderOfOne < orderOfTwo) {
                                return -1;
                            }
                            return 0;
                        }
                ).queue());
            }
        };
    }
    private static Consumer<SlashCommandInteractionEvent> getGenerateLaTeXImageBehaviour() {
        return event -> {
            String formula = event.getOption("formula").getAsString();
            event.reply("Generating LaTeX Image...").queue();
            Optional<InputStream> imageStream = LaTeXParser.formulaToInputStream(formula);
            if(imageStream.isPresent()) {
                InputStream image = imageStream.get();
                event.getHook().sendMessage("Completed Generation of Formula.")
                        .addFiles(FileUpload.fromData(image, UUID.randomUUID() + ".png")).queue();
            }
            else {
                event.getHook().sendMessage("Couldn't generate LaTeX Image").queue();
            }
        };
    }
}

//TREE(3)