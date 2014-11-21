#!/bin/bash
#backup script
#copied from  https://help.ubuntu.com/community/BackupYourSystem/TAR

#options
#tar -c copies everything
#tar -v makes it verbose (not needed but nice to show that it actually runs)
#tar -p preserves all file permisions
#tar -z compresses the resulting file
#tar -f specifies the resulting file location

#exclude specifies items which have to be excluded, in this case, the resulting file has to be excluded
#one-file-system/ makes certain that specific folder such as /proc /sys /mnt etc get excluded

tar -cvpzf /home/virt/backup.tar.gz --exclude=/home/virt/backup.tar.gz --one-file-system/