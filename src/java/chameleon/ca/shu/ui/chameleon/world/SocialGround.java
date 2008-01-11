package ca.shu.ui.chameleon.world;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Hashtable;

import com.aetrion.flickr.people.User;

import ca.shu.ui.chameleon.objects.Person;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.objects.PEdge;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.elastic.ElasticGround;

/**
 * Ground which can hold a social network
 * 
 * @author Shu Wu
 */
public class SocialGround extends ElasticGround {

	private static final long serialVersionUID = 1L;

	private Hashtable<String, Person> personTable = new Hashtable<String, Person>();

	private Hashtable<Person, HashSet<Person>> relationshipsMap = new Hashtable<Person, HashSet<Person>>();

	public void addPerson(Person person) {
		if (personTable.get(person.getId()) != null) {
			throw new InvalidParameterException();
		}
		personTable.put(person.getId(), person);
		addChild(person);
	}

	public boolean addMutualRelationship(Person userA, Person userB) {

		boolean userBAlreadyFriendOfA = false;
		boolean userAAlreadyFriendOfB = false;
		HashSet<Person> friendsOfUserA = relationshipsMap.get(userA);
		if (friendsOfUserA == null) {
			friendsOfUserA = new HashSet<Person>();
			relationshipsMap.put(userA, friendsOfUserA);
		}
		if (friendsOfUserA.contains(userB)) {
			userBAlreadyFriendOfA = true;
		} else {
			friendsOfUserA.add(userB);
		}

		HashSet<Person> friendsOfUserB = relationshipsMap.get(userB);
		if (friendsOfUserB == null) {
			friendsOfUserB = new HashSet<Person>();
			relationshipsMap.put(userB, friendsOfUserB);
		}

		if (friendsOfUserB.contains(userA)) {
			userAAlreadyFriendOfB = true;
		} else {
			friendsOfUserB.add(userA);
		}

		Util
				.Assert(userAAlreadyFriendOfB == userBAlreadyFriendOfA,
						"Relationship table inconsistent, only mutual relationships supported");

		if (!userAAlreadyFriendOfB) {
			/*
			 * Add an edge if the relationship didn't exist before
			 */
			PEdge edge = new PEdge(userA, userB, false);
			addEdge(edge);
		}
		return true;
	}

	public boolean addMutualRelationship(User userA, User userB) {
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

			personB.setOffset(personA.getOffset().getX() + 20, personA
					.getOffset().getY() + 20);
			personB.setTransparency(0f);
			Fader fader = new Fader(personB, 1000, 1f);
			addActivity(fader);
			addPerson(personB);
		}

		return addMutualRelationship(personA, personB);
	}

	public Person getPerson(String id) {
		return personTable.get(id);
	}
}
