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
				(int) theCreature.getEntityBoundingBox().minY+1,
				MathHelper.floor_double(theCreature.posZ)))) {
			return false;
		}

		Vec3 vec3d = findPossibleShelter();

		if (vec3d == null) {
			return false;
		}
		shelterX = vec3d.xCoord;
		shelterY = vec3d.yCoord;
		shelterZ = vec3d.zCoord;
		LMM_LittleMaidMobNX.Debug("SHELTER FOUND %04.2f,%04.2f,%04.2f", shelterX, shelterY, shelterZ);
		return true;
	}

	@Override
	public boolean continueExecuting() {
		return theCreature.getNavigator().noPath()?false:theWorld.canBlockSeeSky(new BlockPos(
				MathHelper.floor_double(theCreature.posX),
				(int) theCreature.getEntityBoundingBox().minY+1,
				MathHelper.floor_double(theCreature.posZ)));
	}

	@Override
	public void startExecuting() {
		LMM_LittleMaidMobNX.Debug("EXECUTE %04.2f,%04.2f,%04.2f", shelterX, shelterY, shelterZ);
		theCreature.getNavigator().tryMoveToXYZ(shelterX, shelterY, shelterZ, movespeed);
	}

	private Vec3 findPossibleShelter() {
		Random random = theCreature.getRNG();
		
		for (int i = 0; i < 10; i++) {
			int j = MathHelper.floor_double((theCreature.posX + (i-5)));
			int k = MathHelper.floor_double((theCreature.getEntityBoundingBox().minY +
					random.nextInt(4)) - 2D);
			int l = MathHelper.floor_double((theCreature.posZ + (i-5)));
			
			//離れすぎている
			if(theCreature.getPosition().distanceSq(j, k, l)>LMM_EntityMode_Torcher.limitDistance_Freedom &&
					((LMM_EntityLittleMaid)theCreature).isFreedom()){
				continue;
			}
			
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
