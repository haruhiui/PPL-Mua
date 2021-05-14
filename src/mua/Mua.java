package mua;

import mua.context.MuaContext;
import mua.execution.Executor;
import mua.input.MuaInput;
import mua.input.MuaIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mua {

    public static void main(String[] args) {
        Mua.run();
    }

    private static final MuaContext globalContext = new MuaContext();
    private static MuaContext currentContext = globalContext;

    public static MuaContext getGlobalContext() {
        return globalContext;
    }

    public static MuaContext getCurrentContext() {
        return currentContext;
    }

    public static void setCurrentContext(MuaContext newCurrCtx) {
        currentContext = newCurrCtx;
    }

    //
    public static boolean hasCurrentOrGlobalVariable(String key) {
        return currentContext.hasVariable(key) || globalContext.hasVariable(key);
    }

    public static String getCurrentOrGlobalVariable(String key) {
        if (currentContext.hasVariable(key))
            return currentContext.getVariable(key);
        return globalContext.getVariable(key);
    }

    public static String addCurrentVariable(String key, String value) {
        return currentContext.addVariable(key, value);
    }

    public static String delCurrentVariable(String key) {
        return currentContext.delVariable(key);
    }

    //

    public static boolean isName(String value) {
        return Pattern.compile("[a-zA-Z]\\w+").matcher(value).matches();
    }

    public static boolean isBoundName(String value) {
        return currentContext.hasVariable(value) || globalContext.hasVariable(value);     // @TODO Whether isname can access global or not
    }

    // support double
    public static boolean isNumber(String value) {
        return Pattern.compile("-?\\d+(\\.\\d+)?").matcher(value).matches();
    }

    public static boolean isBool(String value) {
        return value.equals("true") || value.equals("false");
    }

    public static boolean isWord(String value) {
        return value.startsWith("\"");
    }

    public static boolean isStrippedWord(String value) {
        return (!isNumber(value) && !isBool(value) && !isList(value) && !isInfixExp(value));
    }

    public static boolean isList(String value) {
        return isValidFormattedList(getFormattedSequence(value));
    }

    public static boolean isInfixExp(String value) {
        return isValidFormattedInfixExp(getFormattedSequence(value));
    }

    //
    public static String getFormattedSequence(String value) {
        int offset = 0;
        String result = value;

        // insert '+' before '-', which means we treat minus as addition with a negative number
        // by doing this, we can recognize the negative number
        Matcher m = Pattern.compile("\\(.+\\-.+\\)").matcher(result);
        while (m.find()) {
            result = result.substring(0, m.start() + offset) + "+" + result.substring(m.start() + offset);
            offset += 1;
        }

        offset = 0;
        m = Pattern.compile("\\[|\\]|\\(|\\)|\\+|\\*|/|%").matcher(result);
        while (m.find()) {
            result = result.substring(0, m.start() + offset) + " " + result.substring(m.start() + offset, m.end() + offset) + " " +
                    result.substring(m.end() + offset);
            offset += 2;
        }
        return result.replaceAll("\\s+", " ").trim();
    }

    /**
     * @Description The difference between this and getFormattedSequence is that getFormattedSequence is sensitive to all '[', ']', '(' and ')',
     *              but this not
     * @Param [value]
     * @Return java.lang.String
     */
    public static String getFormattedSequenceStrictly(String value) {
        value = " " + value.replaceAll("\\s+", " ") + " ";

        // format list, " [[a b [c d]]] " -> " [ [ a b [ c d ] ] "
        for (int i = 0; i < value.length(); i++) {
            if (i != 0 && value.charAt(i) == '[' && value.charAt(i - 1) == ' ') {
                value = value.substring(0, i + 1) + " " + value.substring(i + 1); // " [" -> " [ "
            }
        }
        for (int i = value.length() - 1; i >= 0; i--) {
            if (i != value.length() - 1 && value.charAt(i) == ']' && value.charAt(i + 1) == ' ') {
                value = value.substring(0, i) + " " + value.substring(i); // "] " -> " ] "
            }
        }

        // format infix exp
        int nestInfixExp = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '(') {
                if (nestInfixExp > 0) {
                    value = value.substring(0, i) + " " + value.charAt(i) + " " + value.substring(i + 1); // "(" -> " ( "
                    i += 2;
                } else if (nestInfixExp == 0 && i != 0 && value.charAt(i - 1) == ' ') {
                    value = value.substring(0, i + 1) + " " + value.substring(i + 1); // " (" -> " ( "
                    i += 1;
                }
                nestInfixExp++;
            }
        }
        for (int i = value.length() - 1; i >= 0; i--) {
            if (value.charAt(i) == ')') {
                if (nestInfixExp > 0) {
                    value = value.substring(0, i) + " " + value.charAt(i) + " " + value.substring(i + 1); // ")" -> " ) "
                    i += 1;
                } else if (nestInfixExp == 0 && i != value.length() - 1 && value.charAt(i + 1) == ' ') {
                    value = value.substring(0, i) + " " + value.substring(i); // ") " -> " ) "
                    i += 2;
                }
                nestInfixExp--;
            }
        }

        String[] values = value.split("\\s+");
//        List<Character> ops = List.of('+', '-', '*', '/', '%');
        List<Character> ops = new ArrayList<>();
        ops.add('+');
        ops.add('-');
        ops.add('*');
        ops.add('/');
        ops.add('%');

        nestInfixExp = 0;
        for (int i = 0; i < values.length; i++) {
            if (nestInfixExp > 0) {
                for (int j = 0; j < values[i].length(); j++) {
                    if (ops.contains(values[i].charAt(j))) {
                        values[i] = values[i].substring(0, j) + " " + values[i].charAt(j) + " " + values[i].substring(j + 1);
                        j += 2;
                    }
                }
            }
            if (values[i].equals("("))
                nestInfixExp++;
            if (values[i].equals(")"))
                nestInfixExp--;
        }

        String formatted = "";
        for (String s : values) {
            formatted += " " + s;
        }
        return formatted.replaceAll("\\s+", " ").trim();
    }

    /**
     * @Description '[ a b c ]' -> '[a b c]'
     * @Param [value]
     * @Return java.lang.String
     */
    public static String getPrintableList(String value) {
        value = getFormattedSequence(value);    // '[ a b c ]'
        int offset = 0;
        Matcher m = Pattern.compile("\\[\\s+").matcher(value);
        while (m.find()) {
            value = value.substring(0, m.start() + 1 + offset) + value.substring(m.end() + offset);
            offset -= m.end() - m.start() - 1;
        }

        offset = 0;
        m = Pattern.compile("\\s+\\]").matcher(value);
        while (m.find()) {
            value = value.substring(0, m.start() + offset) + value.substring(m.end() - 1 + offset);
            offset -= m.end() - m.start() - 1;
        }

        return value;
    }

    private static boolean isValidFormattedList(String value) {
        int nestList = 0;
        for (String s : value.split(" ")) {
            if (s.equals("["))
                nestList++;
            else if (s.equals("]"))
                nestList--;
        }
        return nestList == 0 && value.startsWith("[") && value.endsWith("]");
    }

    private static boolean isValidFormattedInfixExp(String value) {
        int nestInfix = 0;
        for (String s : value.split(" ")) {
            if (s.equals("("))
                nestInfix++;
            else if (s.equals(")"))
                nestInfix--;
        }
        return nestInfix == 0 && value.startsWith("(") && value.endsWith(")");
    }

    //
    public static void run() {
        MuaInput input = new MuaInput();
        MuaIterator inputIter = input.getIterator();
        while (inputIter.hasNext()) {
            Executor.executeFirst(inputIter);
        }
    }
}
