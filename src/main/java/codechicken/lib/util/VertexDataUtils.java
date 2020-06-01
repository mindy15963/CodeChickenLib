package codechicken.lib.util;

import codechicken.lib.colour.Colour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Vertex5;
import codechicken.lib.vec.uv.UV;
import codechicken.lib.vec.uv.UVTransformation;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.Usage;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.LightUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 4/10/2016.
 * Utilities for anything to do with raw vertex data access.
 */
public class VertexDataUtils {

    /**
     * Gets the position for the element 'position' in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format The format.
     * @return The element position, -1 if it does not exist.
     */
    public static int getPositionElement(VertexFormat format) {
        List<VertexFormatElement> elements = format.getElements();
        for (int e = 0; e < elements.size(); e++) {
            if (elements.get(e).isPositionElement()) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Gets the position for the element 'normal' in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format The format.
     * @return The element position, -1 if it does not exist.
     */
    public static int getNormalElement(VertexFormat format) {
        List<VertexFormatElement> elements = format.getElements();
        for (int e = 0; e < elements.size(); e++) {
            if (elements.get(e).getUsage() == Usage.NORMAL) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Gets the position for the element 'uv' in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format The format.
     * @return The element position, -1 if it does not exist.
     */
    public static int getUVElement(VertexFormat format) {
        List<VertexFormatElement> elements = format.getElements();
        for (int e = 0; e < elements.size(); e++) {
            if (elements.get(e).getUsage() == Usage.UV && elements.get(e).getIndex() == 0) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Gets the position for the element provided in the elements list for use in LightUtil.pack/unpack for a given format.
     *
     * @param format  The format.
     * @param element THe element to get.
     * @return The element position, -1 if it does not exist.
     */
    public static int getElement(VertexFormat format, VertexFormatElement element) {
        List<VertexFormatElement> elements = format.getElements();
        for (int e = 0; e < elements.size(); e++) {
            if (elements.get(e).equals(element)) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Attempts to get the TextureAtlasSprite for a given UV mapping.
     * This is not threaded and will search EVERY sprite loaded in the texture map.
     * This is meant to be a last resort, where possible always try to avoid using this or have this be fired.
     * TODO Improve searching by caching value ranges somehow.
     *
     * @param textureMap The TextureMap to search.
     * @param uv         The UV mapping to find.
     * @return The TextureAtlasSprite found, returns missing icon if it hasn't been found.
     */
    public static TextureAtlasSprite getSpriteForUV(AtlasTexture textureMap, UV uv) {
        for (TextureAtlasSprite sprite : textureMap.mapUploadedSprites.values()) {
            if (MathHelper.between(sprite.getMinU(), uv.u, sprite.getMaxU()) && MathHelper.between(sprite.getMinV(), uv.v, sprite.getMaxV())) {
                return sprite;
            }
        }
        return textureMap.getSprite(MissingTextureSprite.getLocation());
    }

    public static Map<Direction, List<BakedQuad>> sortFaceData(List<BakedQuad> quads) {
        Map<Direction, List<BakedQuad>> faceQuadMap = new HashMap<>();
        for (BakedQuad quad : quads) {
            List<BakedQuad> faceQuads = faceQuadMap.computeIfAbsent(quad.getFace(), k -> new ArrayList<>());
            faceQuads.add(quad);
        }
        return faceQuadMap;
    }

    public static void fullyPackQuads(int[] packedData, float[][][] unpackedData, VertexFormat format) {
        List<VertexFormatElement> elements = format.getElements();
        for (int e = 0; e < elements.size(); e++) {
            for (int v = 0; v < 4; v++) {
                LightUtil.pack(unpackedData[v][e], packedData, format, v, e);
            }
        }
    }

    public static void fullyUnPackQuads(int[] packedData, float[][][] unpackedData, VertexFormat format) {
        List<VertexFormatElement> elements = format.getElements();
        for (int e = 0; e < elements.size(); e++) {
            for (int v = 0; v < 4; v++) {
                LightUtil.unpack(packedData, unpackedData[v][e], format, v, e);
            }
        }
    }

    public static float[][] unpackElements(int[] packed, VertexFormat format, VertexFormatElement element) {
        float[][] data = new float[4][4];
        int e = getElement(format, element);
        for (int v = 0; v < 4; v++) {
            LightUtil.unpack(packed, data[v], format, v, e);
        }
        return data;
    }

    public static BakedQuad buildQuad(VertexFormat format, TextureAtlasSprite sprite, Direction face, Colour colour, UVTransformation t, Vertex5 v1, Vertex5 v2, Vertex5 v3, Vertex5 v4) {
        //        BakedQuadBuilder builder = new BakedQuadBuilder(format);
        //        builder.setQuadTint(-1);
        //        builder.setQuadOrientation(face);
        //        builder.setTexture(sprite);
        //
        //        t.apply(v1.uv);
        //        t.apply(v2.uv);
        //        t.apply(v3.uv);
        //        t.apply(v4.uv);
        //        putVertex(builder, format, face, v1, colour);
        //        putVertex(builder, format, face, v2, colour);
        //        putVertex(builder, format, face, v3, colour);
        //        putVertex(builder, format, face, v4, colour);
        //
        //        return copyQuad(builder.build());
        return null;
    }

    private static float[] diffuseFaceLight(Direction face, float[] colour) {
        double diffuse;
        switch (face) {
            case DOWN:
                diffuse = 0.5D;
                break;
            case NORTH:
            case SOUTH:
                diffuse = 0.8D;
                break;
            case WEST:
            case EAST:
                diffuse = 0.6D;
                break;
            case UP:
            default:
                diffuse = 1.0D;
                break;
        }

        colour[0] *= diffuse;
        colour[1] *= diffuse;
        colour[2] *= diffuse;

        return colour;
    }
}
