package ca.shu.ui.chameleon.util;

import java.awt.geom.Point2D;
import java.util.Random;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.activities.Fader;
import edu.umd.cs.piccolo.activities.PActivity;

public class ChameleonUtil {
	private static Random randomGen = new Random();

	public static void FadeAndDestroy(WorldObject target, long startTime, long timeMs) {
		PActivity destroyPerson = new DestroyActivity(target);
		Fader fadePerson = new Fader(target, timeMs, 0f);
		fadePerson.setStartTime(startTime);

		destroyPerson.setStartTime(startTime + timeMs);

		UIEnvironment.getInstance().addActivity(destroyPerson);
		UIEnvironment.getInstance().addActivity(fadePerson);
	}

	public static Point2D getRandomPointAroundObj(WorldObject obj) {
		double radius = (Math.sqrt((obj.getWidth() * obj.getWidth())
				+ (obj.getHeight() * obj.getHeight()))) / 2d;
		radius *= 1.3d;

		return getRandomPointAroundObj(obj, radius);
	}

	public static Point2D getRandomPointAroundObj(WorldObject obj, double averageRadius) {

		double randomAngle = randomGen.nextDouble() * 2d * Math.PI;
		double randomOffset = ((randomGen.nextDouble() - 0.5d) * 0.2d) + 1d;
		double offsetY = (Math.sin(randomAngle) * averageRadius) * randomOffset;
		double offsetX = (Math.cos(randomAngle) * averageRadius) * randomOffset;

		offsetX += obj.getBounds().getCenterX();
		offsetY += obj.getBounds().getCenterY();
		return new Point2D.Double(offsetX, offsetY);
	}

}

class DestroyActivity extends PActivity {
	private WorldObject obj;

	public DestroyActivity(WorldObject obj) {
		super(0);
		this.obj = obj;
	}

	@Override
	protected void activityStarted() {
		obj.destroy();
	}
}