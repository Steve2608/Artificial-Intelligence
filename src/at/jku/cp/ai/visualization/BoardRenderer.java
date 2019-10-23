package at.jku.cp.ai.visualization;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.GameObject;
import at.jku.cp.ai.utils.RenderUtils;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BoardRenderer extends LabelRenderer {

	public BoardRenderer(final String textField, final String imageField) {
		setTextField(textField);
		setImageField(imageField);
	}

	@Override
	protected Image getImage(final VisualItem item) {
		final IBoard board = (IBoard) item.get("board");
		return render(board);
	}

	private Image render(final IBoard board) {
		final int w = 8;

		final BufferedImage image = new BufferedImage(
				board.getWidth() * w,
				board.getHeight() * w,
				BufferedImage.TYPE_INT_RGB
		);

		final Graphics2D gfx = image.createGraphics();
		for (final List<? extends GameObject> objs : board.getAllObjects()) {
			for (final GameObject g : objs) {
				RenderUtils.draw(gfx, g, w);
			}
		}
		return image;
	}
}
