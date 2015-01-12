#!/bin/bash
# Creates a snapshot of the volume storing the KVM images, and then
# using that snapshot copies the images. The benefit of using a snapshot
# (as opposed to copying the images) is that the VM does not need to be
# shutdown.

# Include appropriate paths (since cron doesn't use your normal environment)
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin

# The date of the backup
bkpdate=`date +%Y-%m-%d`

# Create a LVM snapshot of the var LV
# varsnap is the name of the snapshot followed by the directory that should be snapshotted
lvcreate -L 5G -s -n varsnap /var/lib/libvirt/images/

# Mount the snapshot
mount /dev/virt01/varsnap /mnt/snapshot

#db query = select VMName from vm where VMSLA != "bronze"
$array = mysql -D virt -u root -pVirtTeam1DBP@55W0rdH@xx0r5 -e "select VMName from vm where VMSLA != 'bronze'"

# Copy the KVM images
#	loop --> array namen, for each 
for i in "${array[@]}"
	do
	tar --create --sparse --file /mnt/bkp/$array[i]-${bkpdate}.img.tar /mnt/snapshot/vm/$array[i].img
	done

# Remove the snapshot
umount /mnt/snapshot
lvremove -f /dev/ictweb/varsnap 
