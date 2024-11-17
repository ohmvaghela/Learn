# Cloud Computing
- It is on-demand network access to shared computing resources
- A model for managing storing and processing data online via the internet
## Delivery model/ Service model of cloud computing 
- Types
	- SaaS : Software as a Service
	- PaaS : Platform as a Service
	- IaaS : Infracture as a Service 
- Software as a service : allows users to connect to and use cloud-based apps over the Internet
	- Mostly end users are frequest users like google workspace, microsoft office 365
- Platform as a service : a complete development and deployment environment in the cloud
	- cloud computing service model that provides a platform for developers to build, test, deploy, manage, and update applications
	- Used by developers Eg: Google app engine, Heroku
- Infrastructure as a service : a cloud computing model that provides on-demand access to computing resources such as servers, storage, networking, and virtualization
	- Used by System admins : amazon EC2, google computing engine

## Cloud deployment model
- Types
	- Public Cloud (Most openness) 
	- Hybrid Cloud
	- Private Cloud (Least openness)
- Public Cloud :  Deployed globally
	- Shared resources
	- Cost is generally pay on amount of resouces used
	- Management offloaded to hosts
- Private Cloud : Deployed locally
	- Managed by customers
	- Private resources
- Hybrid Cloud

## Amazon S3 (Simple Storage Service)
### Core conecpts
- Buckets
	- General purpose File storage
- Objects
	- Contents stored in buckets like media, js, csv, zip...
	- Max size 5TB
- Accessing
	1. using url : http://s3/amazonaws.com/<bucket_name>/<object_name>
	2. Using programs like : django, node...
- S3 storage classes
	- allows to reduce cost with reduce in CAP  
	- Eg: Standard, Intelligent, Infrequent Access, Glacier
> - HOT data: data with more frequest access
> - COLD data : data with less frequest access
- Security
	- Public access is blocked by default
	- Data Protection : High durability and availablity gaurantee
	- Access : managed AWS IAM ( Identity and Access Management)
	- Auditing
		- Access Logs, Action logs, alarm
	- Infra security
## EC2
- EC2 : Elastic Compute Cloud | Infrastructure as a service | IaaS
- Sizing and configs 
	- OS : windows and Linux
	- Compute power and cores
	- RAM
	- Storage (EBS or/and EFS)
	- Network card
	- Firewall rules
	- Bootstrap script (First launch)

## Storage in AWS
	- Block storage : EBS (Elastic block store) for persistant data, Instance storage( Cache storage) 
	- Object storage : S3(simple Storage Service) 
	- File storage : EFS(Elastic File System) only for linux works on NFS, FSX for windows


## ELB ( Elastic Load Balancer )

## AMI (Amazon Machine image)
- Pre-configured with OS, softwares and other settings

## VPC (Virtual Private Network)
- `Logically isolated` virtual network
- Resources inside one vpc can communicate with each other
- Resources cannot communicate with resources outside vpc and pubic network
	- They need to be configured to do the same
- AWS VPC is a `regional service`
- Each region has its own `default VPC` 
- EC2, Load balancers,... are resources inside a VPC
- S3 exist outside VPC

- ### Internet gateway (IGW)
	- Enables resources in VPC to access internet
	- Only one internet gateway can connect with a VPC
	- IGW is not `Avability Zone Specific` (IGW not tied to specific zone)

- ### Route Table
	- Set of rules that tell where to direct traffic to in a VPC
	- A VPC can have multiple route tables
	- A route table cannot be deleted if it has dependencies

<img src="readme_img/image.png" height=500/>

- ### Network access control list (NACL/ ACL)
	- Optional layer of security for a VPC that acts as a firewall for controlling traffic to one or more subnets
	- Default VPC has a NACL associted with default subnets
	- NACL for default VPC allows all inbound and outbound traffic
	- There can be multiple NACL in a VPC
	- All the subnets need be associted to specific NACL or else they will be assigned to default NACL
	- Rules are evaluated from the lowest to highest 
	- If first rule applies to traffic then it is executed before moving to next rules
	- Eg:
		- Rule 80 : HTTP rule : Allow
		- Rule 90 : HTTP rule : Deny
		- Now here although second rules says to deny HTTP traffic but the first rule with lower rule number will allows it 

- ### Subnets
	- It can be in only one availability zone and cannot span access zone
	- We can add one or more Subnets in a VPC in a availability zone
	- They can be either public or private
	- Public subnets have a route to the internet 
	- Private subnets cannot have a route to the internet
	- Subnets can have communication with other subnets in a VPC
	- Subnets are basically connected to a route table 
	- So a route table with private cannot have public access

<img src="./readme_img/image-1.png">

- ### Security Groups
	- They are very similar to NACLs in terms of allowing traffic, but they are found on instance level
	- And also their allow/deny rules work different from NACL 
	- They evaluate all the rules before passing the traffic
	- All the traffic is denied by default unless specifically mentioned allow

<img src="./readme_img/image-2.png">

- ### NAT gateway
	- Say the OS needs to update itself which is in private subnet
	- To give it access of internet we create a NAT gateway in the public subnet 
	- Then provide its IP to private subnet
	- This NAT gateway is a just outbound and does not accept any inbound traffic
	- A single NAT Gateway is associated with a specific Availability Zone (AZ).
	- However, it can be associated with multiple subnets, both within the same AZ and across different AZs

> ## Network interface card (NIC)
> - Present in a PC or laptop to connect it to different systems or basically internet
> ## Elastic Network interface AWS (ENI) 
> - Logical networking component in a VPC that represents a virtual network card / Virtual NIC
> ### By default each instance is connected to a Virtual NIC but if we want to attach another Virtual NIC for higher performance we can by creating additional Virtual NIC  



- Amazon CloudWatch
- DNS and CDN


