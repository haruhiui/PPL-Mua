package mua.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MuaContext implements Serializable {

    private Map<String, String> variable = new HashMap<>();
    private boolean inFunction = false;

    public MuaContext() {
        variable.put("pi", "3.14159");
    }

    public MuaContext(boolean inFunction) {
        this();
        this.inFunction = inFunction;
    }

    /**
     * @Description This MuaContext object is in local function but not global
     */
    public boolean isInFunction() {
        return inFunction;
    }

    public Set<String> getNames() {
        return variable.keySet();
    }

    public boolean hasVariable(String key) {
        return variable.containsKey(key);
    }

    public Map<String, String> getVariable() {
        return variable;
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

    public void eraseAllVariables() {
        variable.clear();
    }

    public void copyMuaContext(MuaContext ctx) {
        variable.clear();
        Map<String, String> map = ctx.getVariable();
        for (String key : map.keySet()) {
            variable.put(key, map.get(key));
        }
    }
}
