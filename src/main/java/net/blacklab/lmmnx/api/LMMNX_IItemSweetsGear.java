package net.blacklab.lmmnx.api;

import java.lang.invoke.MethodHandles;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/** メイドに持たせておくことで特殊効果を発揮するアイテムを追加するためのクラス
 * 発動するにはメイドが雇用済みであり、かつ契約期間が残っていることが条件。
 * IItemSpecialSugarと併せて実装しても良いかもしれない。
 */
public interface LMMNX_IItemSweetsGear{
	
	/** SweetsGearに付加するタグ。
	 * countを正の整数にした場合、同じタグを持つGearの効果は、1update内にcountぶんの回数までしか発動しなくなる。
	 * countを0と指定した場合は、回数制限は無くなる。
	 * 通常staticメンバとして保持されるべき。
	 */
	public static class MultipleTag{
		public String tagName;
		public int count;
		
		public MultipleTag(String s, int i){
			if(s==null) throw new NullPointerException("Name cannot be null");
			if(s.isEmpty()) throw new NullPointerException("Name cannot be empty");
			if(i<0||i>18) throw new IllegalStateException("Invalid count definition "+i);
			tagName = s;
			count = i;
		}
		
		public MultipleTag(String s){
			tagName = s;
			count = 1;
		}
	}
	
	/**MultipleTagの取得。
	 * インターフェースではフィールドを保持できないため実装必須。
	 */
	public MultipleTag getMultipleTag();
	
	/**MultipleTagの設定。
	 * インターフェースではフィールドを保持できないため実装必須。
	 */
	public void setMultipleTag(MultipleTag tag);

	/**SweetsGearをメイドのインベントリに入れているときに、1update毎に実行する処理を定義する。
	 * ただし、1update内でMultipleTagで定義されたcount回数を既に消費してしまった場合にはこのメソッドは呼ばれない。
	 * @param maid Gearを持っているメイド
	 * @param stack 対象のItemStack
	 */
	public void itemInSkirt(LMM_EntityLittleMaid maid, ItemStack stack);
	
	/**1update毎に呼ばれ、Gearの処理が実行されるかどうかを返す。
	 * ただし、1update内でMultipleTagで定義されたcount回数を既に消費してしまった場合には処理は実行されない。
	 * @param maid 対象のメイド
	 * @param stack 対象のItemStack
	 * @return
	 */
	public boolean shouldGearExecute(LMM_EntityLittleMaid maid, ItemStack stack);
	
	/**SweetsGearをメイドが持った時、直後のupdate時に呼ばれる処理。
	 * スロットを動かしただけでも発生するため注意。
	 * @param maid 対象のメイド
	 * @param stack 対象のItemStack
	 */
	public void onGearSet(LMM_EntityLittleMaid maid, ItemStack stack);
	
	/**何らかの理由でメイドのインベントリからSweetsGearが外れてしまった時、直後のupdate時に呼ばれる。
	 * スロットを動かしただけでも発生するため注意。
	 * @param maid 対象のメイド
	 * @param stack 対象のItemStack
	 * @return
	 */
	public void onGearLeft(LMM_EntityLittleMaid maid, ItemStack stack);
}
