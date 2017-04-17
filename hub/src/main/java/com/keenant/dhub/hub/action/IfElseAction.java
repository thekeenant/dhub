package com.keenant.dhub.hub.action;

import com.keenant.dhub.hub.network.ProviderRule;

public class IfElseAction implements Action {
    private final ProviderRule rule;
    private final Action ifAction;
    private final Action elseAction;

    public IfElseAction(ProviderRule rule, Action ifAction, Action elseAction) {
        this.rule = rule;
        this.ifAction = ifAction;
        this.elseAction = elseAction;
    }

    public IfElseAction(ProviderRule rule, Action ifAction) {
        this(rule, ifAction, null);
    }

    @Override
    public void execute() {
        if (rule.evaluate()) {
            if (ifAction != null) {
                ifAction.execute();
            }
        }
        else if (elseAction != null) {
            elseAction.execute();
        }
    }

    @Override
    public void stop() {
        ifAction.stop();
        elseAction.stop();
    }
}
