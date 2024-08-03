# The idea
It's not great to have port 22 open for everyone (0.0.0.0) to connect to on your ec2 instances.
Instead what we can do is to build a script that utilizes aws cli to modify a securitry group ingress rule - changing it to your current ip every time you run the script.

# How to use
1. set up your aws cli
2. modify value inside (def security-group-id "put your security group id here")
3. done. You can now run the script with babashka

# Plans
For the future it might be nice to add support for multiple different groups and multiple accounts. 
Not hard to do, but for now i don't need that feature. 