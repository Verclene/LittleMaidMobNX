package mmmlibx.lib.guns;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * 銃火器用の基本クラス
 *
 */
public class ItemGunsBase extends ItemBow {

	public static final String Tag_State		= "State";
	public static final String Tag_MaxLoad		= "MaxLoad";
	public static final String Tag_Magazin		= "Magazin";
	public static final String Tag_Burst		= "Burst";
	public static final String Tag_Cycle		= "Cycle";
	public static final String Tag_ReloadTime	= "ReloadTime";
	public static final String Tag_BurstCount	= "BurstCount";
	public static final String Tag_CycleCount	= "CycleCount";
	public static final String Tag_Efficiency	= "Efficiency";
	public static final String Tag_Stability	= "Stability";
	public static final String Tag_StabilityY	= "StabilityY";
	public static final String Tag_StabilityYO	= "StabilityYO";
	public static final String Tag_StabilityP	= "StabilityP";
	public static final String Tag_StabilityPO	= "StabilityPO";
	public static final String Tag_Accuracy		= "Accuracy";

	protected static byte State_Ready		= 0x00;
	protected static byte State_Empty		= 0x10;
	protected static byte State_Reload		= 0x20;
	protected static byte State_ReloadTac	= 0x30;
	protected static byte State_ReloadCre	= 0x40;
	protected static byte State_ReleseMag	= 0x50;
	protected static byte State_ReloadEnd	= 0x60;
	
	/** 空打ちした時の音 */
	public String soundEmpty;
	/** マガジンを外した時（リロード開始時）の音 */
	public String soundRelease;
	/** マガジンを入れた時（リロード完了時）の音 */
	public String soundReload;
	/** ボリューム */
	public float volume;
	
	/** リロードに掛る時間 */
	public int reloadTime;
	/** 最大点射数 */
	public int burstCount;
	/** 発射インターバル */
	public short cycleCount;
	
	/** 弾速のエネルギー効率 */
	public float efficiency;
	/** 銃身の安定性 */
	public float stability;
	/** 発射時の腕の動き左右、乱数 */
	public float stabilityYaw;
	/** 発射時の腕の動き左右、固定値 */
	public float stabilityYawOffset;
	/** 発射時の腕の動き上下、乱数 */
	public float stabilityPitch;
	/** 発射時の腕の動き上下、固定値 */
	public float stabilityPitchOffset;
	/** 集弾性 */
	public float accuracy;
	/** 使用可能弾薬名 */
	public String[] bullets;
	/** アイコン名称 */
	public String[] iconNames;
	
	//protected IIcon[] icons;
	protected Item[] ammos;


	public ItemGunsBase() {
		maxStackSize = 1;
		setFull3D();
		
		volume = 0.5F;
		reloadTime = 40;
		burstCount = 0;
		cycleCount = 2;
		efficiency = 1.0F;
		stability = 1.0F;
		stabilityPitch = 5.0F;
		stabilityPitchOffset = 5.0F;
		stabilityYaw = 3.0F;
		stabilityYawOffset = 0F;
		accuracy = 1.0F;
		
		iconNames = new String[] {"", "", ""};
		bullets = new String[] {""};
		
		GunsBase.appendItem(this);
	}

	public void init() {
		ammos = new Item[bullets.length];
		for (int li = 0; li < bullets.length; li++) {
			ammos[li] = (Item)Item.itemRegistry.getObject(bullets[li]);
		}
	}

	public void playSoundEmpty(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, soundEmpty,
				volume, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public void playSoundRelease(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, soundRelease,
				volume, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public void playSoundReload(World pWorld, EntityPlayer pPlayer, ItemStack pGun) {
		pWorld.playSoundAtEntity(pPlayer, soundReload,
				volume, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
	}

	public int getReloadTime(ItemStack pGun) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_ReloadTime)) {
			return pGun.getTagCompound().getInteger(Tag_ReloadTime);
		}
		return reloadTime;
	}

	public int getBurstCount(ItemStack pGun) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_BurstCount)) {
			return pGun.getTagCompound().getInteger(Tag_BurstCount);
		}
		return burstCount;
		
	}

	public short getCycleCount(ItemStack pGun) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_CycleCount)) {
			return pGun.getTagCompound().getShort(Tag_CycleCount);
		}
		return cycleCount;
	}

	public float getEfficiency(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_Efficiency)) {
			return pGun.getTagCompound().getFloat(Tag_Efficiency);
		}
		return efficiency;
	}

	public float getStability(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_Stability)) {
			return pGun.getTagCompound().getFloat(Tag_Stability);
		}
		return stability;
	}

	public float getStabilityY(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_StabilityY)) {
			return pGun.getTagCompound().getFloat(Tag_StabilityY);
		}
		return stabilityYaw;
	}

	public float getStabilityYO(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_StabilityYO)) {
			return pGun.getTagCompound().getFloat(Tag_StabilityYO);
		}
		return stabilityYawOffset;
	}

	public float getStabilityP(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_StabilityP)) {
			return pGun.getTagCompound().getFloat(Tag_StabilityP);
		}
		return stabilityPitch;
	}

	public float getStabilityPO(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_StabilityPO)) {
			return pGun.getTagCompound().getFloat(Tag_StabilityPO);
		}
		return stabilityPitchOffset;
	}

	public float getAccuracy(ItemStack pGun, EntityPlayer pPlayer, int pUseCount) {
		if (pGun.hasTagCompound() && pGun.getTagCompound().hasKey(Tag_Accuracy)) {
			return pGun.getTagCompound().getFloat(Tag_Accuracy);
		}
		return accuracy;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		// TODO 取り敢えず付けた
		NBTTagCompound ltag = getTagCompound(stack);
		if (ltag.hasKey(Tag_MaxLoad)) {
			return ltag.getInteger(Tag_MaxLoad);
		}
		return super.getMaxDamage();
	}

	protected NBTTagCompound getTagCompound(ItemStack pGun) {
		if (!pGun.hasTagCompound()) {
			pGun.setTagCompound(new NBTTagCompound());
		}
		return pGun.getTagCompound();
	}


	/**
	 * マガジンのからスロットをスキップするかどうか
	 * @return
	 */
	public boolean isSkipBlank() {
		return true;
	}

	/**
	 * 連射するかどうか、falseの時は通常の弓と同じ。
	 * @param pGun
	 * @return
	 */
	public boolean isBurst(ItemStack pGun) {
		return getBurstCount(pGun) > 0;
	}

	//1.8後回し
	/*
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		// ItemBowで再定義されているので標準に戻す。
		// 必要なら上書きすること。
		icons = new IIcon[3];
//		iconArray[0] = par1IconRegister.registerIcon(getIconString());
//		iconArray[1] = par1IconRegister.registerIcon(getIconString() + "_Empty");
//		iconArray[2] = par1IconRegister.registerIcon(getIconString() + "_Release");
		icons[0] = par1IconRegister.registerIcon(iconNames[0]);
		icons[1] = par1IconRegister.registerIcon(iconNames[1]);
		icons[2] = par1IconRegister.registerIcon(iconNames[2]);
		itemIcon = icons[0];
	}
	

	@Override
	public IIcon getItemIconForUseDuration(int par1) {
		// ItemBowからの継承、引き時間によってアイコンを変更する場合は此処でアイコンを返す。
		// 但し、有効かどうかは不明。
		return itemIcon;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		int li = getState(stack);
		if (li >= State_ReleseMag && li < State_ReloadEnd) {
			return icons[2];
		} else
		if (li >= State_Empty && li < State_ReleseMag) {
			return icons[1];
		} else {
			return icons[0];
		}
	}
	*/

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Forgeのイベントハンドラどうする？
		GunsBase.Debug("%s - trigger", par3EntityPlayer instanceof EntityPlayerMP ? "MP" : "SP");
		int li = getState(par1ItemStack);
		if (par3EntityPlayer.isSwingInProgress) {
			setState(par1ItemStack, State_ReloadTac);
			GunsBase.Debug("Tactical Reload.");
		} else {
			if (isBurst(par1ItemStack)) {
				// 連射
				if (li >= State_Empty && li < State_Reload) {
					if (hasAmmo(par1ItemStack, par2World, par3EntityPlayer)) {
						// リロード
						setState(par1ItemStack, State_Reload);
						GunsBase.Debug("Reload.");
					} else {
						// 空打ち
						playSoundEmpty(par2World, par3EntityPlayer, par1ItemStack);
						GunsBase.Debug("Empty.");
					}
				} else if (li < State_Empty) {
					// 発射可能
					resetBolt(par1ItemStack);
					resetBurst(par1ItemStack);
				}
			} else {
				// 単発
				if (isAmmoEmpty(par1ItemStack) && li < State_Reload) {
					if (hasAmmo(par1ItemStack, par2World, par3EntityPlayer)) {
						setState(par1ItemStack, State_Reload);
						GunsBase.Debug("Reload.");
					}
				}
			}
		}
		// 撃つ
		GunsBase.Debug("%s - ItemStack: %s",
				par3EntityPlayer instanceof EntityPlayerMP ? "MP" : "SP",
				par1ItemStack.toString()
				);
		GunsBase.setUncheckedItemStack(par1ItemStack, par3EntityPlayer);
		par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		int li = getState(stack);
		if (li == State_Reload) {
			setState(stack, State_ReleseMag);
			releaseMagazin(stack, player.worldObj, player);
			GunsBase.setUncheckedItemStack(stack, player);
		}
		if (li == State_ReloadTac) {
			setState(stack, State_ReleseMag);
			releaseMagazin(stack, player.worldObj, player);
			GunsBase.setUncheckedItemStack(stack, player);
		}
		onFireTick(stack, player.worldObj, player, count, li);
	}

	/**
	 * タイマー処理の独自記述はこちらに書くと良し
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 * @param count
	 * @param pState
	 */
	public void onFireTick(ItemStack pGun, World pWorld, EntityPlayer pPlayer, int count, int pState) {
		if (isBurst(pGun)) {
			if (pState == State_Ready && !isAmmoEmpty(pGun)) {
				if (checkBolt(pGun) && decBurst(pGun) > 0) {
					// 発射
					if (fireBullet(pGun, pWorld, pPlayer, count) <= 0) {
						setState(pGun, State_Empty);
					}
				}
				GunsBase.setUncheckedItemStack(pGun, pPlayer);
			}
		}
	}

	/*
	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		int li = getState(par1ItemStack);
		if (li >= State_ReleseMag && li < State_ReloadEnd) {
			reloadMagazin(par1ItemStack, par2World, par3EntityPlayer);
			setState(par1ItemStack, State_ReloadEnd);
			GunsBase.setUncheckedItemStack(par1ItemStack, par3EntityPlayer);
		}
		return par1ItemStack;
	}
	*/

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer, int par4) {
		int li = getState(par1ItemStack);
		if (li == State_ReloadEnd) {
			if (isAmmoEmpty(par1ItemStack)) {
				setState(par1ItemStack, State_Empty);
			} else {
				setState(par1ItemStack, State_Ready);
			}
		} else if (!isBurst(par1ItemStack)) {
			if (!isAmmoEmpty(par1ItemStack)) {
				// 弾があるので発射
				if (fireBullet(par1ItemStack, par2World, par3EntityPlayer, par4) <= 0) {
					// 撃ち尽くした
					setState(par1ItemStack, State_Empty);
				}
			} else if (li < State_Reload) {
				// 弾がないので空打ち
				playSoundEmpty(par2World, par3EntityPlayer, par1ItemStack);
			} else {
//				setState(par1ItemStack, State_Empty);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		// 射撃時と装填時で挙動を変える
		int li = getState(par1ItemStack);
		if (li >= State_Empty && li < State_ReloadEnd) {
			return getReloadTime(par1ItemStack);
		}
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		int li = getState(par1ItemStack);
		if (li < State_ReloadTac) {
			return EnumAction.BOW;
		}
		return EnumAction.BLOCK;
	}

	/**
	 * 弾薬を所持しているか？
	 * @param par1ItemStack
	 * @param par2World
	 * @param par3EntityPlayer
	 * @return
	 */
	public boolean hasAmmo(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		return par3EntityPlayer.capabilities.isCreativeMode || (getAmmoIndex(par3EntityPlayer) > -1);
	}

	/**
	 * 弾体の発射
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 * @param pUseCount
	 * @return 射撃後の残弾数
	 */
	public int fireBullet(ItemStack pGun, World pWorld, EntityPlayer pPlayer, int pUseCount) {
		int ldamage = getDamage(pGun);
		int lmdamage = getMaxDamage(pGun);
		ItemStack lbullet;
		do {
			// 弾薬を取り出す
			lbullet = getBullet(pGun, ldamage);
			ldamage++;
			// 残弾数を減らす
			setDamage(pGun, ldamage);
			if (lbullet != null) break;
		} while(!isSkipBlank() && ldamage < lmdamage);
		// 戻り値として再設定、残弾数を返す。
		ldamage = lmdamage - ldamage;
		if (lbullet == null) return ldamage;
		
		ItemBulletBase libullet = null;
		if (lbullet.getItem() instanceof ItemBulletBase) {
			libullet = (ItemBulletBase)lbullet.getItem();
		}
		// 発射音、弾薬ごとに音声を設定
		if (libullet != null) {
			libullet.playSoundFire(pWorld, pPlayer, pGun, lbullet);
		}
		
		// 弾体を発生させる
		if (!pWorld.isRemote) {
			Entity lentity;
			GunsBase.Debug("Bulle: %s-%s",
					lbullet == null ? "NULL" : lbullet.toString(),
					lbullet.hasTagCompound() ? lbullet.getTagCompound().toString() : "");
			if (libullet != null) {
				lentity = libullet.getBulletEntity(pGun, lbullet, pWorld, pPlayer, 72000 - pUseCount);
				pWorld.spawnEntityInWorld(lentity);
//				onRecoile(pGun, pWorld, pPlayer, 72000 - pUseCount);
			}
		}
		if (libullet != null) {
			onRecoile(pGun, lbullet, pWorld, pPlayer, 72000 - pUseCount);
		}
		return ldamage;
	}

	/**
	 * 発射時のリコイル動作を記述
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 * @param pUseCount
	 */
	public void onRecoile(ItemStack pGun, ItemStack pBullet, World pWorld, EntityPlayer pPlayer, int pUseCount) {
		// しゃがみの時は少し早く照準が安定する
		float lsn = pPlayer.isSneaking() ? 0.5F : 1.0F;
		lsn *= ((ItemBulletBase)pBullet.getItem()).getReaction(pBullet);
		// 腕の動き
		pPlayer.rotationPitch -= (pPlayer.getRNG().nextFloat() * getStabilityP(pGun, pPlayer, pUseCount)
				+ getStabilityPO(pGun, pPlayer, pUseCount)) * lsn;
		pPlayer.rotationYaw += (pPlayer.getRNG().nextFloat() * getStabilityY(pGun, pPlayer, pUseCount)
				+ getStabilityYO(pGun, pPlayer, pUseCount)) * lsn;
		// 後ろに吹っ飛ぶ
		lsn *= getStability(pGun, pPlayer, pUseCount);
		pPlayer.motionX += MathHelper.sin(pPlayer.rotationYawHead * 0.01745329252F) * lsn;
		pPlayer.motionZ -= MathHelper.cos(pPlayer.rotationYawHead * 0.01745329252F) * lsn;
	}

	/**
	 * 装填されている弾を返す。
	 * @param pGun
	 * @param pIndex
	 * @return
	 */
	public ItemStack getBullet(ItemStack pGun, int pIndex) {
		if (pGun.hasTagCompound()) {
			NBTTagCompound ltag = pGun.getTagCompound();
			NBTTagCompound lbullet = ltag.getCompoundTag(Tag_Magazin);
			String ls = String.format("%04d", pIndex);
			if (lbullet.hasKey(ls)) {
				return ItemStack.loadItemStackFromNBT(lbullet.getCompoundTag(ls));
			}
		}
		return null;
	}

	public void setBullet(ItemStack pGun, int pIndex, ItemStack pBullet) {
		NBTTagCompound ltag = getTagCompound(pGun);
		NBTTagCompound lmagazin = ltag.getCompoundTag(Tag_Magazin);
		ltag.setTag(Tag_Magazin, lmagazin);
		String ls = String.format("%04d", pIndex);
		if (pBullet == null) {
			lmagazin.removeTag(ls);
		} else {
			NBTTagCompound lbullet = ltag.getCompoundTag(ls);
			lmagazin.setTag(ls, lbullet);
			pBullet.writeToNBT(lbullet);
		}
	}

	/**
	 * 弾を装填する、装填する際にはスタックから減らす。
	 * @param pGun
	 * @param pIndex
	 * @param pBullet
	 */
	public void loadBullet(ItemStack pGun, ItemStack pBullet) {
		int li = getDamage(pGun);
		while (li > 0) {
			li--;
			ItemStack lis = getBullet(pGun, li);
			pGun.setItemDamage(li);
			if (lis == null) {
				setBullet(pGun, li, pBullet.splitStack(1));
				break;
			}
		}
	}

	/**
	 * 残弾確認
	 * @param pGun
	 * @return
	 */
	public boolean isAmmoEmpty(ItemStack pGun) {
		return getDamage(pGun) >= getMaxDamage(pGun);
	}

	/**
	 * 使用可能な弾薬を判定する
	 * @param pItemStack
	 * @return
	 */
	public boolean checkAmmo(ItemStack pItemStack) {
		Item litem = pItemStack.getItem();
		for (Item li : ammos) {
			if (litem == li) return true;
		}
		return false;
	}

	/**
	 * インベントリから弾薬を検索する
	 * @param pPlayer
	 * @return
	 */
	public int getAmmoIndex(EntityPlayer pPlayer) {
		for (int li = 0; li < pPlayer.inventory.mainInventory.length; li++) {
			ItemStack lis = pPlayer.inventory.mainInventory[li];
			if (lis != null && checkAmmo(lis)) {
				return li;
			}
		}
		return -1;
	}

	/**
	 * マガジンを取り外す。
	 * @param pGun
	 * @param pWorld
	 * @param pPlayer
	 */
	public void releaseMagazin(ItemStack pGun, World pWorld, EntityPlayer pPlayer) {
		playSoundRelease(pWorld, pPlayer, pGun);
		if (!pPlayer.capabilities.isCreativeMode) {
			// マガジンから使用済みのカートを取り出す（Creativeの時はマガジンの内容が変わらない）
			for (int li = 0; li < getDamage(pGun); li++) {
				setBullet(pGun, li, null);
			}
		}
		GunsBase.Debug(pGun.toString());
		for (int li = 0; li < getMaxDamage(pGun); li++) {
			ItemStack lis = getBullet(pGun, li);
			GunsBase.Debug("%04d: %s", li, lis == null ? "null" : lis.toString());
		}
		
		setDamage(pGun, getMaxDamage(pGun));
	}

	/**
	 * リロード完了
	 * @param stack
	 * @param player
	 */
	public void reloadMagazin(ItemStack pGun, World pWorld, EntityPlayer pPlayer) {
		while (getDamage(pGun) > 0) {
			int li = getAmmoIndex(pPlayer);
			if (li == -1) {
				break;
			}
			ItemStack lis = pPlayer.inventory.mainInventory[li];
			loadBullet(pGun, lis);
			if (lis.stackSize <= 0) {
				pPlayer.inventory.setInventorySlotContents(li, null);
			}
		}
		playSoundReload(pWorld, pPlayer, pGun);
	}

	// 状態ステータス

	public void setState(ItemStack pGun, byte pState) {
		NBTTagCompound ltag = getTagCompound(pGun);
		ltag.setByte(Tag_State, pState);
	}

	public byte getState(ItemStack pGun) {
		NBTTagCompound ltag = getTagCompound(pGun);
		return ltag.getByte(Tag_State);
	}

	// 発射間隔の算出

	public boolean checkBolt(ItemStack pGun) {
		NBTTagCompound ltag = getTagCompound(pGun);
		short lval = ltag.getShort(Tag_Cycle);
		if (--lval <= 0) {
			ltag.setShort(Tag_Cycle, getCycleCount(pGun));
			return true;
		}
		ltag.setShort(Tag_Cycle, lval);
		return false;
	}

	public void resetBolt(ItemStack pGun) {
		NBTTagCompound ltag = getTagCompound(pGun);
		ltag.setShort(Tag_Cycle, getCycleCount(pGun));
	}

	// 連射カウント

	public int decBurst(ItemStack pGun) {
		NBTTagCompound ltag = getTagCompound(pGun);
		int lburst = ltag.getInteger(Tag_Burst);
		if (lburst > 0) {
			ltag.setInteger(Tag_Burst, lburst - 1);
		}
		return lburst;
	}

	public void resetBurst(ItemStack pGun) {
		NBTTagCompound ltag = getTagCompound(pGun);
		ltag.setInteger(Tag_Burst, getBurstCount(pGun));
	}


}
