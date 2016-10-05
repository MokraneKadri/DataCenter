package fr.upmc.datacenter.software.applicationvm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.ports.AbstractDataInboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;

/**
 * The class <code>ApplicationVMDynamicStateDataInboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 30 sept. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ApplicationVMDynamicStateDataInboundPort
extends		AbstractDataInboundPort
implements	ControlledDataOfferedI.ControlledPullI
{
	private static final long serialVersionUID = 1L;

	public ApplicationVMDynamicStateDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ControlledDataOfferedI.ControlledPullI.class,
											DataOfferedI.PushI.class, owner) ;

		assert	owner instanceof ApplicationVM ;
	}

	public ApplicationVMDynamicStateDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ControlledDataOfferedI.ControlledPullI.class,
											DataOfferedI.PushI.class, owner);

		assert	owner instanceof ApplicationVM ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		return null;
	}
}
