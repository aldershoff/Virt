package libvirtAccessObject;

/**
 * Class for monitoring and creating different VM's on the hypervisor
 */

import java.util.UUID;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;

public class TestVM {

	/**
	 * Set property. Still not sure what this is..
	 */
	public TestVM() {
		System.setProperty("jna.library.path",
				"/home/virt");
	}

	/**
	 * Make connection to hypervisor and return it
	 * 
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
	//public void monitorDomains() {
	//	try {
//
	//		Connect conn = makeConn();
//
	//		NodeInfo ni = conn.nodeInfo();
//
	//		System.out.println("model: " + ni.model + " mem(kb):" + ni.memory);
//
	//		int numOfVMs = conn.numOfDomains();
//
	//		for (int i = 1; i < numOfVMs + 1; i++) {
		///		Domain vm = conn.domainLookupByID(i);
			//	System.out.println("vm name: " + vm.getName() + "  type: "
				//		+ vm.getOSType() + " max mem: " + vm.getMaxMemory()
					//	+ " max cpu: " + vm.getMaxVcpus());
			//}
			//String cap = conn.getCapabilities();
			//System.out.println("cap: " + cap);
			//conn.close();
	//	} catch (LibvirtException le) {
	//		le.printStackTrace();
//		}
//	}

	/**
	 * Method for creating VM's with XML files
	 * @throws LibvirtException 
	 * 
	 * @throws Exception
	 */
	
	
	public UUID createVM(String OSType, String OSName, int Memory, int Storage, int CPU) throws LibvirtException{
		
		//setup libvirt connection, second parameter is a boolean for read-only 
		Connect conn = new Connect("qemu:///system", false);
		
		//Temp, might use it later on in the project.
		
		UUID UUID = java.util.UUID.randomUUID();
	
		//TODO: CONVERT INPUT TO BYTES
		try{
			StorageVol mainVol = conn.storageVolLookupByPath("/var/lib/libvirt/images/mainVolume.img");
			System.out.println(mainVol);
			//String volXML = mainVol.getXMLDesc(0);
			StoragePool createPool = conn.storagePoolLookupByName("default");
			createPool.storageVolCreateXMLFrom("<volume>" +
			"<name>" + UUID + ".img</name>"+
			"<key>/var/lib/libvirt/images/" + UUID + ".img</key>"+
			"<capacity unit='bytes'>2097152000</capacity>"+
			"<allocation unit='bytes'>0</allocation>"+
			"<target>"+
			"<path>/var/lib/libvirt/images/" + UUID + ".img</path>"+
			"</target>"+
			"</volume>", mainVol, 0);
		}
		catch(LibvirtException e){
			System.out.println(e);
			
		}
		finally{
			//s
		}

//		
//		StringBuilder commandBuilder = new StringBuilder("qemu-img create -f raw /var/lib/libvirt/images/"+ UUID +".img "+ Storage +"M");
//		String command = commandBuilder.toString();
//		Runtime runtime = Runtime.getRuntime();
//		Process process = null;
//		try {
//		    process = runtime.exec(command);
//		    System.out.println(command);
//		    System.out.println("from process try..catch");
//		} catch (IOException e1) {
//		    e1.printStackTrace();
//		    System.out.println(e1.getMessage());
//		}finally{
//		    System.out.println("from finally entry");
//		    process.destroy();
//		}
		
		if(!conn.isConnected()){
			System.out.println("Error, hypervisor connection not available!");
		}
		else{
			System.out.println("Connection is ready!");
			try{
				//Create HDD with Qemu

				
				Domain newDm = conn.domainDefineXML("<domain type='" + OSType + "'>" +
				"<name>" + OSName + "</name>" +
				"<uuid>" + UUID + "</uuid>" +
				"<memory>" + Memory + "</memory>" +
				"<vcpu>" + CPU + "</vcpu>" +
				"<os>" +
			    "<type arch='x86_64' machine='pc-1.1'>hvm</type>" +
			    "<boot dev='cdrom'/>" +
			    "</os>" +
			    "<devices>" +
			    "<emulator>/usr/bin/qemu-system-x86_64</emulator>" +
			    "<disk type='file' device='cdrom'>" +
			    "<source file='/home/sne/ubuntu-14.04.1-server-amd64.iso'/> "+
			    "<target dev='hdc'/>"+
			    //"<readonly/>"+
			    "</disk>"+
			    "<disk type='file' device='disk'>"+
			    "<source file='/var/lib/libvirt/images/" + UUID + ".img'/> " +
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

				
				return UUID;
				
				
			} catch (LibvirtException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);
				
				return null;
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
		return null;
	}
}
