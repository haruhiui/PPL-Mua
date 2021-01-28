package mua.execution;

import mua.Mua;
import mua.input.MuaInput;
import mua.input.MuaIterator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

public class Executor {

    /**
     * @Description Return the result of the first command. If it is not a function nor an operation, then just return that string.
     */
    public static String executeFirst(MuaIterator runIter) {
        String cmd = runIter.next();
        return executeCmd(cmd, runIter);
    }

    /**
     * @Description
     */
    public static String executeCmd(String cmd, MuaIterator inputIter) {
        String res = null;

        if (Operation.isOperation(cmd)) {                                           // an operation
            int argc = Operation.getArgc(cmd);
            String[] args = new String[argc];
            for (int i = 0; i < argc; i++) {
                args[i] = executeFirst(inputIter);
            }
            return Operation.execute(cmd, args);
        }
        else if (Mua.isInfixExp(cmd)) {                                             // infix expression
            MuaIterator infixIter = MuaInput.getIteratorOfString(cmd.substring(1, cmd.length() - 1));       // !!!
            String infixExp = "";
            while (infixIter.hasNext()) {
                infixExp += " " + executeFirst(infixIter);    // execute operation if exists
            }
            return calInfixExp(infixExp.trim());
        }
        else if (Mua.hasCurrentOrGlobalVariable(cmd) &&
                Function.isFunctionBody(Mua.getCurrentOrGlobalVariable(cmd))) {     // a function
            Function func = new Function(Mua.getCurrentOrGlobalVariable(cmd));
            return func.execute(inputIter);
        }
        else if (cmd.startsWith(":")) {                                             // get value of it
            return Mua.getCurrentOrGlobalVariable(cmd.substring(1));
        }
        else if (cmd.startsWith("\"")) {                                            // a word, so we can separate function from the word
            return cmd.substring(1);
        }
        else {                                                                      // neither an operation nor a function
            return cmd;
        }
    }

    /**
     * @Description Execute a list of string. Return the result of last expression. TODO last expression or last operation?
     */
    public static String executeList(String runList) {
        MuaIterator runIter = MuaInput.getIteratorOfString(runList.substring(1, runList.length() - 1));
        String lastRes = null;
        while (runIter.hasNext()) {
            String cmd = runIter.next();
            lastRes = executeCmd(cmd, runIter);
            if (Mua.getCurrentContext().isInFunction() && cmd.equals(Operation.RETURN.getCmd())) {
                break;
            }
        }
        return lastRes;
    }

    /**
     * @Description pure infix expression that can be evaluated by js
     */
    public static String calInfixExpByJS(String infixExp) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        String res = null;
        try {
            res = engine.eval(infixExp).toString();
        } catch(ScriptException e) {
            e.printStackTrace();
        }
        return res;
    }

    // ( 5 % 3 - 3 * 3 / ( 5 + 4 ) ), with parenthesis
    public static String calInfixExp(String exp) {
        // exp = Mua.getFormattedSequence(exp.substring(1, exp.length() - 1).trim());
        Queue<String> postfixExp = new LinkedList<>();
        Map<String, Integer> operatorPriority = new HashMap<>();
        operatorPriority.put("(", 1);
        operatorPriority.put("+", 2);
        operatorPriority.put("-", 2);
        operatorPriority.put("*", 3);
        operatorPriority.put("/", 3);
        operatorPriority.put("%", 4);
        operatorPriority.put(")", 5);
        Deque<String> sta = new ArrayDeque<>();

        // get postfix
        String[] exps = exp.split(" ");
        for (int i = 0; i < exps.length; i++) {
            if (Mua.isNumber(exps[i])) {
                postfixExp.add(exps[i]);
            } else if (exps[i].equals("(")) {               // left parenthesis: just push to sta
                sta.offerLast(exps[i]);
            } else if (exps[i].equals(")")) {               // right parenthesis: pop sta and add to postfixExp until ( poped
                for (String s = sta.pollLast(); !sta.isEmpty() && !s.equals("("); s = sta.pollLast()) {
                    postfixExp.add(s);
                }
            } else if (operatorPriority.containsKey(exps[i])) {     // +-*/%: pop sta and add to postfixExp until priority is lower or sta is empty
                for (String s = sta.peekLast();
                     !sta.isEmpty() && operatorPriority.get(s) >= operatorPriority.get(exps[i]);
                     s = sta.peekLast()) {
                    postfixExp.add(sta.pollLast());
                }
                sta.offerLast(exps[i]);
            }
        }
        while (!sta.isEmpty())
            postfixExp.add(sta.pollLast());

        // calculate postfixExp
        Deque<Double> doubleSta = new ArrayDeque<>();
        while (!postfixExp.isEmpty()) {
            if (Mua.isNumber(postfixExp.element())) {    // a number: push to sta
                doubleSta.addLast(Double.parseDouble(postfixExp.remove()));
            } else {
                Double b = doubleSta.removeLast();
                Double a = doubleSta.removeLast();
                Double c = null;
                switch (postfixExp.element()) {
                    case "+":   c = a + b;    break;
                    case "-":   c = a - b;    break;
                    case "*":   c = a * b;    break;
                    case "/":   c = a / b;    break;
                    case "%":   c = a % b;    break;
                }
                doubleSta.addLast(c);
                postfixExp.remove();
            }
        }

        return doubleSta.element().toString();
    }

}
