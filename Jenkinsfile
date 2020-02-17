pipeline {

	agent any
	tools {
		maven 'Maven 3.3.9'
	}
	
	stages {
		//Stage 1: Checkout Code from Git
		stage('Application Code Checkout from Git') {
			steps{
				checkout scm
			}
		}
		
		//Stage 2: Test Consumer code with maven
		stage('Test Consumer MS with maven') {
			steps {
				dir("./consumer") {
					sh ("mvn test")
				}
			}
		}
		
		stage('Build Consumer code') {
			steps {
				dir("./consumer") {
					sh ("mvn clean package")
				}	
			}
		}
	}
}