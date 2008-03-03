package ca.shu.ui.chameleon.spaces.objects;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.chameleon.adapters.IUser;
import ca.shu.ui.lib.world.Searchable.SearchValuePair;

public class SpaceUser implements IUser {
	private final String alias;
	private final URL profilePictureUrl;
	private final URL profileUrl;
	private Collection<SearchValuePair> searchableValues;

	public SpaceUser(String displayName, URL profilePictureUrl, URL profileURL) {
		super();
		this.alias = displayName;

		this.profilePictureUrl = profilePictureUrl;
		this.profileUrl = profileURL;

		init();
	}

	private void init() {
		LinkedList<SearchValuePair> sValues = new LinkedList<SearchValuePair>();
		sValues.add(new SearchValuePair("Alias", alias));

		this.searchableValues = new ArrayList<SearchValuePair>(sValues);
	}

	@Override
	public String getId() {
		return alias;
	}

	@Override
	public URL getProfilePictureURL() {
		return profilePictureUrl;
	}

	@Override
	public String getDisplayName() {
		return alias;
	}

	@Override
	public URL getURL() {
		return profileUrl;
	}

	@Override
	public void constructTooltips(TooltipBuilder builder) {
		// TODO: add tool tips
	}

	@Override
	public Collection<SearchValuePair> getSearchableValues() {
		return searchableValues;
	}
}
