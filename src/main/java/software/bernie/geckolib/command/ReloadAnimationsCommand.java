package software.bernie.geckolib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.reload.ReloadManager;

public class ReloadAnimationsCommand implements Command<CommandSource>
{
	private static final ReloadAnimationsCommand CMD = new ReloadAnimationsCommand();

	public static LiteralArgumentBuilder<CommandSource> register(CommandDispatcher<CommandSource> dispatcher)
	{
		return Commands.literal("reload")
				.requires(cs -> cs.hasPermissionLevel(0))
				.executes(CMD);
	}


	@Override
	public int run(CommandContext<CommandSource> context) throws CommandSyntaxException
	{
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		for(IResourceManagerReloadListener model : ReloadManager.getRegisteredModels())
		{
			model.onResourceManagerReload(resourceManager);
		}
		for(AnimationController controller : ReloadManager.getRegisteredAnimationControllers())
		{
			controller.markNeedsReload();
		}

		Style style = new Style();
		style.setColor(TextFormatting.GREEN);
		context.getSource().sendFeedback(new StringTextComponent("Reloaded " + ReloadManager.getRegisteredModels().size() + " animations.").setStyle(
				style), false);
		return 0;
	}


}
