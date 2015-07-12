2015/06/04

□開発環境構築について

    ソースコードはhttp://github.com/Verclene/LittleMaidMobNXに公開しています。
    以下は、NX1 Build 52以降での開発環境構築の手順になります。

◇ソースコードから構築する場合

1.  Minecraft Forge srcをセットアップ。
    ソースディレクトリはsrc/main/java、リソースディレクトリはsrc/main/resources。
2.  GitHubからソースコードをダウンロードし、srcディレクトリをプロジェクトの直下に置く。
3.  EBLibのdev版をダウンロードし、プロジェクトのdependenciesに追加する。
4.  src/main/java/net/blacklab/lmmnx/util/LMMNX_DevMode.javaを開き、定数DEVMODEの値を変更する。
    ・IDEを使わず開発する場合 = DEVMODE_NO_IDE
    ・eclipseを使用して開発する場合 = DEVMODE_ECLIPSE
    ※ForgeGradleでビルドするときは必ず「NOT_IN_DEV」に戻してください。
    ※IDEAはサポートしていません