package zabuton;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class VZN_BehaviorZabutonDispense extends BehaviorProjectileDispense
{
	protected ItemStack fitemstack;

	@Override
	public ItemStack dispenseStack(IBlockSource par1iBlockSource, ItemStack par2ItemStack) {
		// 色を識別するためにItemStackを確保
		fitemstack = par2ItemStack;
		return super.dispenseStack(par1iBlockSource, par2ItemStack);
	}

	@Override
	protected IProjectile getProjectileEntity(World var1, IPosition var2) {
		return new VZN_EntityZabuton(var1, var2.getX(), var2.getY(), var2.getZ(), (byte)fitemstack.getItemDamage());
	}
}
