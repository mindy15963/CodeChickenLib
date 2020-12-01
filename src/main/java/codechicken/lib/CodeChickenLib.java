package codechicken.lib;

import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
import codechicken.lib.internal.command.CCLCommands;
import codechicken.lib.internal.network.CCLNetwork;
import codechicken.lib.internal.proxy.Proxy;
import codechicken.lib.internal.proxy.ProxyClient;
import codechicken.lib.render.OpenGLUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Paths;

/**
 * Created by covers1624 on 12/10/2016.
 */
@Mod (CodeChickenLib.MOD_ID)
public class CodeChickenLib {

    public static final String MOD_ID = "codechickenlib";

    public static ConfigTag config;

    public static Proxy proxy;

    public CodeChickenLib() {
        proxy = DistExecutor.runForDist(() -> ProxyClient::new, () -> Proxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(OpenGLUtils::onModelRegistryEvent);
        });
        CCLCommands.registerArguments();
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
        config = new StandardConfigFile(Paths.get("config/ccl.cfg")).load();
        CCLNetwork.init();
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    @SubscribeEvent
    public void onServerSetup(FMLDedicatedServerSetupEvent event) {
        proxy.serverSetup(event);

    }

    private void onServerStarting(FMLServerStartingEvent event) {
        CCLCommands.registerCommands(event.getCommandDispatcher());
    }

}
