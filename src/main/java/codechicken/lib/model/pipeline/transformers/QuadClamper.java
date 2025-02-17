/*
 * This file is part of CodeChickenLib.
 * Copyright (c) 2018, covers1624, All rights reserved.
 *
 * CodeChickenLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * CodeChickenLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CodeChickenLib. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package codechicken.lib.model.pipeline.transformers;

import codechicken.lib.model.Quad.Vertex;
import codechicken.lib.model.pipeline.IPipelineElementFactory;
import codechicken.lib.model.pipeline.QuadTransformer;
import codechicken.lib.vec.Cuboid6;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

/**
 * This transformer simply clamps the vertices inside the provided box.
 * You probably want to Re-Interpolate the UV's, Color, and Lmap, see {@link QuadReInterpolator}
 *
 * @author covers1624
 */
public class QuadClamper extends QuadTransformer {

    public static IPipelineElementFactory<QuadClamper> FACTORY = QuadClamper::new;

    private final Cuboid6 clampBounds = new Cuboid6();

    QuadClamper() {
        super();
    }

    public QuadClamper(IVertexConsumer parent, AxisAlignedBB bounds) {
        super(parent);
        clampBounds.set(bounds);
    }

    public QuadClamper(IVertexConsumer parent, Cuboid6 bounds) {
        super(parent);
        clampBounds.set(bounds);
    }

    public void setClampBounds(AxisAlignedBB bounds) {
        clampBounds.set(bounds);
    }

    public void setClampBounds(Cuboid6 bounds) {
        clampBounds.set(bounds);
    }

    @Override
    public boolean transform() {
        int s = quad.orientation.ordinal() >> 1;

        quad.clamp(clampBounds);

        // Check if the quad would be invisible and cull it.
        Vertex[] vertices = quad.vertices;
        float x1 = vertices[0].dx(s);
        float x2 = vertices[1].dx(s);
        float x3 = vertices[2].dx(s);
        float x4 = vertices[3].dx(s);

        float y1 = vertices[0].dy(s);
        float y2 = vertices[1].dy(s);
        float y3 = vertices[2].dy(s);
        float y4 = vertices[3].dy(s);

        // These comparisons are safe as we are comparing clamped values.
        boolean flag1 = x1 == x2 && x2 == x3 && x3 == x4;
        boolean flag2 = y1 == y2 && y2 == y3 && y3 == y4;
        return !flag1 && !flag2;
    }
}
