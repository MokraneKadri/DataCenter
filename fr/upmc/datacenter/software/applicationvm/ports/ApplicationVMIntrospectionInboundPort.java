package fr.upmc.datacenter.software.applicationvm.ports;

import java.util.Map;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * The class <code>ApplicationVMIntrospectionInboundPort</code>
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
public class			ApplicationVMIntrospectionInboundPort
extends		AbstractInboundPort
implements	ApplicationVMIntrospectionI
{
	private static final long serialVersionUID = 1L;

	public				ApplicationVMIntrospectionInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ApplicationVMIntrospectionI.class, owner) ;

		assert	owner instanceof ApplicationVM ;
	}

	public				ApplicationVMIntrospectionInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ApplicationVMIntrospectionI.class, owner);

		assert	owner instanceof ApplicationVM ;
	}


	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getAVMPortsURI()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String>	getAVMPortsURI()
	throws Exception
	{
		final ApplicationVM avm = (ApplicationVM) this.owner ;
		return this.owner.handleRequestSync(
							new ComponentI.ComponentService<
												Map<ApplicationVMPortTypes,
													String>>() {
								@Override
								public Map<ApplicationVMPortTypes, String>
																		call()
								throws Exception
								{
									return avm.getAVMPortsURI() ;
								}
							}) ;
	}

	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getStaticState()
	 */
	@Override
	public ApplicationVMStaticStateI	getStaticState()
	throws Exception
	{
		final ApplicationVM avm = (ApplicationVM) this.owner ;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<ApplicationVMStaticStateI>() {
						@Override
						public ApplicationVMStaticStateI call()
						throws Exception
						{
							return avm.getStaticState() ;
						}
					}) ;
	}

	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI#getDynamicState()
	 */
	@Override
	public ApplicationVMDynamicStateI	getDynamicState() throws Exception
	{
		final ApplicationVM avm = (ApplicationVM) this.owner ;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<ApplicationVMDynamicStateI>() {
						@Override
						public ApplicationVMDynamicStateI call()
						throws Exception
						{
							return avm.getDynamicState() ;
						}
					}) ;
	}
}
