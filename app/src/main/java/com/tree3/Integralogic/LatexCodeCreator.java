package com.tree3.Integralogic;
import static com.tree3.Integralogic.IntegralSolve.FunctionType.*;


public class LatexCodeCreator {

    IntegralProblem integralProblem;
    int[][] data;
    IntegralSolve.FunctionType[] types;
    int[] a;
    int[] b;
    int[] c;
    int[] bounds;

    public LatexCodeCreator(IntegralProblem integralProblem){
        this.integralProblem = integralProblem;
        this.types = integralProblem.getTypes();
        data = integralProblem.getData();
        bounds = integralProblem.getBounds();
        abcd();
    }

    public String toLatexString(int n){
        abcd();
        return switch (types[n]){
            case POLYNOMIAL -> polynomialToLatex(a[n], b[n], c[n]);
            case EXPONENTIAL -> exponentialToLatex(a[n], b[n], c[n]);
            case SINE -> sineToLatex(a[n], b[n], c[n]);
            case COSINE -> cosineToLatex(a[n], b[n], c[n]);
            case XEXPONENTIAL -> xexponentialToLatex(a[n], b[n], c[n]);
            case ARCTAN -> arctanToLatex(a[n], b[n], c[n]);
            case SINCOS -> sincosToLatex(a[n], b[n], c[n]);
            case SQRT -> sqrtToLatex(a[n], b[n], c[n]);
            case LAURENT -> laurentToLatex(a[n], b[n], c[n]);
            case PARTIAL -> partialToLatex(a[n], b[n], c[n]);
        };
    }

    private void abcd(){
        this.a = new int[data.length];
        this.b = new int[data.length];
        this.c = new int[data.length];

        for (int i = 0; i < a.length; i++) {
            a[i] = data[0][i];
            b[i] = data[1][i];
            c[i] = data[2][i];
        }

    }

    private String polynomialToLatex(int a, int b, int c){
        return String.format("%+d %+dx %+dx^2", a,b,c);
    }

    private String exponentialToLatex(int a, int b, int c){
        return String.format("%+d \\cdot e^{%d x %+d}", a, b ,c);
    }

    private String sineToLatex(int a, int b, int c){
        return String.format("%+d \\cdot sin(%d x + %d)", a, b ,c);
    }

    private String cosineToLatex(int a, int b, int c){
        return String.format("%+d \\cdot cos(%d x %+d)", a, b ,c);
    }

    private String xexponentialToLatex(int a, int b, int c){
        return String.format("%+d x \\cdot e^{%d x %+d}", a, b ,c);
    }

    private String arctanToLatex(int a, int b, int c){
        return getSign(a) + String.format("\\frac{%d}{%d + (%d x)^2}", Math.abs(a), b*b ,c);
    }

    private String sincosToLatex(int a, int b, int c){
        return String.format("%+d \\cdot sin(%d x %+d) \\cdot cos(%d x %+d)", a, b ,c, b, c);
    }

    private String sqrtToLatex(int a, int b, int c){
        return String.format("%+d \\cdot sqrt{%d x %+d}", a, b ,c);
    }

    private String laurentToLatex(int a, int b, int c){
        return String.format("%+dx^{-1} %+d x^{-2} %+dx^{-3}", a, b ,c);
    }

    private String partialToLatex(int a, int b, int c){
        return getSign(a) + String.format("\\frac{%d}{x^2 %+dx %+d}", Math.abs(a), -(b+c) ,b*c);
    }

    private String getSign(int number){
        if (number>0){
            return "+ ";
        }
        else if(number<0){
            return "- ";
        }
        else{
            return"0 * ";
        }
    }

    public String toLatexString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            out.append(new LatexCodeCreator(integralProblem).toLatexString(i)).append(" ");
        }
        if (String.valueOf(out.charAt(0)).equals("+")){
            out = new StringBuilder(out.substring(1));
        }
        return out.toString();
    }

    public String toLatexIntegral() {
        return String.format("\\int_{%d}^{%d} ", bounds[0], bounds[1]) + toLatexString() + "dx";
    }

}
