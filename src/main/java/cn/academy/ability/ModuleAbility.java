/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of the AcademyCraft mod.
* https://github.com/LambdaInnovation/AcademyCraft
* Licensed under GPLv3, see project root for more information.
*/
package cn.academy.ability;

import cn.academy.ability.api.Category;
import cn.academy.ability.api.CategoryManager;
import cn.academy.ability.block.AbilityInterferer;
import cn.academy.ability.block.BlockDeveloper;
import cn.academy.ability.develop.DeveloperType;
import cn.academy.ability.item.ItemDeveloper;
import cn.academy.ability.item.ItemInductionFactor;
import cn.academy.core.item.ACItem;
import cn.academy.core.registry.ACRecipeNamesRegistration.RegACRecipeNames;
import cn.lambdalib.annoreg.core.Registrant;
import cn.lambdalib.annoreg.mc.RegBlock;
import cn.lambdalib.annoreg.mc.RegEventHandler;
import cn.lambdalib.annoreg.mc.RegEventHandler.Bus;
import cn.lambdalib.annoreg.mc.RegInitCallback;
import cn.lambdalib.annoreg.mc.RegItem;
import cn.lambdalib.crafting.CustomMappingHelper.RecipeName;
import cn.lambdalib.multiblock.ItemBlockMulti;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ChestGenHooks;

import java.util.List;

import static net.minecraftforge.common.ChestGenHooks.*;

/**
 * The ability module init class.
 * 
 * @author WeAthFolD
 */
@Registrant
@RegACRecipeNames
@RegEventHandler(Bus.Forge)
public class ModuleAbility {

    @RegItem
    @RegItem.HasRender
    @RecipeName("dev_portable")
    public static ItemDeveloper developerPortable;

    @RegBlock(item = ItemBlockMulti.class)
    @RecipeName("dev_normal")
    public static BlockDeveloper developerNormal = new BlockDeveloper(DeveloperType.NORMAL);

    @RegBlock(item = ItemBlockMulti.class)
    @RecipeName("dev_advanced")
    public static BlockDeveloper developerAdvanced = new BlockDeveloper(DeveloperType.ADVANCED);

    @RegBlock
    public static AbilityInterferer abilityInterferer;

    @RegItem
    @RecipeName("magnetic_coil")
    public static ACItem magneticCoil = new ACItem("magnetic_coil") {
        {
            setMaxStackSize(1);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wtf) {
            list.add(StatCollector.translateToLocal("item.ac_magnetic_coil.desc"));
        }
    };

    @RegItem
    @RecipeName("induction_factor")
    public static ItemInductionFactor inductionFactor;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.target != null && event.target.typeOfHit == MovingObjectType.BLOCK) {
            if (event.player.worldObj.getBlock(event.target.blockX, event.target.blockY,
                    event.target.blockZ) instanceof BlockDeveloper)
                event.setCanceled(true);
        }
    }

    @RegInitCallback
    public void __init() {
        String[] factorAppearance = { MINESHAFT_CORRIDOR, PYRAMID_DESERT_CHEST, PYRAMID_JUNGLE_CHEST, STRONGHOLD_LIBRARY,
                DUNGEON_CHEST };

        // TODO test generation density
        for (String s : factorAppearance) {
            for (Category c : CategoryManager.INSTANCE.getCategories()) {
                ItemStack stack = inductionFactor.create(c);
                ChestGenHooks.addItem(s, new WeightedRandomChestContent(stack, 1, 1, 4));
            }
        }
    }

}
