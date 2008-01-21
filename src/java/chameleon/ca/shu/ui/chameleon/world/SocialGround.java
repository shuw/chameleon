package ca.shu.ui.chameleon.world;

import java.security.InvalidParameterException;
import java.util.Hashtable;
import java.util.Random;

import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.elastic.ElasticGround;
import ca.shu.ui.lib.world.piccolo.primitives.PXEdge;

/**
 * Ground which can hold a social network
 * 
 * @author Shu Wu
 */
public class SocialGround extends ElasticGround {

	public SocialGround() {
		super();

		setElasticEnabled(true);
	}

	private static final long serialVersionUID = 1L;

	private Hashtable<String, Person> personTable = new Hashtable<String, Person>();

	public void addPerson(Person person) {
		addPerson(person, true);
	}

	private void addPerson(Person person, boolean centerPos) {

		if (centerPos) {
			person.setOffset(0, 0);
			addObject(person, true);
		} else {
			addChild(person, 0);
		}
	}

	@Override
	public void childRemoved(WorldObject wo) {
		if (wo instanceof Person) {
			Person person = (Person) wo;

			personTable.remove(person.getId());
		}

		super.childRemoved(wo);
	}

	@Override
	public void childAdded(WorldObject wo) {
		if (wo instanceof Person) {
			Person person = (Person) wo;

			if (personTable.get(person.getId()) != null) {
				throw new InvalidParameterException();
			}
			personTable.put(person.getId(), person);
		}

		super.childAdded(wo);
	}

	@Override
	public void addChild(WorldObject wo, int index) {
		super.addChild(wo, index);
	}

	public boolean addMutualRelationship(Person userA, Person userB) {

		boolean userBAlreadyFriendOfA = false;
		boolean userAAlreadyFriendOfB = false;

		if (userA.isFriend(userB)) {
			userBAlreadyFriendOfA = true;
		} else {
			userA.addFriend(userB);
		}

		if (userB.isFriend(userA)) {
			userAAlreadyFriendOfB = true;
		} else {
			userB.addFriend(userA);
		}

		Util.Assert(userAAlreadyFriendOfB == userBAlreadyFriendOfA,
				"Relationship table inconsistent, only mutual relationships supported");

		if (!userAAlreadyFriendOfB) {
			/*
			 * Add an edge if the relationship didn't exist before
			 */
			PXEdge edge = new PXEdge(userA, userB, false);
			addEdge(edge);
		}
		return true;
	}

	public boolean addMutualRelationship(IUser userA, IUser userB) {
		Person personA = getPerson(userA.getId());
		if (personA == null) {
			personA = new Person(userA);
			addPerson(personA);
		}
		Person personB = getPerson(userB.getId());
		if (personB == null) {
			personB = new Person(userB);

			// If this is a new person being created, we move it to the person
			// it was related to

			Random rand = new Random();
			double xPos = personA.getOffset().getX() + (50 * (rand.nextDouble() - 0.5));
			double yPos = personA.getOffset().getY() + (50 * (rand.nextDouble() - 0.5));

			personB.setOffset(xPos, yPos);
			personB.setTransparency(0f);
			Fader fader = new Fader(personB, 1000, 1f);
			addActivity(fader);
			addPerson(personB, false);
		}

		return addMutualRelationship(personA, personB);
	}

	public Person getPerson(String id) {
		Person person = personTable.get(id);

		if (person != null) {
			Util.Assert(person.getParent() == this);
		}

		return person;

	}
}
