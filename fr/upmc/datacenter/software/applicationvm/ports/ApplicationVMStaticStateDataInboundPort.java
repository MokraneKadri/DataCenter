/**
 * 
 */
package fr.upmc.datacenter.software.applicationvm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.components.ports.AbstractDataInboundPort;

/**
 * The class <code>ApplicationVMStaticStateDataInboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2 oct. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class 			ApplicationVMStaticStateDataInboundPort
extends		AbstractDataInboundPort
{
	private static final long serialVersionUID = 1L;

	public ApplicationVMStaticStateDataInboundPort(
			Class<?> implementedPullInterface,
			Class<?> implementedPushInterface, ComponentI owner)
			throws Exception {
		super(implementedPullInterface, implementedPushInterface, owner);
		// TODO Auto-generated constructor stub
	}

	public ApplicationVMStaticStateDataInboundPort(String uri,
			Class<?> implementedPullInterface,
			Class<?> implementedPushInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedPullInterface, implementedPushInterface, owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataI get() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
