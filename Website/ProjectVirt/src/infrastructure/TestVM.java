package infrastructure;

/**
 * Class for monitoring and creating different VM's on the hypervisor
 * @author PieterDieleman
 */

import java.util.UUID;

import org.libvirt.Connect;
import org.libvirt.ConnectAuth;
import org.libvirt.ConnectAuthDefault;
import org.libvirt.Domain;
import org.libvirt.DomainInfo;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

public class TestVM {

	/**
	 * Set property. Still not sure what this is..
	 */
	public TestVM() {
		System.setProperty("jna.library.path",
				"/home/sne/workspace/ProjectVirt");
	}

	/**
	 * Make connection to hypervisor and return it
	 * @author PieterDieleman
	 * @return
	 * @throws LibvirtException
	 */
//	private static Connect makeConn() throws LibvirtException {
//		ConnectAuth ca = new ConnectAuthDefault();
//		Connect conn = new Connect("qemu:////system", ca, 0);

//		return conn;
//	}

	/**
	 * Method for monitoring all VM's
	 */

	/**
	 * Method for creating VM's with XML files
	 * @throws LibvirtException 
	 * 
	 * @throws Exception
	 */
	public static void createVM(String OSType, String OSName, int Memory, int Storage, int CPU) throws LibvirtException{
		
		//setup libvirt connection, second parameter is a boolean for read-only 
		Connect conn = new Connect("qemu:///system", false);
		
		//Temp, might use it later on in the project.
		//UUID UUID = java.util.UUID.randomUUID();
		if(!conn.isConnected()){
			System.out.println("Error, hypervisor connection not available!");
		}
		else{
			System.out.println("Connection is ready!");
			try{
				
				Domain newDm = conn.domainDefineXML("<domain type='" + OSType + "'>" +
				"<name>" + OSName + "</name>" +
				"<uuid></uuid>" +
				"<memory>" + Memory + "</memory>" +
				"<vcpu>" + CPU + "</vcpu>" +
				"<os>" +
			    "<type arch='x86_64' machine='pc-1.1'>hvm</type>" +
			    "<boot dev='cdrom'/>" +
			    "</os>" +
			    "<devices>" +
			    "<emulator>/usr/bin/qemu-system-x86_64</emulator>" +
			    "<disk type='file' device='disk'>" +
			    "<source file='/home/sne/slackware-14.1-source-dvd.iso'/> "+
			    "<target dev='hdc'/>"+
			    //Add a second HDD with
			    "<readonly/>"+
			    "</disk>"+
			    "<disk type='file' device='disk'>"+
			    "<source file='/var/lib/libvirt/images/testVolume'/>"+
			    "<target dev='hda'/>"+
			    "</disk>"+
			    "<interface type='network'>"+
			    "<source network='default'/>"+
			    "</interface>"+
			    "<graphics type='vnc' port='-1'/>"+
			    "</devices>"+
				"</domain>");
				//conn.domainXMLFromNative("", arg1, arg2)
				//Domain newDm = conn.domainLookupByUUID(UUID);
				newDm.create();
				
				if(newDm.isActive() == 0){
					System.out.println("Error, domain is not running");
				}
			
			} catch (LibvirtException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);
			}
			finally{
				try {
					conn.close();
				} catch (LibvirtException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
