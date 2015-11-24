# LittleMaidMobNX
This project is unofficial port of LittleMaidMob to Minecraft 1.8.(branch for 1.8.8)

Built on Forge 11.14.4.1579
Based on LittleMaidMobX-1.7.x-0.0.9-pre3, made by EMB4.
This is open-source project, use it on NON-COMMERCIAL.

**REQUIRES EBLib: CLONE 'EBLib-1.8' OR ADD EBLib-dev TO BUILD-PATH**

# How to create Dev Environments

1. Setup Forge-src. Set runDir prop to 'eclipse'.
2. Download source of LMMNX from 'Release page' or clone this repo and put 'src' directory into the project dir.
3. Also download source of 'EBLib-1.8' or clone, make a project of EBLib, then add dependency to the project of LMMNX.
4. Open 'src/main/java/net/blacklab/lmmnx/util/LMMNX_DevMode.java', and rewrite the value of 'DEVMODE'. If you are not using any IDE, change to 'DEVMODE_NO_IDE', or using eclipse, to 'DEVMODE_ECLIPSE'.(not support IntelliJ-IDEA)
5. When running from eclipse, don't forget to set 'working dir' in Run Configuration to '${workspace_loc:<PROJECTNAME>/eclipse}'
6. If you make Multimodel or EntityMode on another project, add its name to the 'INCLUDEPROJECT' array in LMMNX_DevMode.java. (NOTICE: This feature is only for eclipse)
7. When building, set 'DEVMODE' back to 'NOT_IN_DEV'.

#Support
Bug reports/Pull requests can be posted to this repo.

