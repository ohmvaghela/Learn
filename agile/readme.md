# AGILE

## Challenges with conventional ways
- Traditionally waterfall method was used
	- Waterfall is linear approach to software development
	- Here each phase is starts after last one is finished
	- No overlapping in phases
	- A comprehensive documentation is prepared for each phase
	- Predictable timetable : timeline for each phase have set durations
- Problems with waterfall method
	- Inflexibility to changing requirements
	- High risk of project failure due to delayed feedback
	- Difficult to accomodate to customer's need
	- Lengthy development cycles
	- High documentation overhead

## AGILE
- It is a `project management` and `software developmet` approach that aims to more effectiveness
	- It focuses on delivering small pieces, rather than one large big launch
	- This allows team to adapt quickly to customer's need	
- Features of agile
	1. Iterative development
	2. Incremental delivery
	3. Flexibility
	4. Collaboration : between teams and cross functional communication
	5. Customer Involvement
	6. Continous Improvement
- AGILE Family
	- AGILE is recomended when the client requirements are evolving(but they are clear)
	- Not much focus of documentation
  	1. CRYSTAL 
		- Deliver software in short working cycles
		- Crystal is tailored to specific project and organizational requirements
		- Good for `small project`
	2. SCRUM
		- Deliver software in short cycles called `sprints`
		- Good for `mid` to `large` project  
		- Terminologies
			- Sprints : Timed box iterations 2-4 weeks
			- Daily SCRUM : Team meeting to discuss progress and plans
			- SCRUM Master : Facilitates SCRUM and remove impurities
	3. DSDM (Dynamic systems Development method)
		- same as other ass-holes
		- Good for large `projects`
		- Consist of 3 phases
			1. Feasibility Study : Access project viability
			2. Business Study : Define business requirements
			3. Technical Development: Develop the software
- Non AGILE
	- SPIRAL
		- Spiral is a risk-driven software development methodology
		- It can handle changing requirements but is more structured than pure prototyping
		- focuses on addressing risks and uncertainties in the development process.
		- 4 Phases
			- Planning: Identify and prioritize risks
			- Risk Analysis: Analyze and mitigate risks
			- Engineering: Develop the software
			- Evaluation: Review and refine the software 
## Prototype Methodology (Other methodology then AGILE)
- When client requirement are not clear

## Terminologies in AGILE
> ### Configuration Items(CIs)
> - components that need to be managed in order to deliver a product or service
- Management
  	- Overall pocess handling, managing workflow
- Check-in
	- When developer submit or checkin code to Version control system (VCS) like `Github`
- BaseLine
	- Represent a referecnce point in a project like a stable software version
- Check-out
	- Retreving latest code from the VCS like github to stay updated
- Configuration Database (CMDB)
	- Repo that stores info about all the assets like software, hardware, docs etc...
- SVN (SubVersion)
	- Version Control like Github
- Audit
	- Reviewing code to ensure compliance with policies, standards, and procedures.
- Testing Report
	- Results of tests including what was testing, what were outcomes and what were issues
- Design Document
- Software Requirement Specifications (SRS)
- Source Code Document

## Different development methodology
- Lean software development
	- Env where efficiency, resources are critial
	- cohesiveness of the team and individual commitment of the team
- Feature driven develop
	- Large Scale projects
	- Client Centric
- Extreme Programming (XP)
	- Rapid Chaging env
- SCRUM
	- Project requirement are flexible and evloving 

## Kanban
- Visualizing workflow
- Limiting work in progress : Minimum backlogs
- Make Process Policies Explicit : Clear defination of rules and policies


---
# DevSecOps
## C.A.M.S model
- Culture Automation Measurement Sharing
1. Culture
	- Communication btwn Developers, QAs and Operations team
2. Automation
	- Automating pipeline like testing, integration, deployment and monitering
3. Measuremnt
	- Measure : Lead time, deployment frequency, change failure rate, average time to recovery, average time between failures, defect escape rate
4. Sharing
	- Collaboration, feedback

## Blue green Deployment
- There are two running instances blue instance and green instance
- First say blue is handling all the users and green will be sitting idle
- If there is an update we update green server and gradually shift users to green server
- Now the blue server will be sitting idle like a backup server
- If there is a new update, we will make changes to idle server and then gradually shift users 

# Testing terminologies
## SAST (Static Application Security Test)
- White box testing
- Analyze source code, bytecode, binary code without executing
- Detects SQL injection, cross-site scripting(XSS)

## DAST (Dynamic Application Security Testing)
- black box testing
- Analyze running application
- Simulates attacks on application

## SCA (Software Composition Analysis)
- Manages open source components
	- Like security, licensing, and quality of open source code

## Container Security
- Security of container
