2015/06/04

□環境構築について

	Minecraft Forge-src開発環境にソースコードをインポートする際の注意事項です。

■1
	LMMNXはmodsディレクトリにある「特定の文字列から始まる名前」のパッケージを読み取って処理するのですが
	デフォルトではmodsディレクトリには何も入っておらず、ソースがコンパイルされた時の一時ファイルの名前も上記のものとは異なります。
	このため
	eclipse/mods に
	littleMaidMobNX-(VERSION).jar
	を入れないとeclipse上で実行する際メイドがスポーンしません。
	
	このとき、eclipse/modsに入れたパッケージ内の、次のファイル・フォルダを削除してください。
	削除は一度解凍するか、7zip等を使用してください。
	・assets, littleMaidMobX, mmmlibx 以外のフォルダ
	・littleMaidMobX/LMM_LittleMaidMobX.class
	・mmmlibx/lib/MMMLib.class

■2
	eclipse/mods にマルチモデルを入れれば読み取られます。サウンドは未対応です。