#!/bin/bash
#restore script
#copied from  https://help.ubuntu.com/community/BackupYourSystem/TAR

#options
#tar -x extract
#tar -v makes it verbose (not needed but nice to show that it actually runs)
#tar -p preserves all file permisions
#tar -z states that the file is compressed
#tar -f specifies the backupfile location

#-C changes the directory where everything should be copied to, in this case, root
#--numeric-owner makes certain that the numeric owners of files are restored (because user id's in the system may be totally different)

tar -xvpzf /home/virt/backup.tar.gz -C / --numeric-owner