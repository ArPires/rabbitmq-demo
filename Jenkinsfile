pipeline {

	environment {
		CONSUMER_VERSION
	}
	
	//Stage 1: Checkout Code from Git
	stage('Application Code Checkout from Git') {
		checkout scm
	}
	
	//Stage 2: Test Consumer code with maven
	stage('Test Consumer MS with maven') {
		container('maven') {
			dir("./consumer") {
				sh ("mvn test")
			}
		}
	}
	
	stage('Build Consumer code') {
		container('maven') {
			dir("./consumer") {
				sh ("mvn clean package")
			}
		}
	}
	
}