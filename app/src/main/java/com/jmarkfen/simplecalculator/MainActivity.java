package com.jmarkfen.simplecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Expression expr;
    TextView input;
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        output = findViewById(R.id.output);
        output.setText("0");
        expr = new Expression();

        View.OnClickListener digitListener = v -> {
            // if operation was entered, this is now the second number.
            if (expr.getOperation() != null) expr.setIndex(2);
            output.setText("");
            int digit = Integer.parseInt(((Button) v).getText().toString());
            expr.appendDigit(digit);
            input.setText(expr.getExpression());
        };
        View.OnClickListener opListener = v -> {
            int id = v.getId();
            if (id == R.id.btnAdd) {
                expr.setOperation(Operation.ADD);
            } else if (id == R.id.btnSubtract) {
                expr.setOperation(Operation.SUBTRACT);
            } else if (id == R.id.btnMultiply) {
                expr.setOperation(Operation.MULTIPLY);
            } else if (id == R.id.btnDivide) {
                expr.setOperation(Operation.DIVIDE);
            } else if (id == R.id.btnModulo) {
                expr.setOperation(Operation.MODULO);
            }
            input.setText(expr.getExpression());
        };
        // digits
        ((Button) findViewById(R.id.btn1)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn2)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn3)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn4)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn5)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn6)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn7)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn8)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn9)).setOnClickListener(digitListener);
        ((Button) findViewById(R.id.btn0)).setOnClickListener(digitListener);
        // dot
        ((Button) findViewById(R.id.btnDot)).setOnClickListener(v -> {
            expr.appendDot();
            input.setText(expr.getExpression());
        });
        ((Button) findViewById(R.id.btnNegation)).setOnClickListener(v -> {
            expr.flipSign();
            input.setText(expr.getExpression());
        });
        // backspace
        ((ImageButton) findViewById(R.id.btnBackspace)).setOnClickListener(v -> {
            expr.backspace();
            input.setText(expr.getExpression());
        });
        // clear all
        ((Button) findViewById(R.id.btnClearAll)).setOnClickListener(v -> {
            expr = new Expression();
            input.setText("");
            output.setText("0");
        });
        // operators
        ((Button) findViewById(R.id.btnAdd)).setOnClickListener(opListener);
        ((Button) findViewById(R.id.btnSubtract)).setOnClickListener(opListener);
        ((Button) findViewById(R.id.btnMultiply)).setOnClickListener(opListener);
        ((Button) findViewById(R.id.btnDivide)).setOnClickListener(opListener);
        ((Button) findViewById(R.id.btnModulo)).setOnClickListener(opListener);
        // equals
        ((Button) findViewById(R.id.btnEquals)).setOnClickListener(v -> {
            String result = expr.getResult();
            output.setText(result);
            expr = new Expression();
            // set previous result as first number
            expr.setN(result);
        });
    }

    enum Operation {
        ADD("+"), SUBTRACT("-"), MULTIPLY("×"), DIVIDE("÷"), MODULO("%");

        public final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public double eval(double a, double b) {
            return switch (this) {
                case ADD -> a + b;
                case SUBTRACT -> a - b;
                case MULTIPLY -> a * b;
                case DIVIDE -> a / b;
                case MODULO -> a % b;
            };
        }

        public double eval(String a, String b) {
            double n1 = Double.parseDouble(a);
            double n2 = Double.parseDouble(b);
            return eval(n1, n2);
        }
    }

    class Expression {
        int nIndex = 1;
        String n1 = "";
        String n2 = "";
        Operation operation = null;
        String symbol = "";
        public String getN() {
            return switch (nIndex) {
                case 1 -> n1;
                case 2 -> n2;
                default -> "0";
            };
        }

        public void setN(String value) {
            switch (nIndex) {
                case 1 -> n1 = value;
                case 2 -> n2 = value;
            };
        }

        void setIndex(int nIndex) {
            this.nIndex = nIndex;
        }

        public void appendDigit(int digit) {
//            if (getN().startsWith("0")) {
//                setN("");
//            }
            if (operation != null) {
                setIndex(2);
            } else {
                setIndex(1);
            }
            setN(getN() + digit);
        }

        public void appendDot() {
            if (!getN().contains(".")) {
                setN(getN() + ".");
            }
        }

        public void backspace() {
            // first number and has an operation
            if (nIndex == 1 && operation != null) {
                operation = null;
                symbol = "";
            } else if (getN().length() > 0) {
                setN(getN().substring(0, getN().length() - 1));
            } else if (nIndex == 2) {
                // remove operator
                operation = null;
                symbol = "";
                // move to first number
                setIndex(1);
            }
            // if on operator index which is empty, go to first number
//            if (get().length() == 0 && index == 1) {
//                index = 0;
//            }
        }

        public void flipSign() {
            if (getN().length() == 0) {
                setN("-");
            } else if (getN().charAt(0) == '-') {
                setN(getN().substring(1));
            } else {
                setN("-" + getN());
            }
        }

        public Operation getOperation() {
            return operation;
        }

        public void setOperation(Operation operation) {
            if (n1.isEmpty()) n1 = "0";
            if (n2 != "") {
                // compute previous
                n1 = getResult();
                n2 = "";
                symbol = "";
                setIndex(2);
            }
            this.operation = operation;
            symbol = operation.symbol;
        }
        public String getExpression() {
            String result = n1 + " " + symbol + " " + n2;
            return result.trim();
        }
        public String getResult() {
            String finalResult = "0";
            String f1 = "0";
            if (!n1.isEmpty()) f1 = n1;
            String f2 = "0";
            if (!n2.isEmpty()) f2 = n2;
            Operation op = Operation.ADD;
            if (operation != null) op = operation;
            try {
                finalResult = "" + op.eval(f1, f2);
                if (finalResult.endsWith(".0")){
                    finalResult = finalResult.replace(".0","");
                }
            } catch (Exception ignored) {}
            symbol = "";
            operation = null;
            return finalResult;
        }
    }
}