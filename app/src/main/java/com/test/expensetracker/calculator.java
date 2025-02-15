package com.test.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.test.expensetracker.R;

import java.util.ArrayList;
import java.util.List;

public class calculator extends AppCompatActivity {

    private TextView displayTextView;
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonClear, buttonDelete, buttonDivide, buttonMultiply, buttonSubtract, buttonAdd, buttonDecimal, buttonEquals;

    private StringBuilder input;
    private List<String> operands;
    private List<Character> operators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        displayTextView = findViewById(R.id.displayTextView);

        // Number buttons
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);

        // Operator buttons
        buttonClear = findViewById(R.id.buttonClear);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDivide = findViewById(R.id.buttonDivide);
        buttonMultiply = findViewById(R.id.buttonMultiply);
        buttonSubtract = findViewById(R.id.buttonSubtract);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonDecimal = findViewById(R.id.buttonDecimal);
        buttonEquals = findViewById(R.id.buttonEquals);

        input = new StringBuilder();
        operands = new ArrayList<>();
        operators = new ArrayList<>();

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("0");
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("9");
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInput();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteInput();
                clearInput();
            }
        });

        buttonDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("/");
            }
        });

        buttonMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("*");
            }
        });

        buttonSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("-");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput("+");
            }
        });

        buttonDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendInput(".");
            }
        });

        buttonEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
            }
        });
    }

    private void appendInput(String value) {
        input.append(value);
        displayTextView.setText(input.toString());
    }

    private void clearInput() {
        input.setLength(0);
        operands.clear();
        operators.clear();
        displayTextView.setText("");
    }

    private void deleteInput() {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
            displayTextView.setText(input.toString());
        }
    }

    private void calculateResult() {
        if (input.length() == 0)
            return;

        String expression = input.toString();

        // Split the expression into operands and operators
        String[] tokens = expression.split("(?=[+\\-*/])|(?<=[+\\-*/])");

        for (String token : tokens) {
            if (isOperator(token.charAt(0))) {
                while (!operators.isEmpty() && hasPrecedence(operators.get(operators.size() - 1), token.charAt(0))) {
                    evaluate();
                }
                operators.add(token.charAt(0));
            } else {
                operands.add(token);
            }
        }

        while (!operators.isEmpty()) {
            evaluate();
        }

        if (operands.size() == 1) {
            String result = operands.get(0);
            displayTextView.setText(result);
            input.setLength(0);
            input.append(result);
        }
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private boolean hasPrecedence(char op1, char op2) {
        return (op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-');
    }

    private void evaluate() {
        if (operands.size() < 2 || operators.isEmpty())
            return;

        double operand2 = Double.parseDouble(operands.get(operands.size() - 1));
        double operand1 = Double.parseDouble(operands.get(operands.size() - 2));
        char operator = operators.get(operators.size() - 1);

        double result = 0;

        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 == 0) {
                    clearInput();
                    displayTextView.setText("Error");
                    return;
                }
                result = operand1 / operand2;
                break;
        }

        operands.remove(operands.size() - 1);
        operands.set(operands.size() - 1, String.valueOf(result));

        operators.remove(operators.size() - 1);
    }
}
