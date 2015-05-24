/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under  
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.academy.core.client.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import cn.academy.core.entity.IRay;
import cn.liutils.render.material.Material;
import cn.liutils.render.material.SimpleMaterial;
import cn.liutils.render.mesh.Mesh;
import cn.liutils.util.helper.Color;

/**
 * @author WeAthFolD
 *
 */
public class RendererRayCylinder<T extends IRay> extends RendererRayBase<T> {
	
	public double width = 0.08;
	
	static final int DIV = 12;
	
	static Mesh head = new Mesh();
	static Mesh cylinder = new Mesh();
	
	public Material material;
	
	static {
		try{
		double drad = Math.PI * 2 / DIV;
		double[] sins = new double[DIV], cosines = new double[DIV];
		{
			double cur = 0;
			for(int i = 0; i < DIV; ++i) {
				sins[i] = Math.sin(cur);
				cosines[i] = Math.cos(cur);
				cur += drad;
			}
		}
		{ //Build the head. on XoZ plane: y = sqrt(x) ( x in [0, 1] )
			int D = 4;
			double dlen = 1.0 / D;
			
			List<double[]> vertices = new ArrayList();
			List<Integer> faces = new ArrayList();
			
			int vertOffset = 0;
			
			double x = 0.0;
			for(int i = 0; i <= D; ++i) { //Loop through the x+ axis
				double y = Math.sqrt(x);
				
				for(int j = 0; j < DIV; ++j) { //Generate the point using the sines and cosines.
					double[] p1 = new double[] { 
						x, y * sins[j], y * cosines[j]
					};
					vertices.add(p1);
				}
				
				x += dlen;
			}
			
			for(int i = 0; i < D; ++i) {
				
				int offset = DIV * i;
				for(int j = 0; j < DIV - 1; ++j) {
					faces.add(offset);
					faces.add(offset + DIV);
					faces.add(offset + DIV + 1);
					faces.add(offset + 1);
					offset++;
				}
				
				faces.add(DIV * i);
				faces.add(offset);
				faces.add(offset + DIV);
				faces.add(DIV * i + DIV);
			}
			
			head.setVertices(vertices.toArray(new double[][] {}));
			head.setQuads(faces.toArray(new Integer[]{}));
		}
		{ //Build the cylinder.
			List<double[]> vertices = new ArrayList();
			List<Integer> faces = new ArrayList();
			
			for(int j = 0; j < DIV; ++j) {
				double[] p1 = new double[] { 
					0, sins[j], cosines[j]
				};
				double[] p2 = new double[] { 
					1, sins[j], cosines[j]
				};
				
				vertices.add(p1);
				vertices.add(p2);
			}
			
			for(int i = 0; i < DIV - 1; ++i) {
				faces.add(i * 2);
				faces.add(i * 2 + 1);
				faces.add(i * 2 + 3);
				faces.add(i * 2 + 2);
			}
			int n = (DIV - 1) * 2;
			faces.add(n);
			faces.add(n + 1);
			faces.add(1);
			faces.add(0);
			
			cylinder.setVertices(vertices.toArray(new double[][] {}));
			cylinder.setQuads(faces.toArray(new Integer[]{}));
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public RendererRayCylinder() {
		SimpleMaterial sm = new SimpleMaterial(null).setIgnoreLight();
		sm.color = new Color().setColor4i(244, 234, 165, 170);
		material = sm;
	}

	@Override
	protected void draw(T ray, Vec3 start, Vec3 end, Vec3 sideDir) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		
		Entity ent = (Entity) ray;
		
		GL11.glRotatef(270 - ent.rotationYaw, 0, 1, 0);
		GL11.glRotatef(ent.rotationPitch, 0, 0, -1);
		
		GL11.glPushMatrix();
		GL11.glScaled(width, width, width);
		head.draw(material);
		GL11.glPopMatrix();
		
		double len = ray.getLength();
		//Draw the cylinder
		GL11.glPushMatrix();
		GL11.glTranslated(width, 0, 0);
		GL11.glScaled(len - width, width, width);
		cylinder.draw(material);
		GL11.glPopMatrix();
		
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
	}

}