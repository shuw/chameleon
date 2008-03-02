
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
 *         &lt;element name="DisposeSessionResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "disposeSessionResult"
})
@XmlRootElement(name = "DisposeSessionResponse")
public class DisposeSessionResponse {

    @XmlElement(name = "DisposeSessionResult")
    protected boolean disposeSessionResult;

    /**
     * Gets the value of the disposeSessionResult property.
     * 
     */
    public boolean isDisposeSessionResult() {
        return disposeSessionResult;
    }

    /**
     * Sets the value of the disposeSessionResult property.
     * 
     */
    public void setDisposeSessionResult(boolean value) {
        this.disposeSessionResult = value;
    }

}
