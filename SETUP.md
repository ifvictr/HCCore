# Setup

For the purposes of this guide we'll designate the `~/minecraft-server` and `~/HCCore` directories for the locations of the minecraft (paper) server, and the HCCore source code. Of course, you don't have to put the files in the same place

## 1. Start Minecraft Paper Server

[Download Paper](https://papermc.io/downloads) (build #261 at the time of writing) and place the resulting jar to `~/minecraft-server`

Start the server and check that it works properly

```sh
java -Xms2G -Xmx2G -jar paper-261.jar --nogui
```

Something like the following should be outputted to the console

```txt
This server is running Paper version git-Paper-261 (MC: 1.16.4) (Implementing API version 1.16.4-R0.1-SNAPSHOT)
```

You'll notice other files and folders like `folder`, `whitelist.json`, etc. are created

To join the server, launch your Minecraft client, and click `[Direct Connection]` -> and enter `localhost` to 'Server Address'. Notice that the `/color` command doesn't work (it will work once we add the HCCore plugin)

## 2. Download and Build HCCore

Ensure [maven](https://maven.apache.org/download.cgi) is installed

```sh
git clone https://github.com/hackclub/HCCore
cd HCCore
mvn package
```

## 3. Copy the Built HCCore Plugin to the Minecraft Paper Server

```sh
cp ~/HCCore/target/HCCore.jar ~/minecraft-server/plugins
```

On launch, you should receive an error:

```txt
[00:33:58 ERROR]: Could not load 'plugins/HCCore.jar' in folder 'plugins'
org.bukkit.plugin.UnknownDependencyException: Unknown dependency ProtocolLib. Please download and install ProtocolLib to run this plugin.
	at org.bukkit.plugin.SimplePluginManager.loadPlugins(SimplePluginManager.java:272) ~[patched_1.16.4.jar:git-Paper-261]
	at org.bukkit.craftbukkit.v1_16_R3.CraftServer.loadPlugins(CraftServer.java:389) ~[patched_1.16.4.jar:git-Paper-261]
	at net.minecraft.server.v1_16_R3.DedicatedServer.init(DedicatedServer.java:206) ~[patched_1.16.4.jar:git-Paper-261]
	at net.minecraft.server.v1_16_R3.MinecraftServer.w(MinecraftServer.java:939) ~[patched_1.16.4.jar:git-Paper-261]
	at net.minecraft.server.v1_16_R3.MinecraftServer.lambda$a$0(MinecraftServer.java:177) ~[patched_1.16.4.jar:git-Paper-261]
	at java.lang.Thread.run(Thread.java:834) [?:?]
```

We fix this by [installing ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

```sh
mv ~/Downloads/ProtocolLib.jar ~/minecraft-server/plugins
```

Note: Make sure you stop the server and restart it for the plugins to properly load

BOOM! Now we can connect with our Minecraft client and test/use the plugin

```text
-> wilde_fox joined the game
/color
-> /color <chat|name> [color]
```

## 4. Install BileTools

We can use BileTools to hot-reload our plugin. BileTools looks for changes in the directory, and when it detects that a `.jar` file has changed, it will reload that plugin

You can test this out by modifying the strings in `player.getServer().broadcastMessage()` in `src/main/java/com/hackclub/hccore/listeners/AFKListener.java`

Once you have made your modifications, rebuild

```
cd ~/HCCore
mvn package
cp ~/HCCore/target/HCCore.jar ~/minecraft-server/plugins
```

Without closing the server, your console should look like the following

```text
[00:47:50 INFO]: [BileTools] File change detected: HCCore.jar
[00:47:50 INFO]: [BileTools] Identified Plugin: HCCore <-> HCCore.jar
[00:47:50 INFO]: [BileTools] Reloading: HCCore
[00:47:50 INFO]: Backed up HCCore 1.0.0
[00:47:50 INFO]: [Bile]: Unloading HCCore
[00:47:50 INFO]: [HCCore] Disabling HCCore v1.0.0
[00:47:50 INFO]: [Bile]: Loading HCCore 1.0.0
[00:47:50 INFO]: [HCCore] Enabling HCCore v1.0.0
[00:47:50 INFO]: Reloading ResourceManager: Default, bukkit
[00:47:50 INFO]: Loaded 7 recipes
```
