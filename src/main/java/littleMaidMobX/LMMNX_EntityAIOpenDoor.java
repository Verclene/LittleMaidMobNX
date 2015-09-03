package littleMaidMobX;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;

public class LMMNX_EntityAIOpenDoor extends EntityAIOpenDoor {
	
	protected LMM_EntityLittleMaid theMaid;

	public LMMNX_EntityAIOpenDoor(EntityLiving p_i1644_1_, boolean p_i1644_2_) {
		super(p_i1644_1_, p_i1644_2_);
		// TODO 自動生成されたコンストラクター・スタブ
		if(p_i1644_1_ instanceof LMM_EntityLittleMaid)
			theMaid = (LMM_EntityLittleMaid) p_i1644_1_;
	}

	@Override
	public boolean shouldExecute() {
		// TODO 水中行動時のラップをしただけ
		if(theMaid==null) return false;
		if(!theMaid.isSwimming||!theMaid.isInWater()) {
			return super.shouldExecute();
		}else return false;
	}
	
}
