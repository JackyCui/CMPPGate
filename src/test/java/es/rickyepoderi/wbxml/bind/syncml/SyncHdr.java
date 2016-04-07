//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.30 at 11:39:15 AM CEST 
//


package es.rickyepoderi.wbxml.bind.syncml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "verDTD",
    "verProto",
    "sessionID",
    "msgID",
    "target",
    "source",
    "respURI",
    "noResp",
    "cred",
    "meta"
})
@XmlRootElement(name = "SyncHdr", namespace="SYNCML:SYNCML1.1")
public class SyncHdr {

    @XmlElement(name = "VerDTD", namespace="SYNCML:SYNCML1.1", required = true)
    protected String verDTD;
    @XmlElement(name = "VerProto", namespace="SYNCML:SYNCML1.1", required = true)
    protected String verProto;
    @XmlElement(name = "SessionID", namespace="SYNCML:SYNCML1.1", required = true)
    protected String sessionID;
    @XmlElement(name = "MsgID", namespace="SYNCML:SYNCML1.1", required = true)
    protected String msgID;
    @XmlElement(name = "Target", namespace="SYNCML:SYNCML1.1", required = true)
    protected Target target;
    @XmlElement(name = "Source", namespace="SYNCML:SYNCML1.1", required = true)
    protected Source source;
    @XmlElement(name = "RespURI", namespace="SYNCML:SYNCML1.1")
    protected String respURI;
    @XmlElement(name = "NoResp", namespace="SYNCML:SYNCML1.1")
    protected NoResp noResp;
    @XmlElement(name = "Cred", namespace="SYNCML:SYNCML1.1")
    protected Cred cred;
    @XmlElement(name = "Meta", namespace="SYNCML:SYNCML1.1")
    protected Meta meta;

    /**
     * Gets the value of the verDTD property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerDTD() {
        return verDTD;
    }

    /**
     * Sets the value of the verDTD property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerDTD(String value) {
        this.verDTD = value;
    }

    /**
     * Gets the value of the verProto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerProto() {
        return verProto;
    }

    /**
     * Sets the value of the verProto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerProto(String value) {
        this.verProto = value;
    }

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the msgID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgID() {
        return msgID;
    }

    /**
     * Sets the value of the msgID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgID(String value) {
        this.msgID = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link Target }
     *     
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link Target }
     *     
     */
    public void setTarget(Target value) {
        this.target = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link Source }
     *     
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link Source }
     *     
     */
    public void setSource(Source value) {
        this.source = value;
    }

    /**
     * Gets the value of the respURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRespURI() {
        return respURI;
    }

    /**
     * Sets the value of the respURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRespURI(String value) {
        this.respURI = value;
    }

    /**
     * Gets the value of the noResp property.
     * 
     * @return
     *     possible object is
     *     {@link NoResp }
     *     
     */
    public NoResp getNoResp() {
        return noResp;
    }

    /**
     * Sets the value of the noResp property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoResp }
     *     
     */
    public void setNoResp(NoResp value) {
        this.noResp = value;
    }

    /**
     * Gets the value of the cred property.
     * 
     * @return
     *     possible object is
     *     {@link Cred }
     *     
     */
    public Cred getCred() {
        return cred;
    }

    /**
     * Sets the value of the cred property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cred }
     *     
     */
    public void setCred(Cred value) {
        this.cred = value;
    }

    /**
     * Gets the value of the meta property.
     * 
     * @return
     *     possible object is
     *     {@link Meta }
     *     
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the value of the meta property.
     * 
     * @param value
     *     allowed object is
     *     {@link Meta }
     *     
     */
    public void setMeta(Meta value) {
        this.meta = value;
    }

}