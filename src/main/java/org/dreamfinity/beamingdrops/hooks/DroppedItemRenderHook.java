package org.dreamfinity.beamingdrops.hooks;

import com.integral.hooklibultimate.asm.Hook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.ForgeHooksClient;
import org.dreamfinity.beamingdrops.BeamRenderer;

import java.util.Random;

@SuppressWarnings({"unused"})
public class DroppedItemRenderHook {
    @Hook(targetMethod = "renderDroppedItem", injectOnExit = true)
    public static void renderDroppedItem(
            RenderItem renderItem,
            EntityItem entityItem,
            IIcon iIcon,
            int p_77020_3_,
            float p_77020_4_,
            float p_77020_5_,
            float p_77020_6_,
            float p_77020_7_
    ) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        BeamRenderer.renderBeam(entityItem);
    }

    @Hook(targetMethod = "renderEntityItem", injectOnExit = true)
    public static boolean renderEntityItem(
            ForgeHooksClient forgeHooksClient,
            EntityItem entityItem,
            ItemStack item,
            float bobing,
            float rotation,
            Random random,
            TextureManager engine,
            RenderBlocks renderBlocks,
            int count,
            @Hook.ReturnValue boolean isRenderNeeded
    ) {
        if (!isRenderNeeded) {
            return false;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        BeamRenderer.renderBeam(entityItem);
        return true;
    }
}