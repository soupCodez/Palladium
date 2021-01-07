package net.threetag.threecore.scripts;

import com.google.common.collect.Maps;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.threetag.threecore.ThreeCore;
import net.threetag.threecore.scripts.bindings.BlockStateBuilder;
import net.threetag.threecore.scripts.bindings.ItemStackBuilder;
import net.threetag.threecore.scripts.bindings.MathHelper;
import net.threetag.threecore.scripts.bindings.ThreeDataBuilder;
import net.threetag.threecore.util.documentation.DocumentationBuilder;
import net.threetag.threecore.scripts.bindings.*;
import org.apache.commons.io.IOUtils;

import javax.script.*;
import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.threetag.threecore.util.documentation.DocumentationBuilder.*;

@Mod.EventBusSubscriber(modid = ThreeCore.MODID)
public class ScriptManager extends ReloadListener<Map<ResourceLocation, String>> {

    public static ScriptManager INSTANCE;
    public static ScriptEngineManager manager = new ScriptEngineManager();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final String folder;
    private static final Map<String, Supplier<?>> bindings = Maps.newHashMap();

    private static final String[] BLOCKED_FUNCTIONS = {
            "load",
            "loadWithNewGlobal",
            "exit",
            "quit"
    };

    public static void registerBinding(String name, Supplier<?> supplier) {
        bindings.put(name, supplier);
    }

    static {
        registerBinding("eventManager", ScriptEventManager.EventManagerAccessor::new);
        registerBinding("threeDataBuilder", ThreeDataBuilder::new);
        registerBinding("blockStateBuilder", BlockStateBuilder::new);
        registerBinding("itemStackBuilder", ItemStackBuilder::new);
        registerBinding("mathHelper", MathHelper::new);
        registerBinding("compoundNBTBuilder", CompoundNBTBuilder::new);
    }

    public ScriptManager() {
        INSTANCE = this;
        this.folder = "scripts";
    }

    @Override
    protected Map<ResourceLocation, String> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
        Map<ResourceLocation, String> map = Maps.newHashMap();
        int i = this.folder.length() + 1;

        for (ResourceLocation resourcelocation : resourceManagerIn.getAllResourceLocations(this.folder, (s) -> {
            return s.endsWith(".js");
        })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(i, s.length() - JSON_EXTENSION_LENGTH));

            try (
                    IResource iresource = resourceManagerIn.getResource(resourcelocation);
                    InputStream inputstream = iresource.getInputStream();
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
            ) {
                String script = IOUtils.toString(reader);
                if (script != null) {
                    String script1 = map.put(resourcelocation1, script);
                    if (script1 != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                    }
                } else {
                    ThreeCore.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourcelocation1, resourcelocation);
                }
            } catch (IllegalArgumentException | IOException exception) {
                ThreeCore.LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, exception);
            }
        }

        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, String> splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        ScriptEventManager.reset();

        for (Map.Entry<ResourceLocation, String> entry : splashList.entrySet()) {
            try {
                ScriptEngine engine = manager.getEngineByName("javascript");
                ScriptContext context = engine.getContext();
                if (!(engine instanceof Invocable)) {
                    ThreeCore.LOGGER.error("Engine is not invocable?");
                    continue;
                }

                for (String s : BLOCKED_FUNCTIONS)
                    context.removeAttribute(s, context.getAttributesScope(s));

                engine.put("namespace", entry.getKey().toString());
                bindings.forEach((s, b) -> engine.put(s, b.get()));
                engine.eval(entry.getValue());
                ThreeCore.LOGGER.info("Executed script file {}!", entry.getKey());
            } catch (ScriptException e) {
                ThreeCore.LOGGER.error("Error when executing script file {}", entry.getKey(), e);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void generateDocumentation() {
        List<String> ignoredMethods = Arrays.asList("fire", "wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll");

        DocumentationBuilder builder = new DocumentationBuilder(new ResourceLocation(ThreeCore.MODID, "scripts/utils"), "Script Utils")
                .add(heading("Script Utils")).add(hr())
                .add(paragraph(subHeading("Overview")).add(list(bindings.keySet().stream().map(supplier -> link(supplier, "#" + supplier)).collect(Collectors.toList()))));

        for (Map.Entry<String, Supplier<?>> entry : bindings.entrySet()) {
            builder.add(hr()).add(div().setId(entry.getKey()).add(subHeading(entry.getKey()))
                    .add(table(Arrays.asList("Function", "Return Type", "Parameters"), Arrays.stream(entry.getValue().get().getClass().getMethods()).filter(method -> !ignoredMethods.contains(method.getName()) && !Modifier.isStatic(method.getModifiers())).map(method -> {
                        Collection<String> columns = new LinkedList<>();
                        columns.add(method.getName());
                        columns.add(method.getReturnType().getSimpleName());

                        if (method.getParameterCount() <= 0)
                            columns.add("/");
                        else {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < method.getParameterCount(); i++) {
                                String parameterName = method.getParameters()[i].getName();
                                ScriptParameterName scriptParameterName = method.getParameters()[i].getAnnotation(ScriptParameterName.class);
                                if (scriptParameterName != null)
                                    parameterName = scriptParameterName.value();

                                stringBuilder.append("<strong>").append(parameterName).append("</strong> - ").append(method.getParameterTypes()[i].getSimpleName());
                                if (method.getParameterCount() > 1)
                                    stringBuilder.append("<br>");
                            }
                            columns.add(stringBuilder.toString());
                        }

                        return columns;
                    }).collect(Collectors.toList()))));
        }

        builder.save();
    }

}
