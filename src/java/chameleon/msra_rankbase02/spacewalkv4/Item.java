
package msra_rankbase02.spacewalkv4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Item complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="itemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemTick" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="itemScore" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="blogContents" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Item", propOrder = {
    "itemId",
    "itemTitle",
    "itemUrl",
    "itemTick",
    "itemScore",
    "blogContents"
})
public class Item {

    protected String itemId;
    protected String itemTitle;
    protected String itemUrl;
    protected long itemTick;
    protected float itemScore;
    protected String blogContents;

    /**
     * Gets the value of the itemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Sets the value of the itemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemId(String value) {
        this.itemId = value;
    }

    /**
     * Gets the value of the itemTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemTitle() {
        return itemTitle;
    }

    /**
     * Sets the value of the itemTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemTitle(String value) {
        this.itemTitle = value;
    }

    /**
     * Gets the value of the itemUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemUrl() {
        return itemUrl;
    }

    /**
     * Sets the value of the itemUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemUrl(String value) {
        this.itemUrl = value;
    }

    /**
     * Gets the value of the itemTick property.
     * 
     */
    public long getItemTick() {
        return itemTick;
    }

    /**
     * Sets the value of the itemTick property.
     * 
     */
    public void setItemTick(long value) {
        this.itemTick = value;
    }

    /**
     * Gets the value of the itemScore property.
     * 
     */
    public float getItemScore() {
        return itemScore;
    }

    /**
     * Sets the value of the itemScore property.
     * 
     */
    public void setItemScore(float value) {
        this.itemScore = value;
    }

    /**
     * Gets the value of the blogContents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlogContents() {
        return blogContents;
    }

    /**
     * Sets the value of the blogContents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlogContents(String value) {
        this.blogContents = value;
    }

}
