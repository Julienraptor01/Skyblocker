package me.xmrvizzy.skyblocker.chat.filters;

import me.xmrvizzy.skyblocker.chat.ChatFilterResult;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;

public class MoltenWaveFilter extends SimpleChatFilter {
    public MoltenWaveFilter() {
        //TODO: verify this pattern
        super("^Your Molten Wave hit " + NUMBER + " enemy(?:y|ies) for " + NUMBER + " damage\\.$");
    }

    @Override
    public ChatFilterResult state() {
        return SkyblockerConfig.get().messages.hideMoltenWave;
    }
}
