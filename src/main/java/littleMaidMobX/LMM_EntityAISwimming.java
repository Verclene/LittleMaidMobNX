package littleMaidMobX;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public class LMM_EntityAISwimming extends EntityAISwimming {

	protected EntityLiving theEntity;
	
	public LMM_EntityAISwimming(EntityLiving par1EntityLiving) {
		super(par1EntityLiving);
		theEntity = par1EntityLiving;
	}

	@Override
	public boolean shouldExecute() {
		if(theEntity instanceof LMM_EntityLittleMaid){
			if(theEntity.handleWaterMovement()) return true;
		}
		return ((theEntity.getNavigator().noPath() ?
				(theEntity.isInsideOfMaterial(Material.water)) : theEntity.isInWater())
				|| theEntity.isInLava());
	}

	@Override
	public void updateTask() {
		// TODO 自動生成されたメソッド・スタブ
		super.updateTask();
		double totalmotionY = 0d;
		if(theEntity instanceof LMM_EntityLittleMaid){
			if(theEntity.isInLava()){
//				theEntity.motionY+=1.0D;
				theEntity.getJumpHelper().setJumping();
				return;
			}
			LMM_EntityLittleMaid theMaid = (LMM_EntityLittleMaid) theEntity;
			if(theMaid.handleWaterMovement()){
				int x = MathHelper.floor_double(theEntity.posX);
				int z = MathHelper.floor_double(theEntity.posZ);
				int y = MathHelper.floor_double(theEntity.getEntityBoundingBox().minY);
				totalmotionY+= 0.03D*MathHelper.sin(theEntity.ticksExisted/8f);
//				if(theEntity.worldObj.isAnyLiquid(new AxisAlignedBB(x, y, z, x, y+h+1, z))){
//					totalmotionY += 0.05D;
//				}
				
				PathEntity pathEntity = theMaid.prevPathEntity;
				if(pathEntity!=null&&theMaid.swimmingEnabled){
					PathPoint pathPoint = pathEntity.getFinalPathPoint();
					theEntity.motionX = ((pathPoint.xCoord>x)?1:(pathPoint.xCoord<x)?-1:0) * theEntity.getAIMoveSpeed()/3d;
					theEntity.motionZ = ((pathPoint.zCoord>z)?1:(pathPoint.zCoord<z)?-1:0) * theEntity.getAIMoveSpeed()/3d;
					totalmotionY +=		((pathPoint.yCoord>y)?1:(pathPoint.yCoord<y)?-1:0) * theEntity.getAIMoveSpeed()/3d;
				}else{
				}
				if(theMaid.swimmingEnabled&&theEntity.handleWaterMovement()){
					theEntity.motionY = totalmotionY;
				}else{
					theEntity.motionY = 0.04D;
				}
			}
		}
	}

}
