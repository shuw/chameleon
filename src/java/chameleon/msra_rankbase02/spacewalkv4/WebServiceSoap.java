
package msra_rankbase02.spacewalkv4;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "WebServiceSoap", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WebServiceSoap {


    /**
     * 
     * @param sessionId
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "DisposeSession", action = "http://msra-rankbase02/spacewalkv4/DisposeSession")
    @WebResult(name = "DisposeSessionResult", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
    @RequestWrapper(localName = "DisposeSession", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.DisposeSession")
    @ResponseWrapper(localName = "DisposeSessionResponse", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.DisposeSessionResponse")
    public boolean disposeSession(
        @WebParam(name = "sessionId", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String sessionId);

    /**
     * 
     * @param channelAlias
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "InitWhatsNewItems", action = "http://msra-rankbase02/spacewalkv4/InitWhatsNewItems")
    @WebResult(name = "InitWhatsNewItemsResult", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
    @RequestWrapper(localName = "InitWhatsNewItems", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.InitWhatsNewItems")
    @ResponseWrapper(localName = "InitWhatsNewItemsResponse", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.InitWhatsNewItemsResponse")
    public String initWhatsNewItems(
        @WebParam(name = "channelAlias", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String channelAlias);

    /**
     * 
     * @param count
     * @param sessionId
     * @return
     *     returns msra_rankbase02.spacewalkv4.Channel
     */
    @WebMethod(operationName = "GetWhatsNewItems", action = "http://msra-rankbase02/spacewalkv4/GetWhatsNewItems")
    @WebResult(name = "GetWhatsNewItemsResult", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
    @RequestWrapper(localName = "GetWhatsNewItems", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.GetWhatsNewItems")
    @ResponseWrapper(localName = "GetWhatsNewItemsResponse", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.GetWhatsNewItemsResponse")
    public Channel getWhatsNewItems(
        @WebParam(name = "sessionId", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String sessionId,
        @WebParam(name = "count", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        int count);

    /**
     * 
     * @param sessionId
     * @param itemId
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "InitRelatedItems", action = "http://msra-rankbase02/spacewalkv4/InitRelatedItems")
    @WebResult(name = "InitRelatedItemsResult", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
    @RequestWrapper(localName = "InitRelatedItems", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.InitRelatedItems")
    @ResponseWrapper(localName = "InitRelatedItemsResponse", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.InitRelatedItemsResponse")
    public boolean initRelatedItems(
        @WebParam(name = "sessionId", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String sessionId,
        @WebParam(name = "itemId", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String itemId);

    /**
     * 
     * @param count
     * @param sessionId
     * @return
     *     returns msra_rankbase02.spacewalkv4.Channel
     */
    @WebMethod(operationName = "GetRelatedItems", action = "http://msra-rankbase02/spacewalkv4/GetRelatedItems")
    @WebResult(name = "GetRelatedItemsResult", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
    @RequestWrapper(localName = "GetRelatedItems", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.GetRelatedItems")
    @ResponseWrapper(localName = "GetRelatedItemsResponse", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.GetRelatedItemsResponse")
    public Channel getRelatedItems(
        @WebParam(name = "sessionId", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String sessionId,
        @WebParam(name = "count", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        int count);

    /**
     * 
     * @param count
     * @param sessionId
     * @return
     *     returns msra_rankbase02.spacewalkv4.ArrayOfDelta
     */
    @WebMethod(operationName = "GetRelatedItemsDelta", action = "http://msra-rankbase02/spacewalkv4/GetRelatedItemsDelta")
    @WebResult(name = "GetRelatedItemsDeltaResult", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
    @RequestWrapper(localName = "GetRelatedItemsDelta", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.GetRelatedItemsDelta")
    @ResponseWrapper(localName = "GetRelatedItemsDeltaResponse", targetNamespace = "http://msra-rankbase02/spacewalkv4/", className = "msra_rankbase02.spacewalkv4.GetRelatedItemsDeltaResponse")
    public ArrayOfDelta getRelatedItemsDelta(
        @WebParam(name = "sessionId", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        String sessionId,
        @WebParam(name = "count", targetNamespace = "http://msra-rankbase02/spacewalkv4/")
        int count);

}
