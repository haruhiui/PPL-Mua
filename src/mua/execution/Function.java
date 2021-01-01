package mua.execution;

import mua.Mua;
import mua.context.MuaContext;
import mua.input.MuaInput;
import mua.input.MuaIterator;

import java.util.HashMap;
import java.util.Map;

public class Function {

    private String funcStr;
    private String funcInput;
    private String funcBody;
    private MuaContext funcCtx;

    Function(String funcStr) {
        if (!isFunctionBody(funcStr))
            return;
        this.funcStr = funcStr;
        MuaIterator funcIter = MuaInput.getIteratorOfString(funcStr.substring(1, funcStr.length() - 1).trim());
        if (funcIter.hasNext())
            funcInput = funcIter.next();
        if (funcIter.hasNext())
            funcBody = funcIter.next();
        funcCtx = new MuaContext(true);
    }

    public MuaContext getFuncCtx() {
        return funcCtx;
    }

    public String execute(MuaIterator inputIter) {
        // change currentContext of Mua
        MuaIterator paraIter = MuaInput.getIteratorOfString(funcInput.substring(1, funcInput.length() - 1));
        while (paraIter.hasNext()) {
            String nextInput = Executor.executeFirst(inputIter);        // !!
            funcCtx.addVariable(paraIter.next(), nextInput);
        }

        MuaContext lastCtx = Mua.getCurrentContext();
        Mua.setCurrentContext(funcCtx);
        String res = Executor.executeList(funcBody);
        Mua.setCurrentContext(lastCtx);
        return res;
    }

//    // Replace variables by argsMap.
//    private String replaceParameters(MuaIterator bodyIter, Map<String, String> argsMap) {
//        String replaced = "";
//        while (bodyIter.hasNext()) {
//            String next = bodyIter.next();
//            if (Mua.isList(next) || Mua.isInfixExp(next)) {                 // list or an infix expression, look into it
//                MuaIterator nextIter = MuaInput.getIteratorOfString(next.substring(1, next.length() - 1));
//                next = replaceParameters(nextIter, argsMap);
//            } else if (next.startsWith(":") && argsMap.containsKey(next.substring(1))) {
//                next = ":" + argsMap.get(next.substring(1));
//            } else if (argsMap.containsKey(next)) {
//                next = argsMap.get(next);
//            }
//            replaced += " " + next;
//        }
//        return replaced.trim();
//    }
//
//    // Build argument map.
//    public String replaceParameters(MuaIterator inputIter) {
//        MuaIterator paraIter = MuaInput.getIteratorOfString(funcInput.substring(1, funcInput.length() - 1));
//        Map<String, String> argsMap = new HashMap<>();
//        while (paraIter.hasNext() && inputIter.hasNext()) {
//            argsMap.put(paraIter.next(), inputIter.next());
//        }
//
//        MuaIterator bodyIter = MuaInput.getIteratorOfString(funcBody.substring(1, funcBody.length() - 1));
//        return "[ " + replaceParameters(bodyIter, argsMap) + " ]";
//    }

    public static boolean isFunctionBody(String funcStr) {
        if (Mua.isList(funcStr)) {
            MuaIterator funcIter = MuaInput.getIteratorOfString(funcStr.substring(1, funcStr.length() - 1).trim());
            if (funcIter.hasNext() && Mua.isList(funcIter.next())) {
                if (funcIter.hasNext() && Mua.isList(funcIter.next())) {
                    return true;
                }
            }
        }
        return false;
    }

}
