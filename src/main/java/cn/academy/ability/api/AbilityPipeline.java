package cn.academy.ability.api;

import cn.academy.core.AcademyCraft;
import cn.academy.core.event.BlockDestroyEvent;
import cn.lambdalib.annoreg.core.Registrant;
import cn.lambdalib.annoreg.mc.RegInitCallback;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;

/**
 * INTERNAL CLASS
 */
@Registrant
public class AbilityPipeline {

    private AbilityPipeline() {}

    /**
     * @return Whether we can break any block at all
     */
    static boolean canBreakBlock(World world) {
        return !ArrayUtils.contains(propWorldsBannedDestroyingBlocks.getIntList(), world.provider.dimensionId);
    }

    /**
     * @return Whether PvP is enabled.
     */
    static boolean canAttackPlayer() {
        return propAttackPlayer.getBoolean();
    }

    /**
     * Tests if we break the block at the specified coordinates.
     * @return Whether the block can be really broken
     */
    static boolean canBreakBlock(World world, int x, int y, int z) {
        return !MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, x, y, z));
    }

    // PROPERTIES
    private static Property propAttackPlayer;
    private static Property propWorldsBannedDestroyingBlocks;

    @RegInitCallback
    private static void _init() {
        Configuration conf = AcademyCraft.config;

        propAttackPlayer = conf.get("generic", "attackPlayer", true, "Whether the skills are effective on players.");
        propWorldsBannedDestroyingBlocks = conf.get("generic", "worldsBannedDestroyingBlocks", new int[]{},
                "The world ids which banned destroying blocks.");

        MinecraftForge.EVENT_BUS.register(new AbilityPipeline());
    }

    @SubscribeEvent
    public void onBlockDestroy(BlockDestroyEvent event) {
        if(!canBreakBlock(event.world))
            event.setCanceled(true);
    }

}
