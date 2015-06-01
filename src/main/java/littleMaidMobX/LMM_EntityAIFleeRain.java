package littleMaidMobX;

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class LMM_EntityAIFleeRain extends EntityAIBase implements LMM_IEntityAI {

	protected EntityCreature theCreature;
	protected double shelterX;
	protected double shelterY;
	protected double shelterZ;
	protected float movespeed;
	protected World theWorld;
	protected boolean isEnable;

	public LMM_EntityAIFleeRain(EntityCreature par1EntityCreature, float pMoveSpeed) {
		theCreature = par1EntityCreature;
		movespeed = pMoveSpeed;
		theWorld = par1EntityCreature.worldObj;
		isEnable = false;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (!isEnable || !theWorld.isRaining()) {
			return false;
		}

		if (!theCreature.isWet()) {
			return false;
		}

		if (!theWorld.canBlockSeeSky(new BlockPos(
				MathHelper.floor_double(theCreature.posX),
				(int) theCreature.getEntityBoundingBox().minY,
				MathHelper.floor_double(theCreature.posZ)))) {
			return false;
		}

		Vec3 vec3d = findPossibleShelter();

		if (vec3d == null) {
			return false;
		} else {
			shelterX = vec3d.xCoord;
			shelterY = vec3d.yCoord;
			shelterZ = vec3d.zCoord;
			return true;
		}
	}

	@Override
	public boolean continueExecuting() {
		return !theCreature.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		theCreature.getNavigator().tryMoveToXYZ(shelterX, shelterY, shelterZ, movespeed);
	}

	private Vec3 findPossibleShelter() {
		Random random = theCreature.getRNG();
		
		for (int i = 0; i < 10; i++) {
			int j = MathHelper.floor_double((theCreature.posX +
					(double) random.nextInt(20)) - 10D);
			int k = MathHelper.floor_double((theCreature.getEntityBoundingBox().minY +
					(double) random.nextInt(6)) - 3D);
			int l = MathHelper.floor_double((theCreature.posZ +
					(double) random.nextInt(20)) - 10D);
			
			if (!theWorld.canBlockSeeSky(new BlockPos(j, k, l))/*
					&& theCreature.getBlockPathWeight(j, k, l) > -0.5F*/) {
				return new Vec3(j, k, l);
			}
		}
		
		return null;
	}

	// 実行可能フラグ
	@Override
	public void setEnable(boolean pFlag) {
		isEnable = pFlag;
	}

	@Override
	public boolean getEnable() {
		return isEnable;
	}

}
