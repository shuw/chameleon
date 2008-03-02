
package msra_rankbase02.spacewalkv4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="channelAlias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "channelAlias"
})
@XmlRootElement(name = "InitWhatsNewItems")
public class InitWhatsNewItems {

    protected String channelAlias;

    /**
     * Gets the value of the channelAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannelAlias() {
        return channelAlias;
    }

    /**
     * Sets the value of the channelAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannelAlias(String value) {
        this.channelAlias = value;
    }

}
