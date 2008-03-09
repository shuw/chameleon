package ca.shu.ui.chameleon.world;

import java.security.InvalidParameterException;
import java.util.Hashtable;

import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.chameleon.util.ChameleonUtil;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.elastic.ElasticEdge;
import ca.shu.ui.lib.world.elastic.ElasticGround;

/**
 * Ground which can hold a social network
 * 
 * @author Shu Wu
 */
public class SocialGround extends ElasticGround {

	private static final long serialVersionUID = 1L;

	private Hashtable<String, Person> personTable = new Hashtable<String, Person>();

	public SocialGround() {
		super();

		setElasticEnabled(true);
	}

	@Override
	public void addChild(WorldObject wo, int index) {
		super.addChild(wo, index);
	}

	public boolean addMutualRelationship(IUser userA, IUser userB) {

		Person personA = addPerson(userA);

		Person personB = getPerson(userB.getId());
		if (personB == null) {
			personB = addPerson(userB, false);

			// If this is a new person being created, we move it to the
			// person
			// it was related to

			ChameleonUtil.setOffsetAroundOrigin(personA, personB, 150);

			personB.setTransparency(0f);
			Fader fader = new Fader(personB, 1000, 1f);
			UIEnvironment.getInstance().addActivity(fader);
		}

		return addMutualRelationship(personA, personB);

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

		if (userAAlreadyFriendOfB != userBAlreadyFriendOfA) {
			Util.debugMsg("Relationship table inconsistent, only mutual relationships supported");
		}

		if (!userAAlreadyFriendOfB) {
			/*
			 * Add an edge if the relationship didn't exist before
			 */
			ElasticEdge edge = new ElasticEdge(userA, userB, 300, false);
			addEdge(edge);
		}
		return true;
	}

	public Person addPerson(IUser user) {
		return addPerson(user, true);
	}

	private Person addPerson(IUser user, boolean centerPos) {
		Person person = personTable.get(user.getId());
		if (person == null) {
			person = new Person(user);
		} else {
			return person;
		}

		if (centerPos) {
			person.setOffset(0, 0);
			addChildFancy(person, true);
		} else {

			addChild(person, 0);
		}
		return person;
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
	public void childRemoved(WorldObject wo) {
		if (wo instanceof Person) {
			Person person = (Person) wo;

			personTable.remove(person.getId());
		}

		super.childRemoved(wo);
	}

	public Person getPerson(String id) {
		Person person = personTable.get(id);

		if (person != null) {
			Util.Assert(person.getParent() == this);
		}

		return person;

	}
}
