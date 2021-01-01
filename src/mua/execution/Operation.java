package mua.execution;

import mua.Mua;
import mua.input.MuaIterator;

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
            if (Mua.isNumber(args[0]) && !args[0].equals("0")) {
                System.out.println(Double.parseDouble(args[0]));
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
            return Boolean.toString(args[0].equals("") || args[0].equals("[]"));
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
