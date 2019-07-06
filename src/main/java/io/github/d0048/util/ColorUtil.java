package io.github.d0048.util;

import com.google.common.collect.Maps;
import com.mojang.realmsclient.util.Pair;

import java.util.Map.Entry;

import io.github.d0048.MCML;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// mainly stuff decompiled from ADynamicColorTable-1.1_1.10.2 as I am unable to contact the author... sorry about that.
public class ColorUtil {
    enum OperationMode {SERVER, MIXED}//We can not access textures from server

    public static String getColorFromState(IBlockState state) {
        return getColorFromBlock(state);
    }

    private static final Map<EnumDyeColor, ColorConversionUtils.LAB> DYE_TO_LAB = Maps.newEnumMap(EnumDyeColor.class);
    private static HashMap<String, ItemStack> colorTable = new HashMap();
    public static final Block[] DEFAULT_BLACKLIST;
    public static final String[] BLACKLIST_RESOURCES;
    private static Block[] BLACKLIST;
    private static boolean USE_DEFAULT_BLOCK_FILTERS = true;
    private static String ALGORITHM_TO_USE = "CMCL:C";

    public static String getKey(Block block) {
        String item = ((ResourceLocation) Block.REGISTRY.getNameForObject(block)).toString();
        return item;
    }

    public static void setBlacklist(String[] stringList) {
        BLACKLIST = new Block[stringList.length];

        for (int i = 0; i < BLACKLIST.length; ++i) {
            BLACKLIST[i] = Block.getBlockFromName(stringList[i]);
        }

    }

    public static void setUseBlockFilters(boolean b) {
        USE_DEFAULT_BLOCK_FILTERS = b;
    }

    public static void setAlgorithm(String algorithm) {
        ALGORITHM_TO_USE = algorithm;
    }

    public static boolean isWhitelisted(IBlockState state) {
        return (!USE_DEFAULT_BLOCK_FILTERS ||
                state.isBlockNormalCube() && state.isOpaqueCube() && !(state.getBlock() instanceof BlockFalling) &&
                        !(state.getBlock() instanceof BlockOre)) &&
                !Arrays.asList(BLACKLIST_RESOURCES).contains(getKey(state.getBlock()));
    }

    public static void init(Logger logger) {
        colorTable.clear();
        Iterator var1 = GameData.getBlockStateIDMap().iterator();

        while (var1.hasNext()) {
            IBlockState state = (IBlockState) var1.next();
            if (isWhitelisted(state)) {
                String c = getColorFromBlock(state);
                if (c != null) {
                    ItemStack stack = createStackedBlock(state);
                    if (stack != null) {
                        colorTable.put(c, stack);
                    }
                }
            }
        }

        logger.info("Dynamic Color Table initialized with " + colorTable.size() + " mappings!");
    }

    @Nullable
    public static ItemStack createStackedBlock(IBlockState state) {
        Item item = Item.getItemFromBlock(state.getBlock());
        if (item == null) {
            return null;
        } else {
            int i = 0;
            if (item.getHasSubtypes()) {
                i = state.getBlock().getMetaFromState(state);
            }

            return new ItemStack(item, 1, i);
        }
    }

    public static String getColorFromBlock(IBlockState state) {
        try {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
            TextureAtlasSprite texture = ibakedmodel.getParticleTexture();
            if (texture != null && !texture.getIconName().equals("missingno")) {
                ResourceLocation resourcelocation = new ResourceLocation(texture.getIconName());
                ResourceLocation loc = new ResourceLocation(resourcelocation.getResourceDomain(),
                        String.format("%s/%s%s", Minecraft.getMinecraft().getTextureMapBlocks().getBasePath(),
                                resourcelocation.getResourcePath(), ".png"));
                InputStream inputstream = Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream();
                Color c = averageColor(ImageIO.read(inputstream));
                return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
            }
        } catch (Exception var8) {
        }

        return null;
    }

    private static Color averageColor(BufferedImage bi) {
        long sumr = 0L;
        long sumg = 0L;
        long sumb = 0L;

        int x;
        for (x = 0; x < bi.getWidth(); ++x) {
            for (int y = 0; y < bi.getHeight(); ++y) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += (long) pixel.getRed();
                sumg += (long) pixel.getGreen();
                sumb += (long) pixel.getBlue();
            }
        }

        x = bi.getWidth() * bi.getHeight();
        return new Color((int) (sumr / (long) x), (int) (sumg / (long) x), (int) (sumb / (long) x));
    }

    public static Pair<ItemStack, String> getBlockFromColor(String hexValue) {
        if (colorTable.isEmpty()) {
            Logger logger = MCML.logger;
            logger.info("Dynamic Color Table has not been initialized! Reloading...");
            init(logger);
        }

        if (colorTable.containsKey(hexValue)) {
            return Pair.of(colorTable.get(hexValue), hexValue);
        } else {
            double[] distances = new double[colorTable.keySet().size()];
            Color val = Color.decode(hexValue);

            int i;
            for (i = 0; i < colorTable.keySet().size(); ++i) {
                Color base = Color.decode((String) colorTable.keySet().toArray()[i]);
                distances[i] = computeWithSelectedAlgorithm(ColorConversionUtils.LAB.convertToCIELAB(base),
                        ColorConversionUtils.LAB.convertToCIELAB(val));
            }

            double minimum = distances[0];
            int index = 0;

            for (i = 0; i < distances.length; ++i) {
                if (distances[i] < minimum) {
                    minimum = distances[i];
                    index = i;
                }
            }

            return Pair.of((ItemStack) colorTable.values().toArray()[index], (String) colorTable.keySet().toArray()[index]);
        }
    }

    private static double computeWithSelectedAlgorithm(ColorConversionUtils.LAB base, ColorConversionUtils.LAB val) {
        if (ALGORITHM_TO_USE.equals("CIE76")) {
            return ColorConversionUtils.deltaE76(base, val);
        } else if (ALGORITHM_TO_USE.equals("CIE94")) {
            return ColorConversionUtils.deltaE94(base, val);
        } else {
            return ALGORITHM_TO_USE.equals("CIEDE2000") ? ColorConversionUtils.deltaE00(base, val) :
                    ColorConversionUtils.deltaECMC(base, val);
        }
    }

    public static IBlockState getGlassFromColor(Color color) {
        float[] rgb = color.getRGBColorComponents((float[]) null);
        ColorConversionUtils.LAB base = ColorConversionUtils.LAB.convertToCIELAB(new Color(rgb[0], rgb[1], rgb[2]));
        double[] distances = new double[DYE_TO_LAB.size()];
        int i = 0;

        for (Iterator var5 = DYE_TO_LAB.entrySet().iterator(); var5.hasNext(); ++i) {
            Entry<EnumDyeColor, ColorConversionUtils.LAB> entry = (Entry) var5.next();
            distances[i] = computeWithSelectedAlgorithm(base, (ColorConversionUtils.LAB) entry.getValue());
        }

        double minimum = distances[0];
        int index = 0;

        for (i = 0; i < distances.length; ++i) {
            if (distances[i] < minimum) {
                minimum = distances[i];
                index = i;
            }
        }

        EnumDyeColor dye = (EnumDyeColor) DYE_TO_LAB.keySet().toArray()[index];
        return Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, dye);
    }

    public static HashMap<String, ItemStack> getTable() {
        return (HashMap) colorTable.clone();
    }

    static {
        DEFAULT_BLACKLIST = new Block[]{Blocks.LEAVES, Blocks.LEAVES2, Blocks.STONE_SLAB, Blocks.STONE_SLAB2,
                                        Blocks.MYCELIUM};/*, Blocks.field_150418_aU, Blocks.field_150342_X, Blocks.field_150349_c,
                                        Blocks.field_150474_ac, Blocks.field_150483_bI, Blocks.field_185777_dd, Blocks.field_185776_dc,
                                        Blocks.field_150449_bY, Blocks.field_150379_bu, Blocks.field_180399_cE, Blocks.field_150378_br,
                                        Blocks.field_150423_aK, Blocks.field_150428_aP, Blocks.field_150460_al, Blocks.field_150470_am,
                                        Blocks.field_150335_W, Blocks.field_185779_df, Blocks.field_189881_dj, Blocks.field_150420_aW,
                                        Blocks.field_150419_aX};*/
        BLACKLIST = DEFAULT_BLACKLIST;
        BLACKLIST_RESOURCES = new String[DEFAULT_BLACKLIST.length];

        for (int i = 0; i < DEFAULT_BLACKLIST.length; ++i) {
            BLACKLIST_RESOURCES[i] = getKey(DEFAULT_BLACKLIST[i]);
        }

        EnumDyeColor[] var5 = EnumDyeColor.values();
        int var1 = var5.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            EnumDyeColor dye = var5[var2];
            float[] rgb = EntitySheep.getDyeRgb(dye);
            DYE_TO_LAB.put(dye, ColorConversionUtils.LAB.convertToCIELAB(new Color(rgb[0], rgb[1], rgb[2])));
        }

    }
}

class ADCTUtils {
    public static final String MODID = "adct";
    public static final String NAME = "A Dynamic Color Table";
    public static final String VERSION = "1.1";
    public static final String CLIENT = "com.themastergabriel.adct.proxy.ClientProxy";
    public static final String SERVER = "com.themastergabriel.adct.proxy.CommonProxy";

    public ADCTUtils() {
    }

    public static int[] parseRelativeCoords(ICommandSender sender, String[] coordString) {
        int[] senderCoords = new int[]{sender.getPosition().getX(), sender.getPosition().getY(),
                                       sender.getPosition().getZ()};
        int[] newCoords = null;
        if (coordString.length % 3 == 0) {
            newCoords = new int[coordString.length];

            for (int i = 0; i < coordString.length; ++i) {
                newCoords[i] = (int) Math.round(parseDouble(coordString[i], (double) senderCoords[i % 3]));
            }
        }

        return newCoords;
    }

    public static double parseDouble(String input, double additive) {
        try {
            return (input.startsWith("~") || input.endsWith("~")) && !input.contains("~~") ?
                    Double.parseDouble(input.replace("~", input.length() == 1 ? "0" : "")) + additive : Double.parseDouble(input);
        } catch (NumberFormatException var4) {
            return 0.0D / 0.0;
        }
    }
}

class ColorConversionUtils {
    public static final double[] MC_WHITE_POINT = new double[]{255.0D, 255.0D, 255.0D};

    public ColorConversionUtils() {
    }

    public static double deltaE76(ColorConversionUtils.LAB lab1, ColorConversionUtils.LAB lab2) {
        double deltaL = lab1.L - lab2.L;
        double deltaA = lab1.A - lab2.A;
        double deltaB = lab1.B - lab2.B;
        return Math.sqrt(Math.pow(deltaL, 2.0D) + Math.pow(deltaA, 2.0D) + Math.pow(deltaB, 2.0D));
    }

    public static double deltaE94(ColorConversionUtils.LAB lab1, ColorConversionUtils.LAB lab2) {
        double K1 = 0.045D;
        double K2 = 0.015D;
        double KL = 1.0D;
        double KC = 1.0D;
        double KH = 1.0D;
        double C1 = Math.sqrt(Math.pow(lab1.A, 2.0D) + Math.pow(lab1.B, 2.0D));
        double C2 = Math.sqrt(Math.pow(lab2.A, 2.0D) + Math.pow(lab2.B, 2.0D));
        double deltaA = lab1.A - lab2.A;
        double deltaB = lab1.B - lab2.B;
        double deltaC = C1 - C2;
        double deltaH2 = deltaA * deltaA + deltaB * deltaB - deltaC * deltaC;
        double deltaH = deltaH2 > 0.0D ? Math.sqrt(deltaH2) : 0.0D;
        double deltaL = lab1.L - lab2.L;
        double SL = 1.0D;
        double SC = 1.0D + K1 * C1;
        double SH = 1.0D + K2 * C1;
        double VL = deltaL / (KL * SL);
        double VC = deltaC / (KC * SC);
        double VH = deltaH / (KH * SH);
        return Math.sqrt(Math.pow(VL, 2.0D) + Math.pow(VC, 2.0D) + Math.pow(VH, 2.0D));
    }

    public static double deltaE00(ColorConversionUtils.LAB lab1, ColorConversionUtils.LAB lab2) {
        double meanLPrime = (lab1.L + lab2.L) / 2.0D;
        double C1 = Math.sqrt(Math.pow(lab1.A, 2.0D) + Math.pow(lab1.B, 2.0D));
        double C2 = Math.sqrt(Math.pow(lab2.A, 2.0D) + Math.pow(lab2.B, 2.0D));
        double meanC = (C1 + C2) / 2.0D;
        double meanCPow7 = Math.pow(meanC, 7.0D);
        double G = 1.0D - Math.sqrt(meanCPow7 / (meanCPow7 + 6.103515625E9D));
        double A1Prime = lab1.A * (1.0D + G);
        double A2Prime = lab2.A * (1.0D + G);
        double C1Prime = Math.sqrt(Math.pow(A1Prime, 2.0D) + Math.pow(lab1.B, 2.0D));
        double C2Prime = Math.sqrt(Math.pow(A2Prime, 2.0D) + Math.pow(lab2.B, 2.0D));
        double meanCPrime = (C1Prime + C2Prime) / 2.0D;
        double arg0 = Math.toDegrees(Math.atan(lab1.B / A1Prime));
        double H1Prime = arg0 >= 0.0D ? arg0 : arg0 + 360.0D;
        double arg1 = Math.toDegrees(Math.atan(lab2.B / A2Prime));
        double H2Prime = arg1 >= 0.0D ? arg1 : arg1 + 360.0D;
        double meanHPrime = H1Prime - H2Prime > 180.0D ? (H1Prime + H2Prime + 360.0D) / 2.0D : (H1Prime + H2Prime) / 2.0D;
        double T =
                1.0D - 0.17D * Math.cos(Math.toRadians(meanHPrime - 30.0D)) + 0.24D * Math.cos(Math.toRadians(2.0D * meanHPrime)) -
                        0.2D * Math.cos(Math.toRadians(4.0D * meanHPrime - 63.0D));
        double arg2 = H2Prime - H1Prime;
        double deltaHPrimeLower =
                Math.abs(arg2) <= 180.0D ? arg2 : (Math.abs(arg2) > 180.0D && H2Prime <= H1Prime ? arg2 + 360.0D : arg2 - 360.0D);
        double deltaLPrime = lab2.L - lab1.L;
        double deltaCPrime = C1Prime - C2Prime;
        double deltaHPrimeUpper = 2.0D * Math.sqrt(C1Prime * C2Prime) * Math.sin(Math.toRadians(deltaHPrimeLower / 2.0D));
        double arg3 = Math.pow(meanLPrime - 50.0D, 2.0D);
        double SL = 1.0D + 0.015D * arg3 / Math.sqrt(20.0D + arg3);
        double SC = 1.0D + 0.045D * meanCPrime;
        double SH = 1.0D + 0.015D * meanCPrime * T;
        double deltaTheta = 30.0D * Math.exp(-Math.pow((meanHPrime - 275.0D) / 25.0D, 2.0D));
        double meanCPrimePow7 = Math.pow(meanCPrime, 7.0D);
        double RC = 2.0D * Math.sqrt(meanCPrimePow7 / (meanCPrimePow7 + 6.103515625E9D));
        double RT = -RC * Math.sin(Math.toRadians(2.0D * deltaTheta));
        double KL = 1.0D;
        double KC = 1.0D;
        double KH = 1.0D;
        return Math.sqrt(Math.pow(deltaLPrime / (KL * SL), 2.0D) + Math.pow(deltaCPrime / (KC * SC), 2.0D) +
                Math.pow(deltaHPrimeUpper / (KH * SH), 2.0D) + RT * (deltaCPrime / (KC * SC)) * (deltaHPrimeUpper / (KH * SH)));
    }

    public static double deltaECMC(ColorConversionUtils.LAB lab1, ColorConversionUtils.LAB lab2) {
        double C1 = Math.sqrt(lab1.A * lab1.A + lab1.B * lab1.B);
        double C2 = Math.sqrt(lab2.A * lab2.A + lab2.B * lab2.B);
        double SL = lab1.L < 16.0D ? 0.511D : 0.040975D * lab1.L / (1.0D + 0.01765D * lab1.L);
        double SC = 0.0638D * C1 / (1.0D + 0.0131D * C1) + 0.638D;

        double H1;
        for (H1 = C1 < 1.0E-6D ? 0.0D : Math.atan2(lab1.B, lab1.A) * 180.0D / 3.141592653589793D; H1 < 0.0D; H1 += 360.0D) {
        }

        while (H1 >= 360.0D) {
            H1 -= 360.0D;
        }

        double T = H1 >= 164.0D && H1 <= 345.0D ? 0.56D + Math.abs(0.2D * Math.cos(3.141592653589793D * (H1 + 168.0D) / 180.0D)) :
                0.36D + Math.abs(0.4D * Math.cos(3.141592653589793D * (H1 + 35.0D) / 180.0D));
        double C4 = Math.pow(C1, 4.0D);
        double F = Math.sqrt(C4 / (C4 + 1900.0D));
        double SH = SC * (F * T + 1.0D - F);
        double deltaL = lab1.L - lab2.L;
        double deltaC = C1 - C2;
        double deltaA = lab1.A - lab2.A;
        double deltaB = lab1.B - lab2.B;
        double deltaH2 = Math.pow(deltaA, 2.0D) + Math.pow(deltaB, 2.0D) - Math.pow(deltaC, 2.0D);
        return Math
                .sqrt(Math.pow(deltaL / (1.0D * SL), 2.0D) + Math.pow(deltaC / (1.0D * SC), 2.0D) + deltaH2 / Math.pow(SH, 2.0D));
    }

    public static class LAB {
        public double L;
        public double A;
        public double B;

        LAB(double L, double A, double B) {
            this.L = L;
            this.A = A;
            this.B = B;
        }

        public static ColorConversionUtils.LAB convertToCIELAB(Color c) {
            double R = (double) c.getRed() / 255.0D;
            double G = (double) c.getGreen() / 255.0D;
            double B = (double) c.getBlue() / 255.0D;
            double red = (R > 0.0404D ? Math.pow((R + 0.055D) / 1.055D, 2.4D) : R / 12.92D) * 100.0D;
            double green = (G > 0.0404D ? Math.pow((G + 0.055D) / 1.055D, 2.4D) : G / 12.92D) * 100.0D;
            double blue = (B > 0.0404D ? Math.pow((B + 0.055D) / 1.055D, 2.4D) : B / 12.92D) * 100.0D;
            double X = red * 0.412453D + green * 0.35758D + blue * 0.180423D;
            double Y = red * 0.212671D + green * 0.71516D + blue * 0.072169D;
            double Z = red * 0.019334D + green * 0.119193D + blue * 0.950227D;
            double Xn = X / ColorConversionUtils.MC_WHITE_POINT[0];
            double Yn = Y / ColorConversionUtils.MC_WHITE_POINT[1];
            double Zn = Z / ColorConversionUtils.MC_WHITE_POINT[2];
            double funcXn = Xn > 0.008856D ? Math.pow(Xn, 0.3333333333333333D) : 7.787D * Xn + 0.13793103448275862D;
            double funcYn = Yn > 0.008856D ? Math.pow(Yn, 0.3333333333333333D) : 7.787D * Yn + 0.13793103448275862D;
            double funcZn = Zn > 0.008856D ? Math.pow(Zn, 0.3333333333333333D) : 7.787D * Zn + 0.13793103448275862D;
            double LStar = Yn > 0.008856D ? 116.0D * Math.pow(Yn, 0.3333333333333333D) : 903.3D * Yn;
            double AStar = 500.0D * (funcXn - funcYn);
            double BStar = 200.0D * (funcYn - funcZn);
            return new ColorConversionUtils.LAB(LStar, AStar, BStar);
        }
    }
}
