/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.ui.widget;

import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.support.v7.widget.AppCompatButton;
import android.content.res.ColorStateList;

public class TiUIButton extends TiUIView
{
	private static final String TAG = "TiUIButton";
	private static final float DEFAULT_SHADOW_RADIUS = 0.5f;
	
	private int defaultColor;
	private float shadowRadius = DEFAULT_SHADOW_RADIUS;
	private float shadowX = 0f;
	private float shadowY = 0f;
	private int shadowColor = Color.TRANSPARENT;

	public TiUIButton(final TiViewProxy proxy)
	{
		super(proxy);
		Log.d(TAG, "Creating a button", Log.DEBUG_MODE);
		AppCompatButton btn = new AppCompatButton(proxy.getActivity())
		{
			@Override
			protected void onLayout(boolean changed, int left, int top, int right, int bottom)
			{
				super.onLayout(changed, left, top, right, bottom);
				TiUIHelper.firePostLayoutEvent(proxy);
			}
		};
		btn.setGravity(Gravity.CENTER);
		defaultColor = btn.getCurrentTextColor();
		setNativeView(btn);
	}

	@Override
	public void processProperties(KrollDict d)
	{
		super.processProperties(d);

		boolean needShadow = false;

		AppCompatButton btn = (AppCompatButton) getNativeView();
		if (d.containsKey(TiC.PROPERTY_IMAGE)) {
			Object value = d.get(TiC.PROPERTY_IMAGE);
			TiDrawableReference drawableRef = null;
			if (value instanceof String) {
				drawableRef = TiDrawableReference.fromUrl(proxy, (String) value);
			} else if (value instanceof TiBlob) {
				drawableRef = TiDrawableReference.fromBlob(proxy.getActivity(), (TiBlob) value);
			}

			if (drawableRef != null) {
				Drawable image = drawableRef.getDensityScaledDrawable();
				btn.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
			}
		} else if (d.containsKey(TiC.PROPERTY_BACKGROUND_COLOR) || d.containsKey(TiC.PROPERTY_BACKGROUND_IMAGE)) {
			// Reset the padding here if the background color/image is set. By default the padding will be calculated
			// for the button, but if we set a background color, it will not look centered unless we reset the padding.
			btn.setPadding(8, 0, 8, 0);
		}
		if (d.containsKey(TiC.PROPERTY_TITLE)) {
			btn.setText(d.getString(TiC.PROPERTY_TITLE));
		}
		if (d.containsKey(TiC.PROPERTY_COLOR) || d.containsKey(TiC.PROPERTY_SELECTED_COLOR)) {
			btn.setTextColor(new ColorStateList(
					new int[][] { 
						new int[] { 
							android.R.attr.state_pressed 
						},
						new int[] {},
					},
					new int[] {
						d.get(TiC.PROPERTY_SELECTED_COLOR) == null ? defaultColor
								: TiConvert.toColor(d,
										TiC.PROPERTY_SELECTED_COLOR),
						d.get(TiC.PROPERTY_COLOR) == null ? defaultColor
								: TiConvert.toColor(d, TiC.PROPERTY_COLOR) 
					}));
		}
		if (d.containsKey(TiC.PROPERTY_FONT)) {
			TiUIHelper.styleText(btn, d.getKrollDict(TiC.PROPERTY_FONT));
		}
		if (d.containsKey(TiC.PROPERTY_TEXT_ALIGN)) {
			String textAlign = d.getString(TiC.PROPERTY_TEXT_ALIGN);
			TiUIHelper.setAlignment(btn, textAlign, null);
		}
		if (d.containsKey(TiC.PROPERTY_VERTICAL_ALIGN)) {
			String verticalAlign = d.getString(TiC.PROPERTY_VERTICAL_ALIGN);
			TiUIHelper.setAlignment(btn, null, verticalAlign);
		}
		if (d.containsKey(TiC.PROPERTY_SHADOW_OFFSET)) {
			Object value = d.get(TiC.PROPERTY_SHADOW_OFFSET);
			if (value instanceof HashMap) {
				needShadow = true;
				HashMap dict = (HashMap) value;
				shadowX = TiConvert.toFloat(dict.get(TiC.PROPERTY_X), 0);
				shadowY = TiConvert.toFloat(dict.get(TiC.PROPERTY_Y), 0);
			}
		}
		if (d.containsKey(TiC.PROPERTY_SHADOW_RADIUS)) {
			needShadow = true;
			shadowRadius = TiConvert.toFloat(d.get(TiC.PROPERTY_SHADOW_RADIUS), DEFAULT_SHADOW_RADIUS);
		}
		if (d.containsKey(TiC.PROPERTY_SHADOW_COLOR)) {
			needShadow = true;
			shadowColor = TiConvert.toColor(d, TiC.PROPERTY_SHADOW_COLOR);
		}
		if (needShadow) {
			btn.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
		}
		btn.invalidate();
	}

	@Override
	public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy)
	{
		if (Log.isDebugModeEnabled()) {
			Log.d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
		}
		AppCompatButton btn = (AppCompatButton) getNativeView();
		if (key.equals(TiC.PROPERTY_TITLE)) {
			btn.setText((String) newValue);
		} else if (key.equals(TiC.PROPERTY_COLOR)) {
			btn.setTextColor(new ColorStateList(
					new int[][] { 
						new int[] { 
							android.R.attr.state_pressed 
						},
						new int[] {},
					},
					new int[] {
						btn.getTextColors().getColorForState(new int[] { 
								android.R.attr.state_pressed 
							}, defaultColor),
						TiConvert.toColor(TiConvert.toString(newValue))
			}));
		} else if (key.equals(TiC.PROPERTY_SELECTED_COLOR)) {
			btn.setTextColor(new ColorStateList(
					new int[][] { 
						new int[] { 
							android.R.attr.state_pressed 
						},
						new int[] {},
					},
					new int[] {
						TiConvert.toColor(TiConvert.toString(newValue)),
						btn.getTextColors().getColorForState(new int[] {}, defaultColor)
			}));
		} else if (key.equals(TiC.PROPERTY_FONT)) {
			TiUIHelper.styleText(btn, (HashMap) newValue);
		} else if (key.equals(TiC.PROPERTY_TEXT_ALIGN)) {
			TiUIHelper.setAlignment(btn, TiConvert.toString(newValue), null);
			btn.requestLayout();
		} else if (key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
			TiUIHelper.setAlignment(btn, null, TiConvert.toString(newValue));
			btn.requestLayout();
		} else if (key.equals(TiC.PROPERTY_IMAGE)) {
			TiDrawableReference drawableRef = null;
			if (newValue instanceof String) {
				drawableRef = TiDrawableReference.fromUrl(proxy, (String) newValue);
			} else if (newValue instanceof TiBlob) {
				drawableRef = TiDrawableReference.fromBlob(proxy.getActivity(), (TiBlob) newValue);
			}
			if (drawableRef != null) {
				Drawable image = drawableRef.getDrawable();
				btn.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
			}
		} else if (key.equals(TiC.PROPERTY_SHADOW_OFFSET)) {
			if (newValue instanceof HashMap) {
				HashMap dict = (HashMap) newValue;
				shadowX = TiConvert.toFloat(dict.get(TiC.PROPERTY_X), 0);
				shadowY = TiConvert.toFloat(dict.get(TiC.PROPERTY_Y), 0);
				btn.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
			}
		} else if (key.equals(TiC.PROPERTY_SHADOW_RADIUS)) {
			shadowRadius = TiConvert.toFloat(newValue, DEFAULT_SHADOW_RADIUS);
			btn.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
		} else if (key.equals(TiC.PROPERTY_SHADOW_COLOR)) {
			shadowColor = TiConvert.toColor(TiConvert.toString(newValue));
			btn.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
		} else {
			super.propertyChanged(key, oldValue, newValue, proxy);
		}
	}

}
