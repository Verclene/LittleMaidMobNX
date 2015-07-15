package net.blacklab.lmmnx.api;

import littleMaidMobX.LMM_EntityLittleMaid;

/** メイドに持たせておくことで特殊効果を発揮するアイテムを追加するためのインターフェース。
 * インターフェースなのでIItemSpecialSugarと併用するのも面白いかもしれない。
 */
public interface LMMNX_IItemSweetsGear {
	/** メイドのインベントリに入れている際の常時処理。
	 * このメソッドはonLivingUpdate毎に呼ばれるので、あまり重い処理を書かないように。
	 * @param maid 対象となるメイド
	 */
	public void itemInsideSkirt(LMM_EntityLittleMaid maid);
}
