package mcjty.theoneprobe.apiimpl.elements;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.LayoutStyle;
import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.apiimpl.ProgressStyle;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElementPanel implements IElement, IProbeInfo {

    protected List<IElement> children = new ArrayList<>();
    protected Integer borderColor;
    protected int spacing;

    @Override
    public void render(int x, int y) {
        if (borderColor != null) {
            int w = getWidth();
            int h = getHeight();
            RenderHelper.drawHorizontalLine(x, y, x + w - 1, borderColor);
            RenderHelper.drawHorizontalLine(x, y + h - 1, x + w - 1, borderColor);
            RenderHelper.drawVerticalLine(x, y, y + h - 1, borderColor);
            RenderHelper.drawVerticalLine(x + w - 1, y, y + h - 1, borderColor);
            x += 3;
            y += 3;
        }
    }

    public AbstractElementPanel(Integer borderColor, int spacing) {
        this.borderColor = borderColor;
        this.spacing = spacing;
    }

    public AbstractElementPanel(ByteBuf buf) {
        children = ProbeInfo.createElements(buf);
        if (buf.readBoolean()) {
            borderColor = buf.readInt();
        }
        spacing = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ProbeInfo.writeElements(children, buf);
        if (borderColor != null) {
            buf.writeBoolean(true);
            buf.writeInt(borderColor);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeShort(spacing);
    }

    @Override
    public IProbeInfo text(String text, ITextStyle style) {
        return text(text);
    }

    @Override
    public IProbeInfo text(String text) {
        children.add(new ElementText(text));
        return this;
    }

    @Override
    public IProbeInfo item(ItemStack stack, IItemStyle style) {
        return item(stack);
    }

    @Override
    public IProbeInfo item(ItemStack stack) {
        children.add(new ElementItemStack(stack));
        return this;
    }

    @Override
    public IProbeInfo progress(int current, int max) {
        return progress(current, max, new ProgressStyle());
    }

    @Override
    public IProbeInfo progress(int current, int max, IProgressStyle style) {
        children.add(new ElementProgress(current, max, style));
        return this;
    }

    @Override
    public IProbeInfo horizontal(ILayoutStyle style) {
        ElementHorizontal e = new ElementHorizontal(style.getBorderColor(), style.getSpacing());
        children.add(e);
        return e;
    }

    @Override
    public IProbeInfo horizontal() {
        ElementHorizontal e = new ElementHorizontal((Integer) null, spacing);
        children.add(e);
        return e;
    }

    @Override
    public IProbeInfo vertical(ILayoutStyle style) {
        ElementVertical e = new ElementVertical(style.getBorderColor(), style.getSpacing());
        children.add(e);
        return e;
    }

    @Override
    public IProbeInfo vertical() {
        ElementVertical e = new ElementVertical((Integer) null, ElementVertical.SPACING);
        children.add(e);
        return e;
    }

    @Override
    public IProbeInfo element(IElement element) {
        children.add(element);
        return this;
    }

    @Override
    public ILayoutStyle defaultLayoutStyle() {
        return new LayoutStyle();
    }

    @Override
    public IProgressStyle defaultProgressStyle() {
        return new ProgressStyle();
    }

    @Override
    public ITextStyle defaultTextStyle() {
        return new ITextStyle() { };
    }

    @Override
    public IItemStyle defaultItemStyle() {
        return new IItemStyle() { };
    }
}
