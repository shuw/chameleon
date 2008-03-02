
package msra_rankbase02.spacewalkv4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetRelatedItemsResult" type="{http://msra-rankbase02/spacewalkv4/}Channel" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getRelatedItemsResult"
})
@XmlRootElement(name = "GetRelatedItemsResponse")
public class GetRelatedItemsResponse {

    @XmlElement(name = "GetRelatedItemsResult")
    protected Channel getRelatedItemsResult;

    /**
     * Gets the value of the getRelatedItemsResult property.
     * 
     * @return
     *     possible object is
     *     {@link Channel }
     *     
     */
    public Channel getGetRelatedItemsResult() {
        return getRelatedItemsResult;
    }

    /**
     * Sets the value of the getRelatedItemsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Channel }
     *     
     */
    public void setGetRelatedItemsResult(Channel value) {
        this.getRelatedItemsResult = value;
    }

}
