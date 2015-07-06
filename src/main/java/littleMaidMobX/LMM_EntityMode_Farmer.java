package littleMaidMobX;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class LMM_EntityMode_Farmer extends LMM_EntityModeBase {
	
	// EntityMode: Farmer Added on NX2
	
	public static final int mmode_Farmer = 0x00F0;


	public LMM_EntityMode_Farmer(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
		isAnytimeUpdate = true;
	}

	@Override
	public int priority() {
		return 6300;
	}

	@Override
	public void init() {
		/* langファイルに移動
		ModLoader.addLocalization("littleMaidMob.mode.Torcher", "Torcher");
		ModLoader.addLocalization("littleMaidMob.mode.F-Torcher", "F-Torcher");
		ModLoader.addLocalization("littleMaidMob.mode.D-Torcher", "D-Torcher");
		ModLoader.addLocalization("littleMaidMob.mode.T-Torcher", "T-Torcher");
		*/
		LMM_TriggerSelect.appendTriggerItem(null, "Hoe", "");
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		// Torcher:0x0020
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;
		
		owner.addMaidMode(ltasks, "Farmer", mmode_Farmer);
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
		if (litemstack != null) {
			//トリガーはもちろんクワ
			if (litemstack.getItem() instanceof ItemHoe || LMM_TriggerSelect.checkWeapon(owner.getMaidMaster(), "Hoe", litemstack)) {
				owner.setMaidMode("Farmer");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setMode(int pMode) {
		switch (pMode) {
		case mmode_Farmer :
			owner.setBloodsuck(false);
			owner.aiAttack.setEnable(false);
			owner.aiShooting.setEnable(false);
			return true;
		}
		
		return false;
	}

	@Override
	public int getNextEquipItem(int pMode) {
		int li;
		ItemStack litemstack;
		
		// モードに応じた識別判定、速度優先
		switch (pMode) {
		case mmode_Farmer : 
			for (li = 0; li < owner.maidInventory.maxInventorySize; li++) {
				litemstack = owner.maidInventory.getStackInSlot(li);
				if (litemstack == null) continue;
				
				if (litemstack.getItem() instanceof ItemHoe || LMM_TriggerSelect.checkWeapon(owner.getMaidMaster(), "Hoe", litemstack)) {
					return li;
				}
			}
			break;
		}
		
		return -1;
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return pItemStack.getItem() instanceof ItemHoe;
	}

	@Override
	public boolean isSearchBlock() {
		return !owner.isMaidWait();
	}

	@Override
	public boolean shouldBlock(int pMode) {
		return !(owner.getCurrentEquippedItem() == null);
	}

	public static final double limitDistance_Freedom = 361D;
	public static final double limitDistance_Follow  = 100D;

	protected boolean isBlockFarmed(int px, int py, int pz) {
		//基本まつあきの使い回し

		World worldObj = owner.worldObj;
		//離れすぎている
		if(owner.isFreedom()){
			//自由行動時
			if(owner.func_180486_cf().distanceSqToCenter(px,py,pz) > limitDistance_Freedom){
				return false;
			}
		}else{
			//追従してる時に農業することなくね？
			if(owner.getMaidMasterEntity()!=null){
				if(owner.getMaidMasterEntity().getPosition().distanceSqToCenter(px,py,pz) > limitDistance_Follow){
					return false;
				}
			}
		}
		
		if (!owner.isMaidWait()) {
			return Block.isEqualTo(worldObj.getBlockState(new BlockPos(px,py,pz)).getBlock(), Blocks.farmland);
		}
		return false;
	}
	
	protected int getCropAge(int px, int py, int pz) {
		//作物の成長度合いを返す。
		//作物でない場合や距離が遠すぎる場合は-1。

		World worldObj = owner.worldObj;
		//離れすぎている
		if(owner.isFreedom()){
			//自由行動時
			if(owner.func_180486_cf().distanceSqToCenter(px,py,pz) > limitDistance_Freedom){
				return -1;
			}
		}else{
			//追従してる時に農業することなくね？
			if(owner.getMaidMasterEntity()!=null){
				if(owner.getMaidMasterEntity().getPosition().distanceSqToCenter(px,py,pz) > limitDistance_Follow){
					return -1;
				}
			}
		}
		
		if (!owner.isMaidWait()) {
			IBlockState b = worldObj.getBlockState(new BlockPos(px,py,pz));
			if(b.getBlock() instanceof BlockCrops){
				try{
					return (int) b.getValue(BlockCrops.AGE);
				}catch(ClassCastException e){
					return -1;
				}
			}
		}
		return -1;
	}
	
	@Override
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		if (isBlockFarmed(px, py, pz) && canBlockBeSeen(px, py - 1, pz, true, true, false) && !owner.isMaidWait()) {		
			if (owner.getNavigator().tryMoveToXYZ(px, py, pz, 1.0F) ) {
				//owner.playLittleMaidSound(LMM_EnumSound.findTarget_D, true);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean executeBlock(int pMode, int px, int py, int pz) {
		ItemStack lis = owner.getCurrentEquippedItem();
		if (lis == null) return false;
		
		if(lis.getItem()!=Item.getItemFromBlock(Blocks.torch)) return false;
		
		int li = lis.stackSize;
		// TODO:当たり判定をどうするか
		if (lis.onItemUse(owner.maidAvatar, owner.worldObj, new BlockPos(px, py - 1, pz), EnumFacing.UP, 0.5F, 1.0F, 0.5F)) {
			owner.setSwing(10, LMM_EnumSound.installation, false);
			
			if (owner.maidAvatar.capabilities.isCreativeMode) {
				lis.stackSize = li;
			}
			if (lis.stackSize <= 0) {
				owner.maidInventory.setInventoryCurrentSlotContents(null);
				owner.getNextEquipItem();
			}
		}
		return false;
	}

	public boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, EnumFacing par5,
			EntityPlayer par6EntityPlayer, ItemStack par7ItemStack, ItemBlock pItemBlock) {
		// TODO:マルチ対策用、ItemBlockから丸パクリバージョンアップ時は確認すること
		Block var8 = par1World.getBlockState(new BlockPos(par2, par3, par4)).getBlock();
		
		if (Block.isEqualTo(var8, Blocks.snow)) {
			par5 = EnumFacing.UP;
		} else if (!Block.isEqualTo(var8, Blocks.vine) && !Block.isEqualTo(var8, Blocks.tallgrass) &&
				!Block.isEqualTo(var8, Blocks.deadbush)) {
			if (par5 == EnumFacing.DOWN) {
				--par3;
			}
			if (par5 == EnumFacing.UP) {
				++par3;
			}
			if (par5 == EnumFacing.NORTH) {
				--par4;
			}
			if (par5 == EnumFacing.SOUTH) {
				++par4;
			}
			if (par5 == EnumFacing.WEST) {
				--par2;
			}
			if (par5 == EnumFacing.EAST) {
				++par2;
			}
		}
		
		Material lmat = par1World.getBlockState(new BlockPos(par2, par3, par4)).getBlock().getMaterial();
		if (lmat instanceof MaterialLiquid) {
			return false;
		}
		
		return par1World.canBlockBePlaced(Block.getBlockFromItem(pItemBlock), new BlockPos(par2, par3, par4), false, par5, (Entity)null, par7ItemStack);
	}
	
	protected ItemStack hasSeed(){
		return null;
	}

	@Override
	public void updateAITick(int pMode) {
		World world = owner.worldObj;
		int px = MathHelper.floor_double(owner.posX);
		int py = MathHelper.floor_double(owner.posY);
		int pz = MathHelper.floor_double(owner.posZ);
		if (pMode == mmode_Farmer && owner.getNextEquipItem()) {
			//足元のブロックが耕地
			if(isBlockFarmed(px,py-1,pz)){
				//作物が実っている
				if(getCropAge(px,py,pz)==7){
					Blocks.wheat.onBlockDestroyedByPlayer(world, new BlockPos(px,py,pz), world.getBlockState(new BlockPos(px,py,pz)));
				}
				//耕されて空
				if(world.getBlockState(new BlockPos(px,py,pz)).getBlock() == Blocks.air){
					
				}
			}
			
			//周囲3x3のブロックを捜索
			//ただし自分がいる場所は除く
			for(int x=-1;x<2;x++){
				for(int z=-1;z<2;z++){
					if(x==0&&z==0) continue;
					int lx = px+x;
					int lz = pz+z;
					
				}
			}
		}
	}

}
