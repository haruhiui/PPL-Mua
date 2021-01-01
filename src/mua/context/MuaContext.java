package mua.context;

import java.util.HashMap;
import java.util.Map;

public class MuaContext {

    private Map<String, String> variable = new HashMap<>();
    private boolean inFunction = false;

    public MuaContext() { }

    public MuaContext(boolean inFunction) {
        this.inFunction = inFunction;
    }

    public boolean isInFunction() {
        return inFunction;
    }

    public boolean hasVariable(String key) {
        return variable.containsKey(key);
    }

    public String getVariable(String key) {
        return variable.get(key);
    }

    public String addVariable(String key, String value) {
        return variable.put(key, value);
    }

    public String delVariable(String key) {
        return variable.remove(key);
    }

}
