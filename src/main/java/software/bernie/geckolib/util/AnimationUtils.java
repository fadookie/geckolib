/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.easing.EasingManager;
import software.bernie.geckolib.easing.EasingType;

import java.util.function.Function;

public class AnimationUtils
{
	public static double convertTicksToSeconds(double ticks)
	{
		return ticks / 20;
	}

	public static double convertSecondsToTicks(double seconds)
	{
		return seconds * 20;
	}

	/**
	 * This is the actual function that smoothly interpolates (lerp) between keyframes
	 *
	 * @param startValue  The animation's start value
	 * @param endValue    The animation's end value
	 * @return The interpolated value
	 */
	public static float lerpValues(double percentCompleted, double startValue, double endValue)
	{
		// current tick / position should be between 0 and 1 and represent the percentage of the lerping that has completed
		return (float) (percentCompleted > 1 ? endValue :  (MathHelper.lerp(percentCompleted, startValue,
				endValue)));
	}

	/**
	 * Lerps an AnimationPoint
	 *
	 * @param animationPoint The animation point
	 * @return the resulting lerped value
	 */
	public static float lerpValues(AnimationPoint animationPoint, EasingType easingType, Function<Double, Double> customEasingMethod)
	{
		if(animationPoint.currentTick >= animationPoint.animationEndTick)
		{
			return animationPoint.animationEndValue;
		}
		if(animationPoint.currentTick == 0 && animationPoint.animationEndTick == 0)
		{
			return animationPoint.animationEndValue;
		}

		if(easingType == EasingType.CUSTOM && customEasingMethod != null)
		{
			return lerpValues(customEasingMethod.apply(animationPoint.currentTick / animationPoint.animationEndTick),
					animationPoint.animationStartValue, animationPoint.animationEndValue);
		}
		else if(easingType == EasingType.NONE && animationPoint.keyframe != null)
		{
			easingType = animationPoint.keyframe.easingType;
		}
		double ease = EasingManager.ease(animationPoint.currentTick / animationPoint.animationEndTick, easingType, animationPoint.keyframe == null ? null : animationPoint.keyframe.easingArgs);
		float result = lerpValues(ease,
				animationPoint.animationStartValue, animationPoint.animationEndValue);
		/**
		 * 			const easedAmount = easingFunc(amount);
		 * 			const start = this.calc(axis);
		 * 			const stop = other.calc(axis);
		 * 			const result = lerp(start, stop, easedAmount);
		 * 			console.log('keyframeGetLerp easing:', easing, 'arguments:', arguments, 'start:', start, 'stop:', stop, 'amount:', amount, 'easedAmount:', easedAmount, 'result:', result);
		 */
		if (!animationPoint.animationStartValue.equals(animationPoint.animationEndValue)) {
			System.out.println("AnimationUtils#lerpValues easing:" + easingType + " start:" + animationPoint.animationStartValue + " stop:" + animationPoint.animationEndValue + " amount:" + animationPoint.currentTick / animationPoint.animationEndTick + " easedAmount:" + ease + " result:" + result);
		}
		return result;
	}

	/**
	 * Gets the renderer for an entity
	 */
	public static <T extends Entity> EntityRenderer<T> getRenderer(T entity)
	{
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		return (EntityRenderer<T>) renderManager.getRenderer(entity);
	}

	/**
	 * Gets the AnimatedEntityModel for an entity.
	 */
	public static <T extends Entity> AnimatedEntityModel getModelForEntity(T entity)
	{
		EntityRenderer<T> entityRenderer = getRenderer(entity);
		if (entityRenderer instanceof IEntityRenderer)
		{
			LivingRenderer renderer = (LivingRenderer) entityRenderer;
			EntityModel entityModel = renderer.getEntityModel();
			if (entityModel instanceof AnimatedEntityModel)
			{
				return (AnimatedEntityModel) entityModel;
			}
			else {
				GeckoLib.LOGGER.error("Model for " + entity.getName() + " is not an AnimatedEntityModel. Please inherit the proper class.");
				return null;
			}
		}
		else {
			GeckoLib.LOGGER.error("Could not find valid renderer for " + entity.getName());
			return null;
		}
	}

}
