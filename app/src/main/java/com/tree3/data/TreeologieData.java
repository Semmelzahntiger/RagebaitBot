package com.tree3.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TreeologieData {
    public static TreeologieData INSTANCE = new TreeologieData();

    private final Map<String, Stage> userStages = new HashMap<>();

    public enum Stage {
        ONE("Was ist TREE(1)?", "1"),
        TWO("Was ist TREE(2)?","3"),
        THREE("Was ist TREE(3)?","Noooooo");
        private final String question;
        private final String answer;

        Stage(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
        public String getAnswer() {
            return answer;
        }
        public String getQuestion() {
            return question;
        }
    }

    public static TreeologieData getInstance() {
        return INSTANCE;
    }
    public String getQuestion(String userId) {
        return userStages.get(userId).question;
    }
    public boolean addUser(String userId) {
        if(userStages.containsKey(userId)) {
            return false;
        }
        userStages.put(userId, Stage.ONE);
        return true;
    }
    public Optional<Stage> getUserStage(String userId) {
        return Optional.ofNullable(userStages.get(userId));
    }
    public boolean userInTreeologie(String userId) {
        return userStages.containsKey(userId);
    }
    public boolean inFinalStage(String userId) {
        return userStages.containsKey(userId) && userStages.get(userId).equals(Stage.THREE);
    }
    public boolean isCorrectAnswer(String userId, String answer) {
        Optional<Stage> stage = getUserStage(userId);
        if(stage.isPresent()) {
            Stage stageInstance = stage.get();
            boolean correct = stageInstance.getAnswer().equals(answer);
            if(!correct) {
                return false;
            }
            Stage nextStage = getNextStage(stageInstance);
            if(nextStage != null) {
                userStages.put(userId, nextStage);
            }
            else {
                userStages.remove(userId);
            }
            return true;
        }
        return false;
    }
    private Stage getNextStage(Stage stage) {
        return switch (stage) {
            case ONE -> Stage.TWO;
            case TWO -> Stage.THREE;
            case THREE -> null;
        };
    }
    public void removeUser(String userId) {
        userStages.remove(userId);
    }
}
