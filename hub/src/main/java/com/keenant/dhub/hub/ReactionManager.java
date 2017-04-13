package com.keenant.dhub.hub;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ReactionManager {
    private final List<Reaction> reactions = new ArrayList<>();

    public void register(Reaction reaction) {
        new Thread(reaction::execute).start();
        reactions.add(reaction);
    }

    public void unregister(Reaction reaction) {
        reaction.stop();
        reactions.remove(reaction);
    }
}
