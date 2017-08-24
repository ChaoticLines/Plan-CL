package main.java.com.djrapitops.plan.data.cache;

import com.djrapitops.plugin.command.CommandUtils;
import com.djrapitops.plugin.command.ISender;
import com.djrapitops.plugin.utilities.player.IPlayer;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.data.AnalysisData;
import main.java.com.djrapitops.plan.locale.Locale;
import main.java.com.djrapitops.plan.locale.Msg;
import main.java.com.djrapitops.plan.utilities.analysis.Analysis;
import main.java.com.djrapitops.plan.utilities.html.HtmlUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * This class is used to store the most recent AnalysisData object and to run
 * Analysis.
 *
 * @author Rsl1122
 * @since 2.0.0
 */
@Deprecated
public class AnalysisCacheHandler {

    private final Plan plugin;
    private final Analysis analysis;
    private AnalysisData cache;
    private boolean analysisEnabled;

    private final Set<UUID> notifyWhenCached;

    /**
     * Class Constructor.
     * <p>
     * Initializes Analysis
     *
     * @param plugin Current instance of Plan
     */
    public AnalysisCacheHandler(Plan plugin) {
        this.plugin = plugin;
        analysis = new Analysis(plugin);
        analysisEnabled = true;
        notifyWhenCached = new HashSet<>();
    }

    /**
     * Runs analysis, cache method is called after analysis is complete.
     */
    public void updateCache() {
        analysis.runAnalysis(this);
    }

    /**
     * Saves the new analysis data to cache.
     *
     * @param data AnalysisData generated by Analysis.analyze
     */
    public void cache(AnalysisData data) {
        cache = data;
        for (UUID uuid : notifyWhenCached) {
            Optional<IPlayer> player = plugin.fetch().getPlayer(uuid);
            player.ifPresent(this::sendAnalysisMessage);
        }
        notifyWhenCached.clear();
    }

    public void sendAnalysisMessage(ISender sender) {
        sender.sendMessage(Locale.get(Msg.CMD_HEADER_ANALYZE).toString());

        // Link
        String url = HtmlUtils.getServerAnalysisUrlWithProtocol();
        String message = Locale.get(Msg.CMD_INFO_LINK).toString();
        boolean console = !CommandUtils.isPlayer(sender);
        if (console) {
            sender.sendMessage(message + url);
        } else {
            sender.sendMessage(message);
            sender.sendLink("   ", Locale.get(Msg.CMD_INFO_CLICK_ME).toString(), url);
        }

        sender.sendMessage(Locale.get(Msg.CMD_CONSTANT_FOOTER).toString());
    }

    /**
     * Returns the cached AnalysisData.
     *
     * @return null if not cached
     */
    public AnalysisData getData() {
        return cache;
    }

    /**
     * Check if the AnalysisData has been cached.
     *
     * @return true if there is data in the cache.
     */
    public boolean isCached() {
        return cache != null;
    }

    /**
     * @return if currently an analysis is being run
     */
    public boolean isAnalysisBeingRun() {
        return analysis.isAnalysisBeingRun();
    }

    public boolean isAnalysisEnabled() {
        return analysisEnabled;
    }

    public void disableAnalysisTemporarily() {
        analysisEnabled = false;
        analysis.setTaskId(-2);
    }

    public void enableAnalysis() {
        analysis.setTaskId(-1);
        analysisEnabled = true;
    }

    public void addNotification(ISender sender) {
        if (CommandUtils.isPlayer(sender)) {
            notifyWhenCached.add(((Player) sender.getSender()).getUniqueId());
        }
    }
}
