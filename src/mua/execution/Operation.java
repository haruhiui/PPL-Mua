package mua.execution;

import mua.Mua;
import mua.context.MuaContext;
import mua.input.MuaInput;
import mua.input.MuaIterator;

import java.io.*;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public enum Operation {
    // p1
    ADD (2, "add") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1])) {
                return Integer.toString(Integer.parseInt(args[0]) + Integer.parseInt(args[1]));
            }
            return null;
        }
    },
    SUB (2, "sub") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1])) {
                return Integer.toString(Integer.parseInt(args[0]) - Integer.parseInt(args[1]));
            }
            return null;
        }
    },
    MUL (2, "mul") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1])) {
                return Integer.toString(Integer.parseInt(args[0]) * Integer.parseInt(args[1]));
            }
            return null;
        }
    },
    DIV (2, "div") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1])) {
                return Double.toString(1.0 * Integer.parseInt(args[0]) / Integer.parseInt(args[1]));
            }
            return null;
        }
    },
    MOD (2, "mod") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1])) {
                return Integer.toString(Integer.parseInt(args[0]) % Integer.parseInt(args[1]));
            }
            return null;
        }
    },
    MAKE (2, "make") {
        @Override
        public String execute(String[] args) {
            return Mua.addCurrentVariable(args[0], args[1]);
        }
    },
    THING (1, "thing") {
        @Override
        public String execute(String[] args) {
            return Mua.getCurrentOrGlobalVariable(args[0]);
        }
    },
    PRINT (1, "print") {
        @Override
        public String execute(String[] args) {
            if (args[0] == null) {
                System.out.println("null");
            } else if (Mua.isNumber(args[0]) && !args[0].equals("0")) {
                // System.out.println(Double.parseDouble(args[0]));
                System.out.println(args[0]);
            } else if (Mua.isList(args[0])) {
                String list = Mua.getPrintableList(args[0]);
                System.out.println(list.substring(1, list.length() - 1));
            } else {
                System.out.println(args[0]);
            }
            return args[0];
        }
    },
    READ (1, "read") {
        @Override
        public String execute(String[] args) {
            return args[0];
        }
    },
    // p2
    ERASE (1, "erase") {
        @Override
        public String execute(String[] args) {
            return Mua.delCurrentVariable(args[0]);
        }
    },
    ISNAME (1, "isname") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Mua.isBoundName(args[0]));
        }
    },
    RUN (1, "run") {
        @Override
        public String execute(String[] args) {
            // [[a b [c]] g]
            return Executor.executeList(args[0]);
        }
    },
    EQ (2, "eq") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1]))
                return Boolean.toString(Double.parseDouble(args[0]) == Double.parseDouble(args[1]));
            else
                return args[0].compareTo(args[1]) == 0 ? "true" : "false";
        }
    },
    GT (2, "gt") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1]))
                return Boolean.toString(Double.parseDouble(args[0]) > Double.parseDouble(args[1]));
            else
                return args[0].compareTo(args[1]) > 0 ? "true" : "false";
        }
    },
    LT (2, "lt") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0]) && Mua.isNumber(args[1]))
                return Boolean.toString(Double.parseDouble(args[0]) < Double.parseDouble(args[1]));
            else
                return args[0].compareTo(args[1]) < 0 ? "true" : "false";
        }
    },
    AND (2, "and") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Boolean.parseBoolean(args[0]) && Boolean.parseBoolean(args[1]));
        }
    },
    OR (2, "or") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Boolean.parseBoolean(args[0]) || Boolean.parseBoolean(args[1]));
        }
    },
    NOT (1, "not") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(!Boolean.parseBoolean(args[0]));
        }
    },
    // judgement
    IF (3, "if") {
        @Override
        public String execute(String[] args) {
            String runList = Boolean.parseBoolean(args[0]) ? args[1] : args[2];
            return Executor.executeList(runList);
        }
    },
    ISNUMBER (1, "isnumber") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Mua.isNumber(args[0]));
        }
    },
    ISWORD (1, "isword") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Mua.isStrippedWord(args[0]));
        }
    },
    ISLIST (1, "islist") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Mua.isList(args[0]));
        }
    },
    ISBOOL (1, "isbool") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(Mua.isBool(args[0]));
        }
    },
    ISEMPTY (1, "isempty") {
        @Override
        public String execute(String[] args) {
            return Boolean.toString(args[0].equals("") || args[0].equals("[ ]"));
        }
    },
    // p3
    RETURN (1, "return") {
        @Override
        public String execute(String[] args) {
            return args[0];
        }
    },
    EXPORT (1, "export") {
        @Override
        public String execute(String[] args) {
            String value = Mua.getCurrentOrGlobalVariable(args[0]);
            Mua.getGlobalContext().addVariable(args[0], value);
            return value;
        }
    },
    // p4
    READLIST (0, "readlist") {
        @Override
        public String execute(String[] args) {
            Scanner scanner = MuaInput.getScanner();
            String line = scanner.nextLine();
            if (Pattern.compile("\\s+").matcher(line).matches()) {
                line = scanner.nextLine();                              // @TODO readlist reads the same line?
            }
            return "[ " + line.replaceAll("\\s+", " ").trim() + " ]";
        }
    },
    WORD (2, "word") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0]) &&
                    (Mua.isNumber(args[1]) || Mua.isBool(args[1]) || Mua.isStrippedWord(args[1]))) {
                return args[0] + args[1];
            }
            return null;
        }
    },
    SENTENCE (2, "sentence") {
        @Override
        public String execute(String[] args) {
            if (Mua.isList(args[0])) {
                args[0] = args[0].substring(1, args[0].length() - 1).trim();
            }
            if (Mua.isList(args[1])) {
                args[1] = args[1].substring(1, args[1].length() - 1).trim();
            }
            return "[ " + args[0] + " " + args[1] + " ]";
        }
    },
    LIST (2, "list") {
        @Override
        public String execute(String[] args) {
            return "[ " + args[0] + " " + args[1] + " ]";
        }
    },
    JOIN (2, "join") {
        @Override
        public String execute(String[] args) {
            if (Mua.isList(args[0])) {
                return Mua.getFormattedSequence(args[0]).substring(0, args[0].length() - 1) + args[1].trim() + " ]";
            }
            // @TODO Exception: type mismatch
            return null;
        }
    },
    FIRST (1, "first") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0]) || Mua.isNumber(args[0]) || Mua.isBool(args[0])) {
                return Character.toString(args[0].charAt(0));
            } else if (Mua.isList(args[0])) {
                MuaIterator iter = new MuaInput(args[0].substring(1).trim()).getIterator();
                return iter.next();
            } else {
                // @TODO Exception: type mismatch
                return null;
            }
        }
    },
    LAST (1, "last") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0]) || Mua.isNumber(args[0]) || Mua.isBool(args[0])) {
                return Character.toString(args[0].charAt(args[0].length() - 1));
            } else if (Mua.isList(args[0])) {
                MuaIterator iter = new MuaInput(args[0].substring(1, args[0].length() - 1).trim()).getIterator();
                String lastItem = "";
                while (iter.hasNext()) {
                    lastItem = iter.next();
                }
                return lastItem;
            } else {
                // @TODO Exception: type mismatch
                return null;
            }
        }
    },
    BUTFIRST (1, "butfirst") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0])) {
                return args[0].substring(1);
            } else if (Mua.isList(args[0])) {
                MuaIterator iter = new MuaInput(args[0].substring(1, args[0].length() - 1).trim()).getIterator();
                String res = " ";
                iter.next();
                while (iter.hasNext()) {
                    res += iter.next() + " ";
                }
                return Mua.getFormattedSequence("[" + res.trim() + "]");
            } else {
                // @TODO Exception type mismatch
                return null;
            }
        }
    },
    BUTLAST (1, "butlast") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0])) {
                return args[0].substring(0, args[0].length() - 1);
            } else if (Mua.isList(args[0])) {
                MuaIterator iter = new MuaInput(args[0].substring(1, args[0].length() - 1).trim()).getIterator();
                String lastItem = "", res = "";
                while (iter.hasNext()) {
                    res += lastItem + " ";
                    lastItem = iter.next();
                }
                return Mua.getFormattedSequence("[" + res.trim() + "]");
            } else {
                // @TODO Exception type mismatch
                return null;
            }
        }
    },
    // numerical
    RANDOM (1, "random") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0])) {
                return Double.toString(Math.random() * Double.parseDouble(args[0]));
            }
            return null;        // @TODO Exception: type mismatch
        }
    },
    INT (1, "int") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0])) {
                return Double.toString(Math.floor(Double.parseDouble(args[0])));
            }
            return null;
        }
    },
    SQRT (1, "sqrt") {
        @Override
        public String execute(String[] args) {
            if (Mua.isNumber(args[0])) {
                return Double.toString(Math.sqrt(Double.parseDouble(args[0])));
            }
            return null;
        }
    },
    SAVE (1, "save") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0])) {
                try {
                    ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(args[0]));
                    stream.writeObject(Mua.getCurrentContext());
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return args[0];
            }
            return null;
        }
    },
    LOAD (1, "load") {
        @Override
        public String execute(String[] args) {
            if (Mua.isStrippedWord(args[0])) {
                try {
                    try {
                        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(args[0]));
                        Mua.getCurrentContext().copyMuaContext((MuaContext)stream.readObject());
                        stream.close();
                    } catch (IOException  e) {       // is not a serialized file
                        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                        String line, all = "";
                        while ((line = reader.readLine()) != null) {
                            all += line + " ";
                        }
                        all = all.replaceAll("\"", "").replaceAll("\\s+", " ");

                        MuaInput fileInput = new MuaInput(all);
                        MuaIterator fileIter = fileInput.getIterator();
                        while (fileIter.hasNext()) {
                            Executor.executeFirst(fileIter);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return args[0];
            }
            return null;
        }
    },
    ERALL (0, "erall") {
        @Override
        public String execute(String[] args) {
            Mua.getCurrentContext().eraseAllVariables();
            return "true";
        }
    },
    POALL (0, "poall") {
        @Override
        public String execute(String[] args) {
            Set<String> names = Mua.getCurrentContext().getNames();

            return Mua.getFormattedSequence("[ " + names.stream().reduce((acc, item) -> acc += " " + item).orElse("") + " ]");
        }
    }
    ;

    int argc;
    String cmd;

    Operation(int argc, String cmd) {
        this.argc = argc;
        this.cmd = cmd;
    }

    public int getArgc() {
        return argc;
    }

    public String getCmd() {
        return cmd;
    }

    public abstract String execute(String[] args);

    // static
    public static int getArgc(String op) {
        for (Operation ope : Operation.values()) {
            if (ope.getCmd().equals(op)) {
                return ope.getArgc();
            }
        }
        return -1;
    }

    public static boolean isOperation(String op) {
        for (Operation ope : Operation.values()) {
            if (ope.getCmd().equals(op)) {
                return true;
            }
        }
        return false;
    }

    public static String execute(String op, String[] args) {
        for (Operation ope : Operation.values()) {
            if (ope.getCmd().equals(op)) {
                return ope.execute(args);
            }
        }
        return null;
    }

}
