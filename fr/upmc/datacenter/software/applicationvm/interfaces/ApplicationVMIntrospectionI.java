package fr.upmc.datacenter.software.applicationvm.interfaces;

import java.util.Map;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;

/**
 * The class <code>ApplicationVMIntrospectionI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 5 oct. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ApplicationVMIntrospectionI
extends		OfferedI,
			RequiredI
{
	/**
	 * return a map of the application VM port URI by their types.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	a map from application VM port types to their URI.
	 * @throws Exception
	 */
	public Map<ApplicationVMPortTypes, String>	getAVMPortsURI() throws Exception ;

	/**
	 * return the static state of the application VM as an instance of
	 * <code>ApplicationVMStaticStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the current static state of the application VM.
	 * @throws Exception
	 */
	public ApplicationVMStaticStateI	getStaticState() throws Exception ;

	/**
	 * return the dynamic state of the application VM as an instance of
	 * <code>ApplicationVMDynamicStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the dynamic state of the application VM.
	 * @throws Exception
	 */
	public ApplicationVMDynamicStateI	getDynamicState() throws Exception ;
}
