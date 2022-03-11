package net.threetag.palladium.addonpack.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.builder.AddonBuilder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AddonParser<T> extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public final ResourceKey<Registry<T>> resourceKey;


    public AddonParser(Gson gson, String string, ResourceKey<Registry<T>> resourceKey) {
        super(gson, string);
        this.resourceKey = resourceKey;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        AtomicInteger i = new AtomicInteger();
        object.forEach((id, jsonElement) -> {
            try {
                register(this.resourceKey, parse(id, jsonElement));
                i.getAndIncrement();
            } catch (Exception e) {
                CrashReport crashReport = CrashReport.forThrowable(e, "Error while parsing addonpack " + this.resourceKey.location().getPath() + " '" + id + "'");

                CrashReportCategory reportCategory = crashReport.addCategory("Addon " + this.resourceKey.location().getPath(), 1);
                reportCategory.setDetail("Resource name", id);

                throw new ReportedException(crashReport);
            }
        });
        Palladium.LOGGER.info("Registered " + i.get() + " addonpack " + this.resourceKey.location().getPath());
    }

    @ExpectPlatform
    public static <T> void register(ResourceKey<Registry<T>> resourceKey, AddonBuilder<T> builder) {
        throw new AssertionError();
    }

    public abstract AddonBuilder<T> parse(ResourceLocation id, JsonElement jsonElement);
}
