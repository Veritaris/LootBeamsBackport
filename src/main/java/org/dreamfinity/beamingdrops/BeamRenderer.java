package org.dreamfinity.beamingdrops;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

public class BeamRenderer {
    static double MAX_HEIGHT = Config.beamHeight;
    private static final ResourceLocation LOOT_BEAM_TEXTURE = new ResourceLocation("lootbeamsbackport:textures/entity/loot_beam.png");
    private static final ResourceLocation LOOT_BEAM_HIGH_TEXTURE = new ResourceLocation("lootbeamsbackport:textures/entity/loot_beam_high_res.png");
    public static HashMap<String, Color> charToAWTColor = new HashMap<String, Color>() {{
        put("0", new Color(0x000000));
        put("1", new Color(0x0000AA));
        put("2", new Color(0x00AA00));
        put("3", new Color(0x00AAAA));
        put("4", new Color(0xAA0000));
        put("5", new Color(0xAA00AA));
        put("6", new Color(0xFFAA00));
        put("7", new Color(0xAAAAAA));
        put("8", new Color(0x555555));
        put("9", new Color(0x5555FF));
        put("a", new Color(0x55FF55));
        put("b", new Color(0x55FFFF));
        put("c", new Color(0xFF5555));
        put("d", new Color(0xFF55FF));
        put("e", new Color(0xFFFF55));
        put("f", new Color(0xFFFFFF));
        put("g", new Color(0xDDD605));
    }};

    public static void renderBeam(EntityItem entityItem) {
        Color beamColor;
        World world = entityItem.worldObj;
        int age = entityItem.age;
        ItemStack itemStack = entityItem.getEntityItem();

        if (world == null || itemStack == null || !Config.needRenderBeam || (Config.isWhitelist ^ ItemHelper.isStackInArray(itemStack, Config.itemBlackList))) {
            return;
        }

        beamColor = getItemColor(itemStack);
        if (beamColor.equals(Color.WHITE)) {
            return;
        }

        Tessellator tessellator = Tessellator.instance;
        RenderManager.instance.renderEngine.bindTexture(Config.useHighResBeam ? LOOT_BEAM_HIGH_TEXTURE : LOOT_BEAM_TEXTURE);

        GL11.glAlphaFunc(516, 0.01F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glColor4f(beamColor.getRed(), beamColor.getGreen(), beamColor.getBlue(), 0.01f);
        GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE);

        float totalWorldTime = Config.useRotatingBeam ? (float) age : 0;
        double rotationAngle = (double) totalWorldTime * 1 / (MAX_HEIGHT + 4) * 0.05 * (1.0 - 2.5);
        double heightOffset = Config.useFloatingBeam ? Math.cos(totalWorldTime * 0.05) * (1 / (MAX_HEIGHT)) : 0.2;

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(beamColor.getRed(), beamColor.getGreen(), beamColor.getBlue(), beamColor.getAlpha());
        tessellator.setBrightness(15728880);

        double beamDiameter = Config.beamDiameter;
        double radiusMultiplier = beamDiameter * 0.4;
        double beamRadius = beamDiameter / 2;

        double var18 = beamRadius + Math.cos(rotationAngle + 2.356194490192345) * radiusMultiplier;
        double var20 = beamRadius + Math.sin(rotationAngle + 2.356194490192345) * radiusMultiplier;
        double var22 = beamRadius + Math.cos(rotationAngle + 0.7853981633974483) * radiusMultiplier;
        double var24 = beamRadius + Math.sin(rotationAngle + 0.7853981633974483) * radiusMultiplier;
        double var26 = beamRadius + Math.cos(rotationAngle + 3.9269908169872414) * radiusMultiplier;
        double var28 = beamRadius + Math.sin(rotationAngle + 3.9269908169872414) * radiusMultiplier;
        double var30 = beamRadius + Math.cos(rotationAngle + 5.497787143782138) * radiusMultiplier;
        double var32 = beamRadius + Math.sin(rotationAngle + 5.497787143782138) * radiusMultiplier;

        double entityXPos = -beamDiameter / 2;
        double entityYPos = 0.2 + heightOffset + MAX_HEIGHT / 2;
        double entityZPos = -beamDiameter / 2;

        tessellator.addVertexWithUV(entityXPos + var18, entityYPos + MAX_HEIGHT / 2, entityZPos + var20, 0, 0);
        tessellator.addVertexWithUV(entityXPos + var18, entityYPos, entityZPos + var20, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var22, entityYPos, entityZPos + var24, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var22, entityYPos + MAX_HEIGHT / 2, entityZPos + var24, 1, 0);

        tessellator.addVertexWithUV(entityXPos + var30, entityYPos + MAX_HEIGHT / 2, entityZPos + var32, 0, 0);
        tessellator.addVertexWithUV(entityXPos + var30, entityYPos, entityZPos + var32, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos, entityZPos + var28, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos + MAX_HEIGHT / 2, entityZPos + var28, 1, 0);

        tessellator.addVertexWithUV(entityXPos + var22, entityYPos + MAX_HEIGHT / 2, entityZPos + var24, 0, 0);
        tessellator.addVertexWithUV(entityXPos + var22, entityYPos, entityZPos + var24, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var30, entityYPos, entityZPos + var32, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var30, entityYPos + MAX_HEIGHT / 2, entityZPos + var32, 1, 0);

        tessellator.addVertexWithUV(entityXPos + var26, entityYPos + MAX_HEIGHT / 2, entityZPos + var28, 0, 0);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos, entityZPos + var28, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var18, entityYPos, entityZPos + var20, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var18, entityYPos + MAX_HEIGHT / 2, entityZPos + var20, 1, 0);

        tessellator.addVertexWithUV(entityXPos + var22, entityYPos - MAX_HEIGHT / 2, entityZPos + var24, 1, 0);
        tessellator.addVertexWithUV(entityXPos + var22, entityYPos, entityZPos + var24, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var18, entityYPos, entityZPos + var20, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var18, entityYPos - MAX_HEIGHT / 2, entityZPos + var20, 0, 0);

        tessellator.addVertexWithUV(entityXPos + var30, entityYPos - MAX_HEIGHT / 2, entityZPos + var32, 0, 0);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos - MAX_HEIGHT / 2, entityZPos + var28, 1, 0);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos, entityZPos + var28, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var30, entityYPos, entityZPos + var32, 0, 1);

        tessellator.addVertexWithUV(entityXPos + var30, entityYPos - MAX_HEIGHT / 2, entityZPos + var32, 1, 0);
        tessellator.addVertexWithUV(entityXPos + var30, entityYPos, entityZPos + var32, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var22, entityYPos, entityZPos + var24, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var22, entityYPos - MAX_HEIGHT / 2, entityZPos + var24, 0, 0);

        tessellator.addVertexWithUV(entityXPos + var18, entityYPos - MAX_HEIGHT / 2, entityZPos + var20, 1, 0);
        tessellator.addVertexWithUV(entityXPos + var18, entityYPos, entityZPos + var20, 1, 1);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos, entityZPos + var28, 0, 1);
        tessellator.addVertexWithUV(entityXPos + var26, entityYPos - MAX_HEIGHT / 2, entityZPos + var28, 0, 0);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);
        double outlineRadius = beamRadius * 2;

        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos + MAX_HEIGHT / 2, entityZPos, 1, 0);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos, 1, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos, 0, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos + MAX_HEIGHT / 2, entityZPos, 0, 0);


        tessellator.addVertexWithUV(entityXPos, entityYPos + MAX_HEIGHT / 2, entityZPos + outlineRadius, 1, 0);
        tessellator.addVertexWithUV(entityXPos, entityYPos + MAX_HEIGHT / 2, entityZPos, 0, 0);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos, 0, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos + outlineRadius, 1, 1);

        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos + MAX_HEIGHT / 2, entityZPos + outlineRadius, 1, 0);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos + outlineRadius, 1, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos, 0, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos + MAX_HEIGHT / 2, entityZPos, 0, 0);

        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos + MAX_HEIGHT / 2, entityZPos + outlineRadius, 1, 0);
        tessellator.addVertexWithUV(entityXPos, entityYPos + MAX_HEIGHT / 2, entityZPos + outlineRadius, 0, 0);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos + outlineRadius, 0, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos + outlineRadius, 1, 1);

        tessellator.addVertexWithUV(entityXPos, entityYPos - MAX_HEIGHT / 2, entityZPos, 0, 0);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos, 0, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos, 1, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos - MAX_HEIGHT / 2, entityZPos, 1, 0);


        tessellator.addVertexWithUV(entityXPos, entityYPos - MAX_HEIGHT / 2, entityZPos + outlineRadius, 1, 0);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos + outlineRadius, 1, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos, 0, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos - MAX_HEIGHT / 2, entityZPos, 0, 0);

        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos - MAX_HEIGHT / 2, entityZPos, 0, 0);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos, 0, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos + outlineRadius, 1, 1);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos - MAX_HEIGHT / 2, entityZPos + outlineRadius, 1, 0);

        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos - MAX_HEIGHT / 2, entityZPos + outlineRadius, 1, 0);
        tessellator.addVertexWithUV(entityXPos + outlineRadius, entityYPos, entityZPos + outlineRadius, 1, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos, entityZPos + outlineRadius, 0, 1);
        tessellator.addVertexWithUV(entityXPos, entityYPos - MAX_HEIGHT / 2, entityZPos + outlineRadius, 0, 0);

        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
    }

    public static Color getItemColor(ItemStack itemStack) {
        String itemName = itemStack.getDisplayName();
        int colorTagIndex = itemName.indexOf("§");

        if (colorTagIndex == -1) {
            return charToAWTColor.getOrDefault(String.valueOf(itemStack.getItem().getRarity(itemStack).rarityColor.getFormattingCode()), Color.WHITE);
        }

        String colorCodePosition = String.valueOf(itemName.charAt(colorTagIndex + 1));
        return charToAWTColor.getOrDefault(colorCodePosition, Color.WHITE);
    }
}
