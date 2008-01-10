package ca.shu.ui.chameleon.world;

import java.security.InvalidParameterException;
import java.util.Hashtable;

import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.lib.objects.PEdge;
import ca.shu.ui.lib.world.elastic.ElasticGround;

public class SocialGround extends ElasticGround {

	private static final long serialVersionUID = 1L;

	public void addPerson(Person person) {
		if (personTable.get(person.getId()) != null) {
			throw new InvalidParameterException();
		}
		personTable.put(person.getId(), person);
		addChild(person);
	}

	public void addRelationship(Person personA, Person personB) {
		PEdge edge = new PEdge(personA, personB, false);
		addEdge(edge);
	}

	public Person getPerson(String id) {
		return personTable.get(id);
	}

	private Hashtable<String, Person> personTable = new Hashtable<String, Person>();

	public void addRelationship(String id_personA, String id_personB) {
		Person personA = getPerson(id_personA);
		Person personB = getPerson(id_personB);

		if (personA != null && personB != null) {
			addRelationship(personA, personB);
		} else {
			throw new InvalidParameterException();
		}
	}
}