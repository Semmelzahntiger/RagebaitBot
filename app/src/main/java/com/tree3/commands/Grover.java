package com.tree3.commands;

import com.tree3.BotCommand;
import com.tree3.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@BotCommand(name = "groverfind", description = "finds the biggest number in a list using Grover's Algorithm")
public class Grover implements Command {
    @Override
    public void registerCommandData(SlashCommandData data) {
        data.addOption(OptionType.STRING, "numbers", "Write a String of Numbers using the Format a, b, c, ...", true);
    }
    List<Integer> numberlist;

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        String list = event.getOption("numbers").getAsString().strip();
        String[] strings = list.split(",");
        List<Integer> nums = Arrays.stream(strings).map(Integer::parseInt).toList();
        this.numberlist = nums;
        if (nums.isEmpty()){
            event.reply("Layer 8").queue();
        } else {

            int amount = nums.size();
            List<Double> stateVector = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                stateVector.add(1/Math.sqrt(amount));
            }

        }
    }

    private List<Double> Operator1(List<Double> list){
        double maxvalue = list.stream().max(Double::compare).get();
        return new ArrayList<Double>();
    }

}
