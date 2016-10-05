package fr.upmc.datacenter.software.applicationvm.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The class <code>ApplicationVMStaticStateDataI</code>
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
public interface		ApplicationVMStaticStateDataI
extends		DataOfferedI,
			DataRequiredI
{
	// The data interface is defined as an external interface
	// ApplicationVMStaticStateI
}
