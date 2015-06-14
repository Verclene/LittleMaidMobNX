2015/06/04

□開発環境構築について

    ソースコードはhttp://github.com/Verclene/LittleMaidMobNXに公開しています。
    開発キットをhttp://forum.minecraftuser.jp/viewtopic.php?f=13&t=26848#p233886からダウンロードできます。ご利用ください。
    以下は、その際に注意すべき事項です。

■1
	LMMNXはmodsディレクトリにある「特定の文字列から始まる名前」のパッケージを読み取って処理するのですが
	デフォルトではmodsディレクトリには何も入っておらず、ソースがコンパイルされた時の一時ファイルの名前も上記のものとは異なります。
	このため
	eclipse/mods に
	littleMaidMobNX-(VERSION).jar ※プレイ用に配布しているものでOK
	を入れないとeclipse上で実行する際メイドがスポーンしません。
	
	このとき、eclipse/modsに入れたパッケージ内の、次のファイル・フォルダを削除してください。
	削除は一度解凍するか、7zip等を使用してください。
	・assets, littleMaidMobX, mmmlibx 以外のフォルダ
	・littleMaidMobX/LMM_LittleMaidMobX.class
	・mmmlibx/lib/MMMLib.class

■2
	eclipse/mods にマルチモデルを入れれば読み取られます。サウンドもmodsに入れてOKです。