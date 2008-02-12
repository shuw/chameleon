package ca.shu.ui.chameleon.util;

import java.awt.geom.Point2D;

import ca.shu.ui.lib.world.Destroyable;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldObject.Property;
import ca.shu.ui.lib.world.elastic.ElasticEdge;

public abstract class DistanceListener implements Destroyable, ca.shu.ui.lib.world.WorldObject.Listener {
	private ElasticEdge edge;
	private WorldObject startNode;
	private WorldObject endNode;

	public DistanceListener(ElasticEdge edge) {
		super();
		this.edge = edge;

		endNode = edge.getEndNode();
		startNode = edge.getStartNode();

		startNode.addPropertyChangeListener(Property.GLOBAL_BOUNDS, this);
		endNode.addPropertyChangeListener(Property.GLOBAL_BOUNDS, this);
	}

	public void destroy() {
		startNode.removePropertyChangeListener(Property.GLOBAL_BOUNDS, this);
		endNode.removePropertyChangeListener(Property.GLOBAL_BOUNDS, this);
	}

	public void propertyChanged(Property event) {
		Point2D startPoint = new Point2D.Double(startNode.getBounds().getCenterX(), startNode
				.getBounds().getCenterY());
		Point2D endPoint = new Point2D.Double(endNode.getBounds().getCenterX(), endNode.getBounds()
				.getCenterY());

		startNode.localToGlobal(startPoint);
		endNode.localToGlobal(endPoint);

		double distance = startPoint.distance(endPoint);

		distanceChanged(distance);

	}

	public abstract void distanceChanged(double distance);

	protected ElasticEdge getEdge() {
		return edge;
	}

	protected WorldObject getStartNode() {
		return startNode;
	}

	protected void setStartNode(WorldObject startNode) {
		this.startNode = startNode;
	}

	protected WorldObject getEndNode() {
		return endNode;
	}

	protected void setEndNode(WorldObject endNode) {
		this.endNode = endNode;
	}
}