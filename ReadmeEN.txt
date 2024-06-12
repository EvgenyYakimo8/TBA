♦ "Transfer to a bank account" ♦
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
The program was implemented according to the technical specifications - as the first project during training. Among the complications, the output of the report file data for the interval and the floating-point sum "double" has been added

HOW DOES IT WORK
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
After starting the program and working through step 1, the transfer.txt file (there may be several of them and with different names, but always with the .txt extension) is moved from the src\files\inputPack folder to the src\files\archivePack folder
The accounts.txt file in the src\files\accountsPack folder will be updated.
The files are made to work out all functions without nuances and errors. After testing, it is recommended to return the original data to simplify the subsequent demonstration of work without various messages from the program coming from the checks embedded in it.
For a convenient return to the original data, the Original_files_to_tests folder was created in the project root - it stores the original files for a convenient rollback to the beginning in case of active testing.
To return to the original data, move the transfer.txt file from the archivePack folder to the inputPack folder, and inside the accounts.txt file, replace the data with the original data from the same file in the Original_files_to_tests folder).


SHORT DESCRIPTION
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
The files folder contains all the folders and files necessary for work.
Folders fileController, operationAccount, service - classes.
At the root of the program I left: technical specifications for development, a class diagram and this Readme file.

After launching the program shows the control panel.
1. Parsing input data files and performing translations;
2. Output of all data in the report file;
3. Output of all data in the report file for the period;
4. Show current account data (Personal data!);
5. Display a list of all checks implemented in the program;
6. Exit the program.
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
1. Parsing first checks the files in the inputPack folder for the presence of .txt files, if it finds other file extensions, it moves them to the spamPack folder.

Reads input files and a file storing invoices and their amounts. Numerous checks and protections are performed simultaneously (the presence of files, whether they are empty, etc.).

The validator takes specific lines from documents and filters out (adds to the ignore list and then deletes) obviously false and/or erroneous translations, for example, removes duplicates
or looks to see if such accounts exist at all, or if during the transfer there are no funds on the sending account, such an account is added to the ignore list ignoreList.

At this stage, the lists with transfers have been validated - we remove accounts located in ignoreList from the transfersList list and credit the funds
- there are also numerous checks for results with protection against negative balance. Here we take the data for the report file.

We record updated accounts with amounts in accounts.txt.

Add entries to the report file outputLogger.txt.

Move the input files to the archivePack folder.

Each step is accompanied by various checks to avoid errors.
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
2. A simple method of reading from a file with checks and subsequent output to the console (checks are more complicated than implementing console output).
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
3. Shows the time period selection panel. We read the data according to the principle of paragraph 2, but additionally select the date from the string using the isInTheRange() method
we check whether the line is included in the time range selected in the control panel, if not, we read the next one, if it is, we add it to the buffer list for subsequent output to the console.
-------------------------------------------------- -------------------------------------------------- -------------------------------------------------- -------------------------------------------------- ----------------------------
4. A simple method of reading from a file with checks and subsequent output to the console