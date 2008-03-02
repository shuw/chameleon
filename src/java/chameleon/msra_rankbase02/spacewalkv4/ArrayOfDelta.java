
package msra_rankbase02.spacewalkv4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfDelta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfDelta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Delta" type="{http://msra-rankbase02/spacewalkv4/}Delta" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDelta", propOrder = {
    "delta"
})
public class ArrayOfDelta {

    @XmlElement(name = "Delta", nillable = true)
    protected List<Delta> delta;

    /**
     * Gets the value of the delta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the delta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDelta().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Delta }
     * 
     * 
     */
    public List<Delta> getDelta() {
        if (delta == null) {
            delta = new ArrayList<Delta>();
        }
        return this.delta;
    }

}
