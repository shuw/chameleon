package ca.shu.ui.chameleon;

import ca.shu.ui.lib.world.elastic.ElasticObject;
import edu.umd.cs.piccolo.nodes.PImage;

public class Person extends ElasticObject {

	private static long idGen = 0;
	private static final long serialVersionUID = 1L;
	private final long id;

	private final String name;

	public Person() {
		name = "Hello World";
		id = idGen++;
		addChild(new PImage("images/Person.gif"));
	}

	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

}
