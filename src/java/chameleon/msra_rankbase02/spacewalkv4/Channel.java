
package msra_rankbase02.spacewalkv4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Channel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Channel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hashCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="authorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profilePictureUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spaceAlias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spaceUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemList" type="{http://msra-rankbase02/spacewalkv4/}ArrayOfItem" minOccurs="0"/>
 *         &lt;element name="friendList" type="{http://msra-rankbase02/spacewalkv4/}ArrayOfChannel" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Channel", propOrder = {
    "hashCode",
    "authorName",
    "profilePictureUrl",
    "spaceAlias",
    "spaceUrl",
    "itemList",
    "friendList"
})
public class Channel {

    protected int hashCode;
    protected String authorName;
    protected String profilePictureUrl;
    protected String spaceAlias;
    protected String spaceUrl;
    protected ArrayOfItem itemList;
    protected ArrayOfChannel friendList;

    /**
     * Gets the value of the hashCode property.
     * 
     */
    public int getHashCode() {
        return hashCode;
    }

    /**
     * Sets the value of the hashCode property.
     * 
     */
    public void setHashCode(int value) {
        this.hashCode = value;
    }

    /**
     * Gets the value of the authorName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Sets the value of the authorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorName(String value) {
        this.authorName = value;
    }

    /**
     * Gets the value of the profilePictureUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /**
     * Sets the value of the profilePictureUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfilePictureUrl(String value) {
        this.profilePictureUrl = value;
    }

    /**
     * Gets the value of the spaceAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpaceAlias() {
        return spaceAlias;
    }

    /**
     * Sets the value of the spaceAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpaceAlias(String value) {
        this.spaceAlias = value;
    }

    /**
     * Gets the value of the spaceUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpaceUrl() {
        return spaceUrl;
    }

    /**
     * Sets the value of the spaceUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpaceUrl(String value) {
        this.spaceUrl = value;
    }

    /**
     * Gets the value of the itemList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfItem }
     *     
     */
    public ArrayOfItem getItemList() {
        return itemList;
    }

    /**
     * Sets the value of the itemList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfItem }
     *     
     */
    public void setItemList(ArrayOfItem value) {
        this.itemList = value;
    }

    /**
     * Gets the value of the friendList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfChannel }
     *     
     */
    public ArrayOfChannel getFriendList() {
        return friendList;
    }

    /**
     * Sets the value of the friendList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfChannel }
     *     
     */
    public void setFriendList(ArrayOfChannel value) {
        this.friendList = value;
    }

}
