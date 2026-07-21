package com.tree3.data;

import com.tree3.integrallogic.IntegralProblem;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {

    public static final Map<User, Guild> servermap = new HashMap<>();
    public static final Map<User, Integer> answermap = new HashMap<>();
    public static final Map<User, List<Role>> rolemap = new HashMap<>();
    public static final Map<User, IntegralProblem> problemmap = new HashMap<>();
    public static final Map<User, Double> nukeanswermap = new HashMap<>();

}
