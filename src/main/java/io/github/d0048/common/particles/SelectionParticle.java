package io.github.d0048.common.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectionParticle extends Particle {


    protected SelectionParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double par8, double par10,
                                double par12) {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, 1.0F, par8, par10, par12);
    }

    protected SelectionParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, float scale, double par8,
                                double par10, double par12) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.15000000149011612D;
        this.motionZ *= 0.10000000149011612D;

        if (par8 == 0.0F) {
            par8 = 1.0F;
        }
        this.particleGreen = 0.71372549019607843137254901960784F;
        this.particleRed = 1F;
        this.particleBlue = 0.75686274509803921568627450980392F;
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * scale);
    }

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ,
                               float rotationYZ, float rotationXY, float rotationXZ) {
        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge * 32.0F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                                       double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new SelectionParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, (float) xSpeedIn, (float) ySpeedIn, (float) zSpeedIn);
        }
    }
}
