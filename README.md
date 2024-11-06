# README - COSI 12b Programming Assignment 8 

The code provided in this repository contains the solution to PA8 for COSI 12b - Advanced Programming Techniques in Java. The goal of this assignment was to gain practice in using HashMap, HashSet, and ArrayList. As this assignment was done for a class, we were limited to using only certain existing Java libraries/features in our program. 

## Installation and Execution 

Get the files from GitHub and open your desired application to compile and run Java code. My preferred one is Eclipse. Import the files, and run the test file, which is under the name ``` Test1.java ```. Normally, I prefer running the program through my computer's terminal, but I was having issues setting it up so that the JUnit test would run, which is why I recommend running it through an application instead. This would allow the user to run the test file included to see that it is passing the test cases provided when we were initially given the assignment. 

However, if the user would like to run the program itself and not just the test file provided they are also able to do so. To compile the files with the necessary code, they can navigate into the src folder of the project through their terminal/console and run the following line: 

``` javac TA*.java ```

After compiling, start the program with the following line: 

``` java TAChecker ```

The console will then prompt the user for a work log, where they will then enter the name of a .txt file that contains the data they wish to provide. Some test case files have been included in the assignment upload, so the user is free to use these if they wish. For the program to accept the .txt files that the user wishes to use, the file should be included/created in the src folder of the project for accessing. 


## Problem Description 

The program is used to detect fraudulent TA Timesheet Submissions for a 'Computer Science Bootcamp'. The 'bootcamp' consists of many students and relies heavily on TAs to be able to help the students. To ensure that the TA are being both helpful and fast, they are required to submit a timesheet entry for each student they help. After looking at timesheet submissions, it is suspected some TAs are under-reporting the amount of time taken to help a student. While the bootcamp doesn't want the TAs to feel as though they're being evaluated by how fast they're helping the students, the timeframes submitted is still an important metric to gauge so that content/course planning is best fitted to what will work best for the students. To make sure students are getting the help they need without wasting funds is then where catching cases of "fraudulent" TA timesheet reports comes into play. 

### Billing and Reporting Process 

The Computer Science Bootcamp has separate systems for job tracking and billing: 

* TAs indicate when they start helping a student by submitting a job start notice to the job tracking system. 
* At any time while helping a student, the TA enters a single invoice into the billing system. The system then gives back a unique, increasing numeric invoice ID. 
* Upon completion, TAs submit their invoice IDs to job tracking, which records that the jobs were completed. A TA must complete and submit all open jobs together (other students could've shown un while a TA is helping another student, and they must finish helping all students present before submitting their invoice IDs). 

### Input 

The user will receive/submit input as strings in the form of .txt files. Each string will be on its own line and correspond to one event each. These events are already sorted in terms of time, and take one of the two following forms: 

1. Job start events that the form <TA_NAME>;START 
    * The TA_NAME is a unique identifier for the TA, and is guaranteed not to contain a semicolon 

2. Job completion events take the form <CONTRACTOR_NAME>;<INVOICE_ID>(,<INVOICE_ID>)* 
    * The TA_NAME is the same unique identifier for the TA as before 
    * INVOICE_IDs are integer values, guaranteed to fit within the value of an integer 
    * If a TA has multiple jobs started, then they will complete and submit invoice IDs for all started jobs as a single job completion event. These invoice IDs will be commandeliminated, one invoice ID per job start event. These are referred to as "batch job completions". For example, if a TA has started helping three students, then the next job completion from that TA is guaranteed to consist of three distinct invoice IDs. We don't know which invoice number corresponds to which job start event. We assume that input will always be well-formed and contain no extra characters or whitespace. 

### Detecting Fraud 

There are two kinds of fraudulent submissions being detected by the program: shortened jobs and suspicious batches. Furthermore, the program is also able to detect unstarted jobs, which are less serious than fraudulent submissions. 

#### Unstarted Jobs 

Unstarted jobs are exactly as they sound - jobs where a job completion event/invoice ID are created, but there is no prior log of the event having been started. For example, take the following event log: 

Kelly;START
Theo;START
Theo;10 
Kelly;12 
Danny; 20

In the above case, Danny could not have submitted an invoice ID if he never submitted a job start notice. In this situation, the program would return the following:

5;Danny;UNSTARTED_JOB 

#### Shortened Jobs 

Shortened jobs are job start events which, given their later submitted invoice ID, must have been submitted artificially late. These are identified as jobs with an invoice ID smaller than any invoice ID submitted before their start. For example, take the following event log: 

Nick;START 
Nick;24 
Dan;START 
Dan;18 

Since the invoice ID of Nick is 24, while the invoice ID of Dan is 18, this indicates that Dan's job was actually started before Nick's. Therefore, Dan's start event on the third line is classified as a shortened job, and the program would return the following: 

3;Dan;SHORTENED_JOB

#### Suspicious Batches 

Suspicious batches are batch job completions which, given their invoice IDs and their associated start events, must contain at least one shortened job. For example, consider the following event log: 

Leah;START 
Leah;10 
Alice;START 
Alice;START 
Alice;8,14 

According to the work log, first Leah started and finished helping a student with an invoice ID of 10. Then, Alice starts 2 jobs, gets invoice IDs 8 and 14 for the two jobs, and submits a batch job completion with those invoice IDs. However, since Alice's ID of 8 is lower than Leah's ID of 10, then this means one of her jobs must have started before Leah's job. Therefore, Alice's batch job completion on the fifth line is a suspicious batch, because at least one of the start events should have been submitted before Leah's job completion. Since only one is flagged as a shortened job, we are unable to identify which of the jobs was shortened, so neither can be flagged as a shortened job, and they must be flagged as a suspicious batch instead. If both of Alice's IDs were below 10, then both jobs would be flagged as shortened jobs, rather than as a suspicious batch. Due to this, the event log above would return the following: 

5;Alice;SUSPICIOUS_BATCH 

### Output 

The program returns a string array that indicates the set of submissions that are possibly fraudulent/unstarted based on the criteria above. For each suspicious behavior identified, the string array returned contains a line of the form <LINE_NUMBER>;<TA_NAME>;<VIOLATION_TYPE>, as seen in the above examples. 

* LINE_NUMBER is a (one-indexed) line number from the input. 
    * For SUSPICIOUS_BATCH, this is the line on which the batch job completion occurred. 
    * For SHORTENED_JOB, this is the line on which the job start occurred.
    * For UNSTARTED_JOB, this is the line on which the job completion occurred. 
* TA_NAME is the unique identifier of the offending TA. 
* VIOLATION_TYPE is a string that matches either SUSPICIOUS_BATCH, SHORTENED_JOB, or UNSTARTED_JOB. This indicates which patter has been identified, following the rules previously defined. Violations may be presented in any order. 
* There are four possible outputs for any test case, which are the following: 
    * No Output - this is when every TA reports correct hours 
    * SHORTENED_JOB - this is when a shortened job has been identified 
    * SUSPICIOUS_BATCH - this is when a suspicious batch has been identified 
    * UNSTARTED_JOB - this is for when an unstarted job has been identified 

## Implementation 

There are two separate .java files/classes being used to implement this program: TARecord and TAChecker. 

### TARecord 

This class is used to store start and end records with their associated invoice IDs for an individual TA after reading in the .txt file passed in. A base/skeleton of the class was provided with a simple constructor that takes in a String, and constructs a new TARecord for the specific TA whose name is passed in. The following has be done to provide further needed functionality to complete the assignment: 

* Determine how to best store each start and end record (aka which data structure(s) to use). The records must be accessible as they will be retrieved again at a later time to determine whether fraud has been committed. 
    * My submission of the assignment utilizes the Java HashMap and ArrayList data structures to do so. 

### TAChecker 

This is the main class that checks all the TA work logs for instances of fraud. The following methods are implemented to implement functionality of the program for assignment completion: 

* void sortWorkLog() - this method scans through the .txt files and creates a TARecord for each unique TA 
* void checkValidity() - this method goes through each TARecord, detects whether the TA has committed fraud, and prints one of the four appropriate messages according to the result 

While the two previously mentioned methods were required for the assignment, my solution to the problem includes a few other methods to break down the work of checking for the overall validity of the TARecords. I include the following methods to assist in checking for the different possible cases of fraudulence: 

* void checkShortened(TARecord r1, TARecord r2) - this method compares two TA records to each other to find shortened jobs and checks for suspicious batches 
* boolean checkAllShortened(ArrayList<Integer> invID, int invComp) - this method compares all invoices in an ArrayList to a separate invoice provided, returning true if all invoices are less that the one they are being compared to and false once one is greater than the one they are being compared to (this is for when all jobs in a batch a shortened, thus requiring all jobs be flagged as SHORTENED_JOB, rather than the entire batch being flagged as SUSPICIOUS_BATCH)
* void checkUnstarted(TARecord r) - this method checks if a TA marks a job as completed when they had not previously indicated/logged that it was starting 
* boolean checkSuspicious(ArrayList<Integer> invID, int invComp) - this method compares all invoices in an ArrayList to a separate invoice provided, returning true if invComp falls between invoices in the List and false if it doesn't 