package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.render.IGUIGraphics;

import java.util.ArrayList;
import java.util.List;

public class IconCheckBox extends Widget implements IInputWidget<Boolean> {
	

	private final ResourceLocation sprite;
	private final List<ILayoutVariable<Boolean>> bindings = new ArrayList<>();

	private boolean checked;
	private int backgroundColor;
    
    public IconCheckBox(ResourceLocation sprite) {
		this.sprite = sprite;
	}
	
	@Override
	public Boolean getValue() {
		return this.checked;
	}

	@Override
	public void setValue(Boolean value) {
		this.checked = value != null && value;
	}

	@Override
	public IconCheckBox bind(ILayoutVariable<Boolean> variable) {
		this.setValue(variable.get());
		this.bindings.add(variable);
		return this;
	}

	@Override
	protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
		this.checked = !this.checked;
		for (var bind : this.bindings) {
			bind.set(this.checked);
		}
		return true;
	}

	@Override
	public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
		super.doRender(graphics, mouseX, mouseY, a);
		float minX = this.x;
		float minY = this.y;
		float maxX = this.getMaxX();
		float maxY = this.getMaxY();
		if (this.hovered) {
			graphics.fillRounded(minX, minY, maxX, maxY, 0x88333333, width/4);
		}
		if (this.checked) {
			graphics.fillRounded(minX, minY, maxX, maxY, this.backgroundColor, width/4);
		}
		graphics.blitSprite(this.sprite, minX, minY, this.width, this.height, -1);
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
