# Decisionator
#### [Team Website](http://web.csulb.edu/~jcover/cecs492/index.html)

Continuous integration status: ![Build status](https://travis-ci.org/Mosquito-Mashers/Decisionator.svg)

#### [Build History](https://travis-ci.org/Mosquito-Mashers/Decisionator/builds)

Items to be claimed in backlog for this sprint: [![Stories in Ready](https://badge.waffle.io/Mosquito-Mashers/Decisionator.png?label=ready&title=Ready)](https://waffle.io/Mosquito-Mashers/Decisionator)

Project Throughput:[![Throughput Graph](https://graphs.waffle.io/Mosquito-Mashers/Decisionator/throughput.svg)](https://waffle.io/Mosquito-Mashers/Decisionator/metrics)

[Issues](https://github.com/Mosquito-Mashers/Decisionator/issues) | [Milestones](https://github.com/Mosquito-Mashers/Decisionator/milestones) | [Metrics](https://github.com/Mosquito-Mashers/Decisionator/graphs/contributors)
___

## About
General info about the Decisionator
* What does it do?
* Who is the target audience
* what cloud aspects are there?

## Development Environment
___
What tools we use
* Android
* Android studio
* Git
* Github
* Waffle.io
* Hipchat
* GDrive
* Google App engine / amazon aws
* JUnit
* Travis CI
* Java

## Architechure
___
Different diagrams and methodologies used

## Contributing
___

How to contribute to the project

1. Clone repo into local machine
    * **git clone http://github.com/Mosquito-Mashers/Decisionator.git**
2. Build and run (master branch should be clean)
3. checkout new branch
    *  **git checkout -b {branch-name}**
          * Branch names: "name_of_feature-#issue_number"
          * ex: **git checkout -b adding_login-#12**
4. Add your changes while git adding and git committing often
    * **git checkout...**
    * Open android studio and make code changes
    * **git add -A**
    * **git commit -am "Descriptive message explaining what work was done"**
    * make more changes (maybe add more files)
    * **git add -A**
    * **git commit -am "Descriptive message explaining what work was done"**
5. If done working but the feature is not closed:
    * **git add -A**
    * **git commit -am "Descriptive message explaining what work was done"**
    * **git push origin {branch_name}**
        * Enter username and password
6. If done working and your branch closes #issue_number
    * **git add -A**
    * **git commit -am "Description of the changes Fixes #issue_number"**
        * This will close the particular issue once it is merged with master
    * **git push origin {branch_name}**
    * On Github, select your branch from the dropdown menu and click the button to create ***New Pull Request***
    * Either replace the pull requests title with ***Fixes #issue_number*** or put ***Fixes #issue_number*** in the body of the pull request
    * Leave a comment using the @mention for another team memeber to review the code

## Integration
___
Our integration strategy

The master branch is protected based on the build results from Travis CI, this way we are guaranteed a working build on the master branch. We are implementing the feature branch workflow. A developer will checkout a branch and label it in accordance with the task they plan to achieve. After they have made their changes and commited them to their checked out branch they will initiate an automated build through travis-ci.org; this is done by pushing to their checked out branch. Upon a successful build, they will initiate a pull request which will be reviewed and tested before being merged with the master branch.
