package littleMaidMobX;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAISwimming;

public class LMM_EntityAISwimming extends EntityAISwimming {

	protected EntityLiving theEntity;
	
	public LMM_EntityAISwimming(EntityLiving par1EntityLiving) {
		super(par1EntityLiving);
		theEntity = par1EntityLiving;
	}

	@Override
	public boolean shouldExecute() {
		// 足がつくなら泳がない
		return (theEntity.getNavigator().noPath() ?
				(/*!theEntity.onGround || */theEntity.isInsideOfMaterial(Material.water)) : theEntity.isInWater())
				|| theEntity.handleLavaMovement();
	}

}
