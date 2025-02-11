package at.jku.cp.ai.visualization;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;


/**
 * Demo application showcasing the use of AggregateItems to
 * visualize groupings of nodes with in a graph visualization.
 * <p>
 * This class uses the AggregateLayout class to compute bounding
 * polygons for each aggregate and the AggregateDragControl to
 * enable drags of both nodes and node aggregates.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class AggregateDecoratorDemo extends Display {

	public static final String GRAPH = "graph";
	public static final String NODES = "graph.nodes";
	public static final String EDGES = "graph.edges";
	public static final String AGGR = "aggregates";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AggregateDecoratorDemo() {
		// initialize display and data
		super(new Visualization());
		initDataGroups();

		// set up the renderers
		// draw the nodes as basic shapes
		final Renderer nodeR = new ShapeRenderer(20);
		// draw aggregates as polygons with curved edges
		final Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
		((PolygonRenderer) polyR).setCurveSlack(0.15f);

		final DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(nodeR);
		drf.add("ingroup('aggregates')", polyR);
		m_vis.setRendererFactory(drf);

		// set up the visual operators
		// first set up all the color actions
		final ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
		nStroke.setDefaultColor(ColorLib.gray(100));
		nStroke.add("_hover", ColorLib.gray(50));

		final ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
		nFill.setDefaultColor(ColorLib.gray(255));
		nFill.add("_hover", ColorLib.gray(200));

		final ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
		nEdges.setDefaultColor(ColorLib.gray(100));

		final ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
		aStroke.setDefaultColor(ColorLib.gray(200));
		aStroke.add("_hover", ColorLib.rgb(255, 100, 100));

		final int[] palette = {
				ColorLib.rgba(255, 200, 200, 150),
				ColorLib.rgba(200, 255, 200, 150),
				ColorLib.rgba(200, 200, 255, 150)
		};
		final ColorAction aFill = new DataColorAction(AGGR, "id",
				Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

		// bundle the color actions
		final ActionList colors = new ActionList();
		colors.add(nStroke);
		colors.add(nFill);
		colors.add(nEdges);
		colors.add(aStroke);
		colors.add(aFill);

		// now create the main layout routine
		final ActionList layout = new ActionList(Activity.INFINITY);
		layout.add(colors);
		layout.add(new ForceDirectedLayout(GRAPH, true));
		layout.add(new AggregateLayout(AGGR));
		layout.add(new RepaintAction());
		m_vis.putAction("layout", layout);

		// set up the display
		setSize(500, 500);
		pan(250, 250);
		setHighQuality(true);
		addControlListener(new AggregateDragControl());
		addControlListener(new ZoomControl());
		addControlListener(new PanControl());

		// set things running
		m_vis.run("layout");
	}

	public static void main(final String[] argv) {
		final JFrame frame = demo();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static JFrame demo() {
		final AggregateDecoratorDemo ad = new AggregateDecoratorDemo();
		final JFrame frame = new JFrame("p r e f u s e  |  a g g r e g a t e d");
		frame.getContentPane().add(ad);
		frame.pack();
		return frame;
	}

	private void initDataGroups() {
		// create sample graph
		// 9 nodes broken up into 3 interconnected cliques
		final Graph g = new Graph();
		for (int i = 0; i < 3; ++i) {
			final Node n1 = g.addNode();
			final Node n2 = g.addNode();
			final Node n3 = g.addNode();
			g.addEdge(n1, n2);
			g.addEdge(n1, n3);
			g.addEdge(n2, n3);
		}
		g.addEdge(0, 3);
		g.addEdge(3, 6);
		g.addEdge(6, 0);

		// add visual data groups
		final VisualGraph vg = m_vis.addGraph(GRAPH, g);
		m_vis.setInteractive(EDGES, null, false);
		m_vis.setValue(NODES, null, VisualItem.SHAPE, new Integer(Constants.SHAPE_ELLIPSE));

		final AggregateTable at = m_vis.addAggregates(AGGR);
		at.addColumn(VisualItem.POLYGON, float[].class);
		at.addColumn("id", int.class);

		// add nodes to aggregates
		// create an aggregate for each 3-clique of nodes
		final Iterator<?> nodes = vg.nodes();
		for (int i = 0; i < 3; ++i) {
			final AggregateItem aitem = (AggregateItem) at.addItem();
			aitem.setInt("id", i);
			for (int j = 0; j < 3; ++j) {
				aitem.addItem((VisualItem) nodes.next());
			}
		}
	}

} // end of class AggregateDemo


/**
 * Layout algorithm that computes a convex hull surrounding
 * aggregate items and saves it in the "_polygon" field.
 */
class AggregateLayout extends Layout {

	private int m_margin = 5; // convex hull pixel margin
	private double[] m_pts;   // buffer for computing convex hulls

	public AggregateLayout(final String aggrGroup) {
		super(aggrGroup);
	}

	private static void addPoint(final double[] pts, final int idx,
								 final VisualItem item, final int growth) {
		final Rectangle2D b = item.getBounds();
		final double minX = b.getMinX() - growth;
		final double minY = b.getMinY() - growth;
		final double maxX = b.getMaxX() + growth;
		final double maxY = b.getMaxY() + growth;
		pts[idx] = minX;
		pts[idx + 1] = minY;
		pts[idx + 2] = minX;
		pts[idx + 3] = maxY;
		pts[idx + 4] = maxX;
		pts[idx + 5] = minY;
		pts[idx + 6] = maxX;
		pts[idx + 7] = maxY;
	}

	/**
	 * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
	 */
	public void run(final double frac) {

		final AggregateTable aggr = (AggregateTable) m_vis.getGroup(m_group);
		// do we have any  to process?
		final int num = aggr.getTupleCount();
		if (num == 0) return;

		// update buffers
		int maxsz = 0;
		for (final Iterator<?> aggrs = aggr.tuples(); aggrs.hasNext(); )
			maxsz = Math.max(maxsz, 4 * 2 *
					((AggregateItem) aggrs.next()).getAggregateSize());
		if (m_pts == null || maxsz > m_pts.length) {
			m_pts = new double[maxsz];
		}

		// compute and assign convex hull for each aggregate
		final Iterator<?> aggrs = m_vis.visibleItems(m_group);
		while (aggrs.hasNext()) {
			final AggregateItem aitem = (AggregateItem) aggrs.next();

			int idx = 0;
			if (aitem.getAggregateSize() == 0) continue;
			VisualItem item = null;
			final Iterator<?> iter = aitem.items();
			while (iter.hasNext()) {
				item = (VisualItem) iter.next();
				if (item.isVisible()) {
					addPoint(m_pts, idx, item, m_margin);
					idx += 2 * 4;
				}
			}
			// if no aggregates are visible, do nothing
			if (idx == 0) continue;

			// compute convex hull
			final double[] nhull = GraphicsLib.convexHull(m_pts, idx);

			// prepare viz attribute array
			float[] fhull = (float[]) aitem.get(VisualItem.POLYGON);
			if (fhull == null || fhull.length < nhull.length)
				fhull = new float[nhull.length];
			else if (fhull.length > nhull.length)
				fhull[nhull.length] = Float.NaN;

			// copy hull values
			for (int j = 0; j < nhull.length; j++)
				fhull[j] = (float) nhull[j];
			aitem.set(VisualItem.POLYGON, fhull);
			aitem.setValidated(false); // force invalidation
		}
	}

} // end of class AggregateLayout


/**
 * Interactive drag control that is "aggregate-aware"
 */
class AggregateDragControl extends ControlAdapter {

	protected Point2D down = new Point2D.Double();
	protected Point2D temp = new Point2D.Double();
	protected boolean dragged;
	private VisualItem activeItem;

	/**
	 * Creates a new drag control that issues repaint requests as an item
	 * is dragged.
	 */
	public AggregateDragControl() {
	}

	protected static void setFixed(final VisualItem item, final boolean fixed) {
		if (item instanceof AggregateItem) {
			final Iterator<?> items = ((AggregateItem) item).items();
			while (items.hasNext()) {
				setFixed((VisualItem) items.next(), fixed);
			}
		} else {
			item.setFixed(fixed);
		}
	}

	protected static void move(final VisualItem item, final double dx, final double dy) {
		if (item instanceof AggregateItem) {
			final Iterator<?> items = ((AggregateItem) item).items();
			while (items.hasNext()) {
				move((VisualItem) items.next(), dx, dy);
			}
		} else {
			final double x = item.getX();
			final double y = item.getY();
			item.setStartX(x);
			item.setStartY(y);
			item.setX(x + dx);
			item.setY(y + dy);
			item.setEndX(x + dx);
			item.setEndY(y + dy);
		}
	}

	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemEntered(final VisualItem item, final MouseEvent e) {
		final Display d = (Display) e.getSource();
		d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		activeItem = item;
		if (!(item instanceof AggregateItem))
			setFixed(item, true);
	}

	/**
	 * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemExited(final VisualItem item, final MouseEvent e) {
		if (activeItem == item) {
			activeItem = null;
			setFixed(item, false);
		}
		final Display d = (Display) e.getSource();
		d.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemPressed(final VisualItem item, final MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;
		dragged = false;
		final Display d = (Display) e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), down);
		if (item instanceof AggregateItem)
			setFixed(item, true);
	}

	/**
	 * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemReleased(final VisualItem item, final MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;
		if (dragged) {
			activeItem = null;
			setFixed(item, false);
			dragged = false;
		}
	}

	/**
	 * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemDragged(final VisualItem item, final MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;
		dragged = true;
		final Display d = (Display) e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), temp);
		final double dx = temp.getX() - down.getX();
		final double dy = temp.getY() - down.getY();

		move(item, dx, dy);

		down.setLocation(temp);
	}

} // end of class AggregateDragControl