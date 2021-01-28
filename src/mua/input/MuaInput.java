package mua.input;

import mua.Mua;

import java.util.Scanner;

public class MuaInput {

    public static final int SCANNER = 1;
    public static final int EMPTY = 2;
    public static final int SEQUENCE = 3;

    private static Scanner scanner = new Scanner(System.in);

    private int type = SCANNER;
    private String[] sequence = null;

    public MuaInput() { }

    public MuaInput(String str) {
        if (str.equals(""))
            this.type = EMPTY;
        else
            this.type = SEQUENCE;
        this.sequence = str.split(" ");
    }

    public int getType() {
        return type;
    }

    public InputIterator getIterator() {
        return new InputIterator();
    }

    public static Scanner getScanner() {
        return scanner;
    }

    public static InputIterator getIteratorOfString(String str) {
        return new MuaInput(str.trim()).getIterator();
    }

    class InputIterator implements MuaIterator {

        private int index = 0;

        @Override
        public boolean hasNext() {
            if (MuaInput.this.type == SCANNER) {
                return MuaInput.getScanner().hasNext();
            } else if (MuaInput.this.type == EMPTY) {
                return false;
            } else {
                return index < MuaInput.this.sequence.length;
            }
        }

        @Override
        public String next() {      // @TODO Exception: has not next
            String next = null;
            String nn = null;

            if (MuaInput.this.type == SCANNER) {                               // input from scanner
                next = MuaInput.getScanner().next();
                if (next.startsWith("[")) {
                    while (!Mua.isList(next)) {            // list
                        nn = MuaInput.getScanner().next();
                        next += " " + nn;
//                        if (nn.startsWith("\""))        next += " " + nn.substring(1);
//                        else                            next += " " + nn;
                    }
                } else if (next.startsWith("(")) {                      // infix expression
                    while (!Mua.isInfixExp(next)) {
                        nn = MuaInput.getScanner().next();
                        next += " " + nn;
                    }
                }
                next = Mua.getFormattedSequence(next);
            } else {                                                    // input from sequence, get the next of seq
                next = MuaInput.this.sequence[index++];
                if (next.startsWith("[")) {                             // list, and it is impossible that starts with a "\""
                    while (hasNext() && !Mua.isList(next)) {
                        nn = MuaInput.this.sequence[index++];
                        next += " " + nn;
                    }
                } else if (next.startsWith("(")) {                      // infix sexpression
                    while (hasNext() && !Mua.isInfixExp(next)) {
                        nn = MuaInput.this.sequence[index++];
                        next += " " + nn;
                    }
                }
            }
            return next;
        }
    }

}
