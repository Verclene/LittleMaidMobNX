package net.blacklab.lmmnx.api;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public interface LMMNX_IItemSpecialSugar {
	
	/** インベントリに入れておいた実装アイテムをメイドが契約更新時に食べた時に発揮する効果。
	 * @param maid 対象のメイドを示すインスタンス
	 */
	public abstract void onSugarEatenRecontract(LMM_EntityLittleMaid maid);
	
	/** インベントリに入れておいた実装アイテムをメイドが回復時に食べた時に発揮する効果。
	 * @param maid 対象のメイドを示すインスタンス
	 * @param toHeal falseの場合はつまみ食い
	 */
	public abstract void onSugarEatenHeal(LMM_EntityLittleMaid maid, boolean toHeal);
	
	/** 実装アイテムを直接与えた時の処理。
	 * @param world
	 * @param player 砂糖を与えたプレイヤー
	 * @param maid 砂糖を与えられたメイド
	 * @return falseを返すとモード切替をしなくなる。
	 */
	public abstract boolean onSugarInteract(World world, EntityPlayer player, LMM_EntityLittleMaid maid);

}
