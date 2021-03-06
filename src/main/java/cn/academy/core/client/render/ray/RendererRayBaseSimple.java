/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of the AcademyCraft mod.
* https://github.com/LambdaInnovation/AcademyCraft
* Licensed under GPLv3, see project root for more information.
*/
package cn.academy.core.client.render.ray;

import cn.academy.core.entity.IRay;
import cn.lambdalib.util.deprecated.ViewOptimize;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.helper.Motion3D;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import static cn.lambdalib.util.generic.VecUtils.*;

/**
 * @author WeAthFolD
 *
 */
public abstract class RendererRayBaseSimple extends Render {

    {
        this.shadowOpaque = 0;
    }
    
    @Override
    public void doRender(Entity ent, 
        double x, double y, double z, float a, float b) {
        IRay ray = (IRay) ent;
        
        GL11.glPushMatrix();
        
        double length = ray.getLength();
        double fix = ray.getStartFix();
        
        Vec3 vo;
        if(ray.needsViewOptimize())
            vo = ViewOptimize.getFixVector(ray);
        else
            vo = vec(0, 0, 0);
        // Rotate fix vector to world coordinate
        vo.rotateAroundY(MathUtils.toRadians(270 - ent.rotationYaw));
        
        Vec3 start = vec(0, 0, 0),
            end = add(start, multiply(new Motion3D(ent, true).getMotionVec(), length));
        start = add(start, vo);
        
        x += start.xCoord;
        y += start.yCoord;
        z += start.zCoord;
        
        Vec3 delta = subtract(end, start);
        double dxzsq = delta.xCoord * delta.xCoord + delta.zCoord * delta.zCoord;
        double npitch = MathUtils.toDegrees(Math.atan2(delta.yCoord, Math.sqrt(dxzsq)));
        double nyaw = MathUtils.toDegrees(Math.atan2(delta.xCoord, delta.zCoord));
        
        GL11.glTranslated(x, y, z);
        GL11.glRotated(-90 + nyaw, 0, 1, 0);
        GL11.glRotated(npitch, 0, 0, 1);
        GL11.glTranslated(fix, 0, 0);
        draw(ent, ray.getLength() - fix);
        
        GL11.glPopMatrix();
    }
    
    /**
     * Render the ray in x+ direction. The transformation is automatically applied.
     * Note that if you want view optimizing, you must do it yourself( We don't know where is your begin pos and where is end).
     */
    protected abstract void draw(Entity entity, double suggestedLength);

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return null;
    }

}
