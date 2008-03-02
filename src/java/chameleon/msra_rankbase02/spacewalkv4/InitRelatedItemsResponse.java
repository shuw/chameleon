
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
 *         &lt;element name="InitRelatedItemsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "initRelatedItemsResult"
})
@XmlRootElement(name = "InitRelatedItemsResponse")
public class InitRelatedItemsResponse {

    @XmlElement(name = "InitRelatedItemsResult")
    protected boolean initRelatedItemsResult;

    /**
     * Gets the value of the initRelatedItemsResult property.
     * 
     */
    public boolean isInitRelatedItemsResult() {
        return initRelatedItemsResult;
    }

    /**
     * Sets the value of the initRelatedItemsResult property.
     * 
     */
    public void setInitRelatedItemsResult(boolean value) {
        this.initRelatedItemsResult = value;
    }

}
