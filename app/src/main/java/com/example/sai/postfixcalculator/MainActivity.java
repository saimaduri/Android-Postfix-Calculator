package com.example.sai.postfixcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    String expression = "";
    String answer = "";
    TextView tvexpression, tvanswer;
    PostFix pc;
    Calculator calc;
    ArrayList<String> list = new ArrayList<>();
    boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvexpression = findViewById(R.id.tvexpression);
        tvanswer = findViewById(R.id.tvanswer);
    }

    public void buttonClick (View v) {
        try {
            if (tvexpression.getText().equals("Error")) {
                tvexpression.setText("");
            }

            switch ((String) ((Button) v).getText()) {      // Switch Statement takes in the text of each button and adds respective digit or operator
                case "0":                                   // to the expression.
                    expression += "0";
                    break;
                case "1":
                    expression += "1";
                    break;
                case "2":
                    expression += "2";
                    break;
                case "3":
                    expression += "3";
                    break;
                case "4":
                    expression += "4";
                    break;
                case "5":
                    expression += "5";
                    break;
                case "6":
                    expression += "6";
                    break;
                case "7":
                    expression += "7";
                    break;
                case "8":
                    expression += "8";
                    break;
                case "9":
                    expression += "9";
                    break;
                case "+":
                    expression += "+";
                    break;
                case "-":
                    expression += "-";
                    break;
                case "×": //×
                    expression += "*";
                    break;
                case "÷":
                    expression += "/";
                    break;
                case "CE":                               // If clear is clicked, it calls the clear method, which clears expression, answer,
                    clear();                                // and sets the textview expression and answer to ""
                    break;
                case "^":
                    expression += "^";
                    break;
                case "(":                                   // Parenthesis needs to have a PAUSE because if there is no closing parenthesis,
                    expression += "(";                      // it will compute whatever is after the open parenthesis --> if this happens,
                    pause = true;                           // an error will occur because the postfix converter can not detect the proper order
                    break;
                case ")":
                    expression += ")";                      // Close parenthesis sets PAUSE to false to let the postfix converter continue setting
                    pause = false;                          // the new notation
                    break;
                case "=":
                    expression = answer;                    // Sets expression to answer (to use answer in the next calculation)
                    break;                                  // '=' causes an error if answer is negative !!Calculator does not work for negative values
                case ".":
                    expression += ".";
                    break;
            }

            if (checkValid(toCalculate(expression)) && expression.length() > 0) {
                expression = toCalculate(expression);
                calculate();
                tvexpression.setText(toExpression(expression));
                tvanswer.setText(answer);
            }

            if (tvanswer.length() > 0 && tvanswer.getText().equals("Infinity") || tvanswer.getText().equals("NaN") || tvanswer.getText().equals("-Infinity")) {
                clear();
                tvexpression.setText("Error");                                    // These answers cause errors if they are set to expression (calculator cannot do postfix of strings)
                tvanswer.setText("");
            }

        } catch (Throwable e){
            clear();
            toCalculate(expression);
            calculate();
            tvexpression.setText("Error");
        }
    }

    public void calculate() {
        if (expression.length() > 0 && (Character.isDigit(expression.charAt(expression.length() - 1)) || expression.charAt(expression.length() - 1) == ')') && !pause) {
            pc = new PostFix(expression);
            calc = new Calculator(pc.getPostfixAsList());
            answer = Double.toString(calc.result());
        }
    }

    public void clear() {
        expression = "";
        answer = "";
        tvexpression.setText(expression);
        tvanswer.setText(answer);
    }

    public String toExpression(String expression) {
        if (expression.length() > 1 && expression.charAt(0) == '0') {
            expression = expression.substring(1, expression.length());
        }
        if (expression.indexOf('*') != -1) {
            expression = expression.replace('*', '×');
        }
        if (expression.indexOf('/') != -1) {
            expression = expression.replace('/', '÷');
        }
        return expression;
    }

    public String toCalculate(String expression) {
        if (expression.length() > 0 && expression.charAt(0) == '-') {
            expression = "0" + expression;
            expression = expression.replace('×', '*');
        }
        if (expression.length() > 0 && expression.indexOf('×') != -1) {
            expression = expression.replace('×', '*');
        }
        if (expression.indexOf('÷') != -1) {
            expression = expression.replace('÷', '/');
        }
        return expression;
    }

    public boolean checkValid(String expression) {
        for (int i = 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '+' || expression.charAt(i) == '-' || expression.charAt(i) == '*' || expression.charAt(i) == '/' || expression.charAt(i) == '^') {
                if (expression.charAt(i-1) == '+' || expression.charAt(i-1) == '-' || expression.charAt(i-1) == '*' || expression.charAt(i-1) == '/' || expression.charAt(i-1) == '^') {
                    clear();
                    tvexpression.setText("Error");
                    return false;
                }
            }
        }
        return true;
    }

    public class Calculator {
        private ArrayList<String> postfix = new ArrayList<String>();
        private Stack<Double> stack = new Stack<>();        // Used stack because it has all functions needed for postfix
        // Push to last, pop last digit are the only required
        public Calculator(ArrayList<String> postfix) {
            this.postfix = postfix;
        }

        public double result() {
            for (int i = 0; i < postfix.size(); i++) {
                if (Character.isDigit(postfix.get(i).charAt(0)) || postfix.get(i).charAt(0) == '.') {
                    stack.push(Double.parseDouble(postfix.get(i)));
                } else {
                    double one;
                    double two;

                    switch (postfix.get(i)) {
                        case "+":
                            one = stack.pop();
                            two = stack.pop();
                            stack.push(two+one);
                            break;
                        case "-":                           // Does TWO - ONE because ONE is removed before TWO
                            one = stack.pop();              // "3 2 -" results in ONE as 2 and TWO as 3 (must do 3-2, which is TWO-ONE
                            two = stack.pop();
                            stack.push(two-one);
                            break;
                        case "*":
                            one = stack.pop();
                            two = stack.pop();
                            stack.push(two*one);
                            break;
                        case "/":
                            one = stack.pop();
                            two = stack.pop();
                            stack.push(two/one);
                            break;
                        case "^":
                            one = stack.pop();
                            two = stack.pop();
                            stack.push(Math.pow(two, one));
                            break;
                    }
                }
            }
            return stack.pop();
        }
    }

    public class PostFix {
        private String original;
        private ArrayList<String> postfix = new ArrayList<String>();
        private Stack<Character> stack = new Stack<>();

        public PostFix(String expression)
        {
            original = expression;
            convertExpression();
        }

        public void convertExpression() {
            StringTokenizer tokenizer = new StringTokenizer(original, "+-*/^()", true);

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (Character.isDigit(token.charAt(0))) {
                    postfix.add(token);
                } else {
                    addToStack(token.charAt(0));
                }
            }
            addStackToPostFix();
        }


        public void addToStack(char input) {
            if(stack.isEmpty() || input == '(')
                stack.push(input);
            else {
                if(input == ')') {
                    while(!stack.peek().equals('(')) {
                        postfix.add(stack.pop().toString());
                    }
                    stack.pop();
                }
                else {
                    if(stack.peek().equals('('))
                        stack.push(input);
                    else {
                        if (getPrecedence(input) <= getPrecedence(stack.peek())) {
                            while(!stack.isEmpty() && !(stack.peek() == '('))  {
                                postfix.add(stack.pop().toString());
                            }
                        }
                        stack.push(input);
                    }
                }
            }
        }

        public int getPrecedence(char op)
        {
            switch (op) {
                case '+':
                    return 1;
                case '-':
                    return 1;
                case '*':
                    return 2;
                case '/':
                    return 2;
                case '^':
                    return 3;
                default:
                    return 0;
            }
        }

        public void addStackToPostFix()
        {
            while(!stack.isEmpty())
            {
                postfix.add(stack.pop().toString());
            }
        }

        public ArrayList<String> getPostfixAsList()
        {
            return postfix;
        }
    }
}
