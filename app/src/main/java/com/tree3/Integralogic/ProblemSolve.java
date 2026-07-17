package com.tree3.Integralogic;

public class ProblemSolve {
    IntegralProblem integralProblem;
    int amount;
    IntegralSolve.FunctionType[] types;
    int[][] data;
    int[] bounds;

    public ProblemSolve(IntegralProblem integralProblem){
        this.integralProblem = integralProblem;
        this.amount = integralProblem.getAmount();
        this.types = integralProblem.getTypes();
        this.data = integralProblem.getData();
        this.bounds = integralProblem.getBounds();
    }

    public double getSolution(){
        double solution = 0;
        for (int i = 0; i < amount; i++) {
            solution = solution + new IntegralSolve(types[i], bounds, data[i]).solve();
        }
        return solution;
    }

}
