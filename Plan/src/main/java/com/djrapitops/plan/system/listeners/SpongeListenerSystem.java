package com.djrapitops.plan.system.listeners;

import com.djrapitops.plan.PlanSponge;
import com.djrapitops.plan.system.listeners.sponge.*;
import org.spongepowered.api.Sponge;

public class SpongeListenerSystem extends ListenerSystem {

    private final PlanSponge plugin;

    public SpongeListenerSystem(PlanSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void registerListeners() {
        plugin.registerListener(
                new SpongeAFKListener(),
                new SpongeChatListener(),
                new SpongeCommandListener(),
                new SpongeDeathListener(),
                new SpongeGMChangeListener(),
                new SpongePlayerListener(),
                new SpongeWorldChangeListener()
        );
    }

    @Override
    protected void unregisterListeners() {
        Sponge.getEventManager().unregisterPluginListeners(plugin);
    }
}
