package littleMaidMobX;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;

public class LMM_EntityAIFollowOwner extends EntityAIBase implements LMM_IEntityAI {

	private LMM_EntityLittleMaid theMaid;
	private Entity theOwner;
	private float moveSpeed;
	private PathNavigate petPathfinder;
	private int field_48310_h;
	protected double maxDist;
	protected double minDist;
	protected double sprintDist;
	protected double toDistance;
	protected boolean isEnable;

	public LMM_EntityAIFollowOwner(LMM_EntityLittleMaid par1EntityLittleMaid,
			float pSpeed, double pMin, double pMax, double pSprintDistSQ) {
		theMaid = par1EntityLittleMaid;
		moveSpeed = pSpeed;
		petPathfinder = par1EntityLittleMaid.getNavigator();
		minDist = pMin;
		maxDist = pMax;
		sprintDist = pSprintDistSQ;
		isEnable = true;
		setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		LMM_LittleMaidMobNX.Debug("SHOULD TASK FOLLOW");
		if (!isEnable)
			return false;

		Entity entityliving = theMaid.getOwner();
		if (entityliving == null) {
			return false;
		}

		if (theMaid.isSitting()||theMaid.isMaidWait()) {
			return false;
		}

		toDistance = theMaid.getDistanceSqToEntity(entityliving);
		if (toDistance < minDist && !theMaid.handleWaterMovement()) {
			return false;
		} else {
			theOwner = entityliving;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		LMM_LittleMaidMobNX.Debug("CONTINUE TASK FOLLOW");
		toDistance = theMaid.getDistanceSqToEntity(theOwner);
		if(theMaid.handleWaterMovement())
			return !theMaid.isMaidWait()&&!theMaid.isSitting();
		else return !theMaid.getNavigator().noPath()
				&&(toDistance > maxDist)
				&& !theMaid.isSitting();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		field_48310_h = 0;
		//lastAvoidWater = petPathfinder.getAvoidsWater();
		//petPathfinder.setAvoidsWater(false);
		if(!theMaid.isInWater()) ((PathNavigateGround)this.theMaid.getNavigator()).func_179690_a(false);
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		LMM_LittleMaidMobNX.Debug("RESET TASK FOLLOW");
		theMaid.setSprinting(false);
		theOwner = null;
		if(!theMaid.isInWater()) ((PathNavigateGround)this.theMaid.getNavigator()).func_179690_a(true);
		petPathfinder.clearPathEntity();
		//petPathfinder.setAvoidsWater(lastAvoidWater);
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		LMM_LittleMaidMobNX.Debug("UPDATE TASK FOLLOW");
		theMaid.getLookHelper().setLookPositionWithEntity(theOwner, 10F,
				theMaid.getVerticalFaceSpeed());

		if (theMaid.isSitting()) {
			return;
		}
		// 指定距離以上ならダッシュ
		if(!theMaid.handleWaterMovement()){
			theMaid.setSprinting(toDistance > sprintDist);
			if (--field_48310_h > 0) {
				return;
			}
		}

		field_48310_h = 10;

		PathEntity entity = theMaid.getNavigator().getPathToEntityLiving(theOwner);
		if(entity==null){
			LMM_LittleMaidMobNX.Debug("PATH NULL");
			return;
		}
		theMaid.getNavigator().setPath(entity, moveSpeed);
	}

	@Override
	public void setEnable(boolean pFlag) {
		isEnable = pFlag;
	}

	@Override
	public boolean getEnable() {
		return isEnable;
	}

}
