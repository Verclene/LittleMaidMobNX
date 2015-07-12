#LittleMaidMobNX
This project is unofficial port of LittleMaidMob to Minecraft 1.8.

Built on Forge 11.14.3.1450
Based on LittleMaidMobX-1.7.x-0.0.9-pre3, made by EMB4.
This is open-source project, use it on NON-COMMERCIAL.

**REQUIRES EBLib: CLONE 'EBLib-1.8' OR ADD EBLib-dev TO BUILD-PATH**

VERSION: NX1 Bulid 52

#How to create Dev Environments

1. Setup Forge-src. Set runDir prop to 'eclipse'.
2. Download source of LMMNX from 'Release page' and put 'src' directory into the project dir. If you set Forge-src up multi-project, You can use 'git clone' instead.
3. Download [EBLib-dev 0.1.1](http://1drv.ms/1bVlUPl) and put it into 'libs' dir in the project dir.
4. Open 'src/main/java/net/blacklab/lmmnx/util/LMMNX_DevMode.java', and rewrite the value of 'DEVMODE'. If you are not using any IDE, change to 'DEVMODE_NO_IDE', or using eclipse, to 'DEVMODE_ECLIPSE'.(not support IntelliJ-IDEA)
5. When running from eclipse, don't forget to set 'working dir' in Run Configuration to '${workspace_loc:<PROJECTNAME>/eclipse}'
6. When build, set 'DEVMODE' back to 'NOT_IN_DEV'.

#Support
Issues can be posted on my GitHub, support English and Japanese.

