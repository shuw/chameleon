package ca.shu.ui.chameleon.adapters;

import java.net.URL;
import java.util.Collection;

import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.world.Searchable.SearchValuePair;

public interface IUser extends IChameleonObj {

	public String getId();

	public URL getProfilePictureURL();

	public String getDisplayName();

	public Collection<SearchValuePair> getSearchableValues();

	public void constructTooltips(TooltipBuilder builder);
	
}
