package kvm;

/**
 * Class for monitoring and creating different VM's on the hypervisor
 */

import org.libvirt.Connect;
import org.libvirt.ConnectAuth;
import org.libvirt.ConnectAuthDefault;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;

public class TestVM {

	/**
	 * Set property. Still not sure what this is..
	 */
	public TestVM() {
		System.setProperty("jna.library.path",
				"C:\\Program Files (x86)\\Libvirt\\bin");
	}

	/**
	 * Make connection to hypervisor and return it
	 * 
	 * @return
	 * @throws LibvirtException
	 */
	private Connect makeConn() throws LibvirtException {
		ConnectAuth ca = new ConnectAuthDefault();
		Connect conn = new Connect("qemu+tcp://192.168.45.22/system", ca, 0);

		return conn;
	}

	/**
	 * Method for monitoring all VM's
	 */
	public void monitorDomains() {
		try {

			Connect conn = makeConn();

			NodeInfo ni = conn.nodeInfo();

			System.out.println("model: " + ni.model + " mem(kb):" + ni.memory);

			int numOfVMs = conn.numOfDomains();

			for (int i = 1; i < numOfVMs + 1; i++) {
				Domain vm = conn.domainLookupByID(i);
				System.out.println("vm name: " + vm.getName() + "  type: "
						+ vm.getOSType() + " max mem: " + vm.getMaxMemory()
						+ " max cpu: " + vm.getMaxVcpus());
			}
			String cap = conn.getCapabilities();
			System.out.println("cap: " + cap);
			conn.close();
		} catch (LibvirtException le) {
			le.printStackTrace();
		}
	}

	/**
	 * Method for creating VM's with XML files
	 * 
	 * @throws Exception
	 */
	public void createDomains() throws Exception {

		Connect conn = makeConn();

		try {
			// Prob not the good one..
			conn.domainDefineXML("<domain type='test' id='2'>"
					+ "  <name>deftest</name>" + " "
					+ " <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e70</uuid>"
					+ "  <memory>8388608</memory>" + "  <vcpu>2</vcpu>"
					+ "  <os><type arch='i686'>hvm</type></os>"
					+ "  <on_reboot>restart</on_reboot>"
					+ "  <on_poweroff>destroy</on_poweroff>"
					+ "  <on_crash>restart</on_crash>" + "</domain>");

			// Prob only for linux?
			conn.domainCreateLinux("<domain type='test' id='3'>"
					+ "  <name>createst</name>"
					+ "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>"
					+ "  <memory>8388608</memory>" + "  <vcpu>2</vcpu>"
					+ "  <os><type arch='i686'>hvm</type></os>"
					+ "  <on_reboot>restart</on_reboot>"
					+ "  <on_poweroff>destroy</on_poweroff>"
					+ "  <on_crash>restart</on_crash>" + "</domain>", 0);

			// Prob only for Prob also for Windows?
			conn.domainCreateXML("<domain type='test' id='3'>"
					+ "  <name>createst</name>"
					+ "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>"
					+ "  <memory>8388608</memory>" + "  <vcpu>2</vcpu>"
					+ "  <os><type arch='i686'>hvm</type></os>"
					+ "  <on_reboot>restart</on_reboot>"
					+ "  <on_poweroff>destroy</on_poweroff>"
					+ "  <on_crash>restart</on_crash>" + "</domain>", 0);

			System.out.println("Number of domains" + conn.numOfDomains());
			System.out.println("Number of listed domains"
					+ conn.listDomains().length);
			System.out.println("Number of defined domains"
					+ conn.numOfDefinedDomains());
			System.out.println("Number of listed defined domains"
					+ conn.listDefinedDomains().length);
		}

		finally {
			conn.close();
		}
	}

}
