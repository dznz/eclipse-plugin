/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.eclipse.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Rene Groeschke
 * Generic color manager.
 */
public class ColorManager implements ISharedTextColors {	
	
	private static ColorManager colorManager;
	
	protected Map<RGB, Color> colorTable = new HashMap<RGB, Color>(10);

	private ColorManager() {
	}
	
	public static ColorManager getDefault() {
		if (colorManager == null) {
			colorManager= new ColorManager();
		}
		return colorManager;
	}
	
	public Color getColor(RGB rgb) {
		Color color = colorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}
		return color;
	}
	
	public void dispose() {
		Iterator<Color> e= colorTable.values().iterator();
		while (e.hasNext()) {
			 e.next().dispose();
		}
	}
}


